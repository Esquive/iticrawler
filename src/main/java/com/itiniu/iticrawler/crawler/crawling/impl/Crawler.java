package com.itiniu.iticrawler.crawler.crawling.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.itiniu.iticrawler.crawler.frontier.inte.IFrontier;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
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

import com.itiniu.iticrawler.crawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.crawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.util.PageExtractionType;
import com.itiniu.iticrawler.util.exceptions.InputStreamPageExtractionException;
import com.itiniu.iticrawler.httptools.impl.URLCanonicalizer;
import com.itiniu.iticrawler.httptools.inte.IHttpConnectionManager;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

/**
 * Type performing the actual crawling. Crawler implements Runnable and thus
 * qualifies to run in a thread pool. This object calls the methods defined by
 * the {@link ICrawlBehavior} Interface at the respective stages.
 *
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
@SuppressWarnings("JavadocReference")
public class Crawler implements Runnable
{
	// Getting the logger
	private static final Logger LOG = LogManager.getLogger(Crawler.class);

	//Frontier
	private IFrontier frontier;

	//RobotTxt
	private IRobotTxtStore robotTxtData = null;
	private IRobotTxtBehavior robotTxtBehavior = null;

	// The Behaviors
	private ICrawlBehavior customCrawlBehavior = null;

	// The HttpTools
	private HttpClient httpClient = null;
	private IHttpConnectionManager httpConnectionManager = null;

	// Crawler relevant variables
	private PageExtractionType extractionType;

	public Crawler(IFrontier frontier, IRobotTxtStore robotTxtData,
			ICrawlBehavior customCrawlBehavior, IRobotTxtBehavior robotTxtBehavior,
			IHttpConnectionManager httpConnectionManager, PageExtractionType extractionType)
	{
		super();
		this.frontier = frontier;
		this.customCrawlBehavior = customCrawlBehavior;
		this.robotTxtData = robotTxtData;
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
	private void execute()
	{
		URLInfo url;
		boolean shouldRun = true;
		int schedulerReturnedNullCounter = 0;

		//TODO: Avoid busy waiting
		while (shouldRun)
		{
			//Init the HttpClient
			if(this.httpClient == null) this.httpClient = this.httpConnectionManager.getHttpClient();

			url = this.frontier.getNextURL();

			if (url != null)
			{
				LOG.debug("Loading URL: " + url.toString() );

				if (!this.frontier.isURLCurrentlyCrawled(url) && !this.frontier.wasURLCrawled(url));
				{
					// TODO: Possible race condition
					this.frontier.addCurrentlyCrawledURL(url);

					if (!this.robotTxtData.containsRule(url))
					{
						LOG.debug("Fetching robots.txt for url: " + url.getDomain() );

						this.robotTxtBehavior.fetchRobotTxt(url, this.httpClient,
								this.robotTxtData);
					}

					if (this.robotTxtData.allows(url))
					{
						int siteDelay = this.robotTxtData.getDelay(url);
						Long lastProcessing = this.frontier.getLastHostProcessingTimeStamp(url);
						Long timeUntil = 0l;
						if (lastProcessing != null)
						{
							if (siteDelay != -1)
							{
								timeUntil = lastProcessing + (siteDelay * 1000);
							}
							else
							{
								timeUntil = lastProcessing + ConfigSingleton.INSTANCE.getPolitnessDelay();
							}
						}

						if (timeUntil <= System.currentTimeMillis())
						{
							try
							{
								this.crawlPage(url);
							}
							catch (InputStreamPageExtractionException e)
							{
								LOG.error("Error in the Page extraction process", e);
							}

							// Setting the politeness Timestamp for future
							// access to the host
							this.frontier.addCrawledHost(url);
							this.frontier.addCrawledURL(url);
							this.frontier.removeCurrentlyCrawledURL(url);
						}
						else
						{
							LOG.debug("Not ready for crawling again yet: " + url.toString());
							this.frontier.scheduleURL(url);
							this.frontier.removeCurrentlyCrawledURL(url);
						}
					}
					else
					{
						LOG.debug("Robots.txt does not allow the crawling of page: " + url.toString());
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
	 * @param {@link URLInfo}
	 * @throws {@link InputStreamPageExtractionException}
	 */
	protected void crawlPage(URLInfo url) throws InputStreamPageExtractionException
	{

		Page page;
		HttpGet request;
		CloseableHttpResponse response = null;
		int pageStatus = -1;

		try
		{
			if (this.httpClient == null) this.httpClient = this.httpConnectionManager.getHttpClient();

			// Initializing a page object
			page = new com.itiniu.iticrawler.crawler.crawling.impl.Page();
			page.setUrl(url);

			// Making the request
			request = new HttpGet(url.toString());
			request.setProtocolVersion(HttpVersion.HTTP_1_1);
			response = (CloseableHttpResponse) this.httpClient.execute(request);

			// Handling the returned status code
			pageStatus = response.getStatusLine().getStatusCode();
			page.setStatusCode(pageStatus);

			this.handleStatusCode(page);
			//TODO: Check if I could change something here from the logic to simplify
			if (pageStatus == HttpStatus.SC_NOT_FOUND)
			{
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
						this.frontier.scheduleURL(url);
					}

					// If user decides to stop because of the status code so may it be.
					if (!page.isContinueProcessing()) return;
				}
			}
			else if (pageStatus == HttpStatus.SC_OK)
			{
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
					throw new InputStreamPageExtractionException("An error occurred in the procesing of the page: the response entity was null: " + url.toString());
				}

			}

		}
		catch (IOException e) {
			LOG.error("An error occurred during the page content extraction", e);
		}
		finally
		{
			try
			{
				if (response != null) response.close();
			}
			catch (IOException e)
			{
				//We Close silently but do log it just in case
				LOG.warn("An error occured while closing the HTTP response while crawling: " + url.toString());
			}
		}
	}

	/**
	 * Internal Method extracting the content as an InputStream in case the
	 * configuration specifies:</br> -{@link PageExtractionType#BY_STREAM}
	 *
	 * @param {@link URLInfo}
	 * @param {@link HttpEntity}
	 * @throws {@link InputStreamPageExtractionException}
	 */
	protected void crawlPageToInputStream(com.itiniu.iticrawler.crawler.crawling.impl.Page page, HttpEntity entity) throws InputStreamPageExtractionException
	{
		InputStream pageStream = null;
		InputStream htmlStream = null;
		URLInfo url = page.getUrl();

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
	 * @param {@link URLInfo}
	 * @param {@link HttpEntity}
	 * @throws {@link InputStreamPageExtractionException}
	 */
	protected void crawlPageToString(com.itiniu.iticrawler.crawler.crawling.impl.Page page, HttpEntity entity)
	{
		URLInfo url = page.getUrl();
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
	 * {@link ICrawlBehavior#handleStatuScode(com.itiniu.iticrawler.crawler.crawling.impl.Page)}.
	 *
	 * @param page
	 */
	protected void handleStatusCode(com.itiniu.iticrawler.crawler.crawling.impl.Page page)
	{
		this.customCrawlBehavior.handleStatuScode(page);
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#handleContentSize(com.itiniu.iticrawler.crawler.crawling.impl.Page)}.
	 *
	 * @param page
	 */
	protected void handleContentSize(com.itiniu.iticrawler.crawler.crawling.impl.Page page)
	{
		this.customCrawlBehavior.handleContentSize(page);
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#processPage(com.itiniu.iticrawler.crawler.crawling.impl.Page)}.
	 *
	 * @param page
	 */
	protected void processPage(com.itiniu.iticrawler.crawler.crawling.impl.Page page)
	{
		this.customCrawlBehavior.processPage(page);
	}

	/**
	 * Internal wrapper method to wrap the call of
	 * {@link ICrawlBehavior#handleOutgoingURLs(com.itiniu.iticrawler.crawler.crawling.impl.Page)}.
	 *
	 * @param page
	 */
	protected void handleOutgoingUrls(com.itiniu.iticrawler.crawler.crawling.impl.Page page)
	{
		this.customCrawlBehavior.handleOutgoingURLs(page);
	}

	/**
	 * Method to schedule the URLs from the page. This method calls
	 * {@link ICrawlBehavior#processPage(com.itiniu.iticrawler.crawler.crawling.impl.Page page)} as last instance.
	 *
	 * @param page
	 */
	protected void scheduleURLs(Page page)
	{
		for (URLInfo url : page.getOutgoingURLs())
		{
			if (!this.frontier.wasURLCrawled(url) && !this.frontier.isURLCurrentlyCrawled(url)
					&& this.frontier.canCrawlURL(url))
			{
				//TODO: Maybe don't schedule each URL separatly but a single time but propose different scheduling mechanisms maybe allow CustomScheduling behavior
					url.setUrlDepth(page.getUrl().getUrlDepth() + 1);
					url.setParentURLInfo(page.getUrl());

					if (this.customCrawlBehavior.shouldScheduleURL(url))
					{
						this.frontier.scheduleURL(url);
					}

			}
		}
	}

}
