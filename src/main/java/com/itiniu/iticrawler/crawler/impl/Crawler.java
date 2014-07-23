package com.itiniu.iticrawler.crawler.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.io.input.TeeInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.PageExtractionType;
import com.itiniu.iticrawler.exceptions.InputStreamPageExtractionException;
import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLCanonicalizer;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.httptools.inte.IHttpConnectionManager;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

/**
 * Crawler. Objects of this class are used to run inside crawling threads. They
 * do the page processing and the scheduling.
 * 
 * @author esquive
 */
public class Crawler implements Runnable
{
	// Getting the logger
	private static final Logger LOG = LogManager.getLogger(Crawler.class);

	// The Data holders
	private IScheduledURLStore scheduledUrls = null;
	private IProcessedURLStore processedUrls = null;
	private IRobotTxtStore robotTxtData = null;

	// The Behaviors
	private ICrawlBehavior customCrawlBehavior = null;
	private IRobotTxtBehavior robotTxtBehavior = null;

	// The HttpTools
	private HttpClient httpClient = null;
	private IHttpConnectionManager httpConnectionManager = null;

	// Crawler relevant variables
	private boolean busy = false;
	private PageExtractionType extractionType;

	public Crawler()
	{
		
	}
	
	@Override
	public void run()
	{
		this.execute();
	}

	private void execute()
	{
		URLWrapper cUrl = null;
		boolean shouldRun = true;
		int schedulerReturnedNullCounter = 0;

		while (shouldRun)
		{
			cUrl = this.scheduledUrls.getNextURL();

			if (cUrl != null)
			{
				this.busy = true;

				if (!this.processedUrls.isCurrentlyProcessedUrl(cUrl) && !this.processedUrls.wasProcessed(cUrl))
				{	//TODO: Possible race condition
					this.processedUrls.addCurrentlyProcessedUrl(cUrl);

					if (!this.robotTxtData.containsRule(cUrl))
					{
						this.robotTxtBehavior.fetchRobotTxt(cUrl, this.httpConnectionManager.getHttpClient(),
								this.robotTxtData);
					}
					if (this.robotTxtData.allows(cUrl))
					{
						// Getting a timeStamp to determine if I can request
						// the host again
						long timeStamp = this.processedUrls.lastHostProcessing(cUrl)
								+ ConfigSingleton.INSTANCE.getPolitnessDelay();

						if (timeStamp <= System.currentTimeMillis())
						{
							try
							{
								this.crawlPage(cUrl);
							}
							catch (InputStreamPageExtractionException e)
							{
								LOG.error("Error in the extraction process", e);
							}

							// Setting the politeness Timestamp for future
							// access to the host
							this.processedUrls.addProcessedHost(cUrl, System.currentTimeMillis());
							this.processedUrls.addProcessedURL(cUrl);
							this.processedUrls.removeCurrentlyProcessedUrl(cUrl);
						}
						else
						{
							this.scheduledUrls.scheduleURL(cUrl);
							this.processedUrls.removeCurrentlyProcessedUrl(cUrl);
						}
					}
				}

				this.busy = false;
			}
			else
			{
				schedulerReturnedNullCounter++;
				if (schedulerReturnedNullCounter == 10)
				{
					shouldRun = false;
				}
			}
		}// End of the while loop
	}

