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
 * Type performing the actual crawling. Crawler implements Runnable and thus
 * qualifies to run in a thread pool. This object calls the methods defined by
 * the {@link ICrawlBehavior} Interface at the respective stages.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
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
	private PageExtractionType extractionType;

	public Crawler(IScheduledURLStore scheduledUrls, IProcessedURLStore processedUrls, IRobotTxtStore robotTxtData,
			ICrawlBehavior customCrawlBehavior, IRobotTxtBehavior robotTxtBehavior,
			IHttpConnectionManager httpConnectionManager, PageExtractionType extractionType)
	{
		super();
		this.scheduledUrls = scheduledUrls;
		this.processedUrls = processedUrls;
		this.robotTxtData = robotTxtData;
		this.customCrawlBehavior = customCrawlBehavior;
		this.robotTxtBehavior = robotTxtBehavior;
		this.httpConnectionManager = httpConnectionManager;
		this.extractionType = extractionType;
	}

	@Override
	public void run()
	{
		this.execute();
	}

	/**
	 * Internal wrapper method called by the {@link Runnable.run()}. method.
	 */
	protected void execute()
	{
		URLWrapper url = null;
		boolean shouldRun = true;
		int schedulerReturnedNullCounter = 0;

		while (shouldRun)
		{
			url = this.scheduledUrls.getNextURL();

			if (url != null)
			{
				if (!this.processedUrls.isCurrentlyProcessedUrl(url) && !this.processedUrls.wasProcessed(url))
				{
					// TODO: Possible race condition
					this.processedUrls.addCurrentlyProcessedUrl(url);

					if (!this.robotTxtData.containsRule(url))
					{
						this.robotTxtBehavior.fetchRobotTxt(url, this.httpConnectionManager.getHttpClient(),
								this.robotTxtData);
					}
					if (this.robotTxtData.allows(url))
					{
						int siteDelay = this.robotTxtData.getDelay(url);
						Long lastProcessing = this.processedUrls.lastHostProcessing(url);
						Long timeStamp = 0l;
						if (lastProcessing != null)
						{
							if (siteDelay != -1)
							{
								timeStamp = lastProcessing + (siteDelay * 1000);
							}
							else
							{
								timeStamp = lastProcessing + ConfigSingleton.INSTANCE.getPolitnessDelay();
							}
						}

						if (timeStamp <= System.currentTimeMillis())
						{
							try
							{
								this.crawlPage(url);
							}
							catch (InputStreamPageExtractionException e)
							{
								LOG.error("Error in the extraction process", e);
							}

							// Setting the politeness Timestamp for future
							// access to the host
							this.processedUrls.addProcessedHost(url, System.currentTimeMillis());
							this.processedUrls.addProcessedURL(url);
							this.processedUrls.removeCurrentlyProcessedUrl(url);
						}
						else
						{
							this.scheduledUrls.scheduleURL(url);
							this.processedUrls.removeCurrentlyProcessedUrl(url);
						}
					}
				}
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

	/**
	 * Internal Method extracting the content from the provided URL.
	 * 
	 * Depending on the configuration the page gets extracted by stream or by
	 * returning a String:</br> -{@link PageExtractionType#BY_STREAM}</br> -
	 * {@link PageExtractionType#BY_STRING}
	 * 
	 * @param {@link URLWrapper}
	 * @throws {@link InputStreamPageExtractionException}
	 */
	protected void crawlPage(URLWrapper url) throws InputStreamPageExtractionException
	{

		Page page = null;
		HttpGet request = null;
		CloseableHttpResponse response = null;
		int pageStatus = -1;

		try
		{
			if (this.httpClient == null) this.httpClient = this.httpConnectionManager.getHttpClient();

			// Initializing a page object
			page = new Page();
			page.setUrl(url);

			// Making the request
			request = new HttpGet(url.toString());
			request.setProtocolVersion(HttpVersion.HTTP_1_1);
			response = (CloseableHttpResponse) this.httpClient.execute(request);

			// Handling the returned status code
			pageStatus = response.getStatusLine().getStatusCode();
			page.setStatusCode(pageStatus);
			

			if (pageStatus == HttpStatus.SC_NOT_FOUND)
			{
				this.handleStatusCode(page);
				// If user decides to stop because of the status code so may it be.
				if (!page.isContinueProcessing()) return;
			}
			else if ((pageStatus == HttpStatus.SC_MOVED_TEMPORARILY) || (pageStatus == HttpStatus.SC_MOVED_PERMANENTLY))
			{
				Header header = response.getFirstHeader("Location");
				if (header != null)
				{
					url.setRedirectedFrom(url.toString());
					url.setUrl( header.getValue().startsWith("/") ? 
							URLCanonicalizer.getCanonicalURL(
									header.getValue(),url.getProtocol() + "://" + url.getDomain()).toExternalForm():
										URLCanonicalizer.getCanonicalURL(header.getValue()));
					
					if (ConfigSingleton.INSTANCE.isFollowRedirect())
					{
						this.scheduledUrls.scheduleURL(url);
					}
					
					this.handleStatusCode(page);
					// If user decides to stop because of the status code so may it be.
					if (!page.isContinueProcessing()) return;
				}
			}
			else if (pageStatus == HttpStatus.SC_OK)
			{
				
				this.handleStatusCode(page);
				// If user decides to stop because of the status code so may it be.
				if (!page.isContinueProcessing()) return;
				
				// Getting the content
				HttpEntity entity = response.getEntity();

				if (entity != null)
				{
					page.setContentLength(entity.getContentLength());
					this.handleContentSize(page);
					if (!page.isContinueProcessing()) return;

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

	/**
	 * Internal Method extracting the content as an InputStream in case the
	 * configuration specifies:</br> -{@link PageExtractionType#BY_STREAM}
	 * 
	 * @param {@link Page}
	 * @param {@link HttpEntity}
	 * @throws {@link InputStreamPageExtractionException}
	 */
	protected void crawlPageToInputStream(Page page, HttpEntity entity) throws InputStreamPageExtractionException
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
							LOG.error("IOException in while closing the stream in parsing thread: crawlToInputStream",
									e);
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

			if (!page.isContinueProcessing()) return;

			// Set the urls extracted from the page, and schedule them by
			// user code.
			page.setOutgoingURLs(tHandler.getLinks());
			this.handleOutgoingUrls(page);

			if (!page.isContinueProcessing()) return;

			this.scheduleURLs(page);

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

	/**
	 * Internal Method extracting the content as a String in case the
	 * configuration specifies:</br> -{@link PageExtractionType#BY_STRING}
	 * 
	 * @param {@link Page}
	 * @param {@link HttpEntity}
	 * @throws {@link InputStreamPageExtractionException}
	 */
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

			this.processPage(page);

			if (!page.isContinueProcessing()) return;

			// The outgoing URLs would be available already. But to have a
			// consistent behavior
			// they are kept hidden until now.
			page.setOutgoingURLs(links.getLinks());
			this.handleOutgoingUrls(page);

			if (!page.isContinueProcessing()) return;

			this.scheduleURLs(page);
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
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#handleStatuScode(Page)}.
	 * 
	 * @param page
	 */
	protected void handleStatusCode(Page page)
	{
		this.customCrawlBehavior.handleStatuScode(page);
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#handleContentSize(Page)}.
	 * 
	 * @param page
	 */
	protected void handleContentSize(Page page)
	{
		this.customCrawlBehavior.handleContentSize(page);
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#processPage(Page)}.
	 * 
	 * @param page
	 */
	protected void processPage(Page page)
	{
		this.customCrawlBehavior.processPage(page);
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#handleOutgoingURLs(Page)}.
	 * 
	 * @param page
	 */
	protected void handleOutgoingUrls(Page page)
	{
		this.customCrawlBehavior.handleOutgoingURLs(page);
	}

	/**
	 * Method to schedule the URLs from the page. This method calls
	 * {@link ICrawlBehavior#processPage(Page page)} as last instance.
	 * 
	 * @param page
	 */
	protected void scheduleURLs(Page page)
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

					if (this.customCrawlBehavior.shouldScheduleURL(cUrl))
					{
						this.scheduledUrls.scheduleURL(cUrl);
					}
				}
			}
		}
	}

}