	public void crawlPage(URLWrapper url) throws InputStreamPageExtractionException
	{

		Page page = null;
		HttpGet request = null;
		CloseableHttpResponse response = null;
		int pageStatus = -1;

		try
		{
			// Initializing a page object
			page = new Page();
			page.setUrl(url);

			// Making the request
			request = new HttpGet(url.toString());
			request.setProtocolVersion(HttpVersion.HTTP_1_1);
			response = (CloseableHttpResponse) this.httpClient.execute(request);
			
			// Handling the returned statuscode
			pageStatus = response.getStatusLine().getStatusCode();
			page.setStatusCode(pageStatus);

			if (pageStatus == HttpStatus.SC_NOT_FOUND)
			{
				LOG.info("URL not found: " + url.toString());
			}
			else if ((pageStatus == HttpStatus.SC_MOVED_TEMPORARILY) || (pageStatus == HttpStatus.SC_MOVED_PERMANENTLY))
			{
				Header header = response.getFirstHeader("Location");
				if (header != null)
				{
					url.setRedirectedFrom(url.toString());
					url.setUrl(URLCanonicalizer.getCanonicalURL(header.getValue()));
					if (ConfigSingleton.INSTANCE.isFollowRedirect())
					{
						this.scheduledUrls.scheduleURL(url);
					}
				}
			}

			this.customCrawlBehavior.handleStatuScode(page);

			if (pageStatus == HttpStatus.SC_OK)
			{
				if (page.isContinueProcessing())
				{

					// Getting the content
					HttpEntity entity = response.getEntity();
					//TODO: set The length of the content so the user can decide to use it or not. 
					
					if (entity != null)
					{
						// According to what Extraction the user wants we
						// process the page accordingly
						if (this.extractionType == PageExtractionType.BY_STREAM)
						{
							this.crawlPageToInputStream(page, entity);
						}
						else if (this.extractionType == PageExtractionType.BY_STRING)
						{
							this.crawlPageToString(page, entity);
						}

					}
					else
					{
						throw new InputStreamPageExtractionException("Error in the Http request process");
					}
				}
			}

		}
		catch (ClientProtocolException e1)
		{
			LOG.error("ClientProtocolException during the crawl process", e1);
		}
		catch (IOException e2)
		{
			LOG.error("IOException during the crawl process", e2);

		}
		finally
		{
			try
			{
				if (response != null) response.close();
			}
			catch (IOException e)
			{
				LOG.error("IOException in the crawlPage method.", e);
			}
		}
	}

	protected void crawlPageToInputStream(Page page, HttpEntity entity)
			throws InputStreamPageExtractionException
	{
		InputStream pageStream = null;
		InputStream htmlStream = null;
		URLWrapper url = page.getUrl();

		try
		{
			pageStream = new PipedInputStream();
			final InputStream tHtmlStream = new BufferedInputStream(new TeeInputStream(entity.getContent(),
					new PipedOutputStream((PipedInputStream) pageStream), true));

			final LinkContentHandler tHandler = new LinkContentHandler();

			// Do all the document parsing here links and htmlContent
			final Metadata metadata = new Metadata();
			metadata.add(Metadata.CONTENT_LOCATION, url.toString());
			metadata.add(Metadata.RESOURCE_NAME_KEY, url.toString());

			final HtmlParser parser = new HtmlParser();

			// Parsing the html for the urls in a separate thread
			Thread t = new Thread(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						parser.parse(tHtmlStream, tHandler, metadata, new ParseContext());
					}
					catch (IOException e)
					{
						LOG.error("IOException in parsing thread: crawlToInputStream", e);
					}
					catch (SAXException e)
					{
						LOG.error("SAXException in parsing thread: crawlToInputStream", e);
					}
					catch (TikaException e)
					{
						LOG.error("TikaException in parsing thread: crawlToInputStream", e);
					}
					finally
					{
						try
						{
							tHtmlStream.close();
						}
						catch (IOException e)
						{
							LOG.error(
									"IOException in while closing the stream in parsing thread: crawlToInputStream", e);
						}
					}
				}
			});
			t.start();

			// Set the stream of the page and process it by user code
			page.setInStream(pageStream);
			this.processPage(page);

			// Waiting for the processing thread to finish
			t.join();
			htmlStream = tHtmlStream;

			if (page.isContinueProcessing())
			{
				// Set the urls extracted from the page, and schedule them by
				// user code.
				page.setOutgoingURLs(tHandler.getLinks());
				this.scheduleURLs(page);
			}
		}
		catch (IOException e)
		{
			LOG.error("IOException during the crawlToInputStream method", e);
		}
		catch (InterruptedException e)
		{
			throw new InputStreamPageExtractionException(
					"Error in the page processing thread in the crawlToInputStream method.", e);
		}
		finally
		{
			try
			{
				if (pageStream != null) pageStream.close();
				if (htmlStream != null) htmlStream.close();
			}
			catch (IOException e)
			{
				LOG.error("IOException in while closing the streams: crawlToInputStream", e);
			}

		}
	}

	protected void crawlPageToString(Page page, HttpEntity entity)
	{
		URLWrapper url = page.getUrl();
		LinkContentHandler links = null;
		ContentHandler html = null;
		TeeContentHandler teeHandler = null;
		HtmlParser parser = null;

		try
		{
			links = new LinkContentHandler();
			html = new ToHTMLContentHandler();
			teeHandler = new TeeContentHandler(links, html);

			// Do all the document parsing here links and htmlContent
			final Metadata metadata = new Metadata();
			metadata.add(Metadata.CONTENT_LOCATION, url.toString());
			metadata.add(Metadata.RESOURCE_NAME_KEY, url.toString());

			parser = new HtmlParser();

			parser.parse(entity.getContent(), teeHandler, metadata, new ParseContext());

			page.setHtml(html.toString());
			page.setOutgoingURLs(links.getLinks());

			this.processPage(page);

			if (page.isContinueProcessing())
			{
				// Set the urls extracted from the page, and schedule them by
				// user code.
				this.scheduleURLs(page);
			}
		}
		catch (IOException e)
		{
			LOG.error("IOException in the crawlToString method", e);
		}
		catch (SAXException e)
		{
			LOG.error("SAXException in the crawlToString method", e);
		}
		catch (TikaException e)
		{
			LOG.error("TikaException in the crawlToString method", e);
		}
		finally
		{

		}
	}

	/**
	 * @param page
	 */
	private void processPage(Page page)
	{
		this.customCrawlBehavior.processPage(page);
	}

	/**
	 * @param page
	 */
	private void scheduleURLs(Page page)
	{
		for (URLWrapper cUrl : page.getOutgoingURLs())
		{
			if (!this.processedUrls.wasProcessed(cUrl) && !this.processedUrls.isCurrentlyProcessedUrl(cUrl)
					&& this.processedUrls.canCrawlHost(cUrl, ConfigSingleton.INSTANCE.getMaxHostsToCrawl()))
			{

				if (((page.getUrl().getUrlDepth() + 1) * -1) <= ConfigSingleton.INSTANCE.getMaxCrawlDepth())
				{
					cUrl.setUrlDepth(page.getUrl().getUrlDepth() + 1);
					cUrl.setParentURL(page.getUrl());

					if (this.customCrawlBehavior.shouldScheduleURL(page, cUrl))
					{
						this.scheduledUrls.scheduleURL(cUrl);
					}
				}
			}
		}
	}

	// -----------Getters and Setters--------------------//

	public void setCustomCrawlBehavior(ICrawlBehavior customCrawlBehavior)
	{
		this.customCrawlBehavior = customCrawlBehavior;
	}

	public void setHttpConnectionManager(IHttpConnectionManager httpConnectionManager)
	{
		this.httpConnectionManager = httpConnectionManager;
	}

	public void setRobotTxtBehavior(IRobotTxtBehavior robotTxtBehavior)
	{
		this.robotTxtBehavior = robotTxtBehavior;

	}

	public void setScheduledUrlsData(IScheduledURLStore scheduledUrls)
	{
		this.scheduledUrls = scheduledUrls;
	}

	public void setProcessedUrlsData(IProcessedURLStore processedUrls)
	{
		this.processedUrls = processedUrls;
	}

	public void setRobotTxtData(IRobotTxtStore robotTxtData)
	{
		this.robotTxtData = robotTxtData;
	}

	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}

	public boolean isBusy()
	{
		return this.busy;
	}

	public void setExtractionType(PageExtractionType extractionType)
	{
		this.extractionType = extractionType;
	}

	public PageExtractionType getExtractionType()
	{
		return this.extractionType;
	}

}
