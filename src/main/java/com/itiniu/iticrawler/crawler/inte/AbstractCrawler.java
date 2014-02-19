package com.itiniu.iticrawler.crawler.inte;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.SAXException;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.impl.DefaultPage;
import com.itiniu.iticrawler.crawler.inte.AbstractPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.httptools.inte.HttpConnectionManagerInterf;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;


/**
 * Crawler. Objects of this class are used to run inside crawling threads. They
 * do the page processing and the scheduling.
 * 
 * @author esquive
 * 
 */
public abstract class AbstractCrawler implements Runnable
{
	//Getting the logger
	protected static final Logger logger = LogManager.getLogger(AbstractCrawler.class);

	
	// The Data holders
	private IScheduledURLStore scheduledUrls = null;
	private IProcessedURLStore processedUrls = null;
	private IRobotTxtStore robotTxtData = null;

	// The Behaviors
	private ICrawlBehavior customCrawlBehavior = null;
	private IRobotTxtBehavior robotTxtBehavior = null;

	// The HttpTools
	private HttpClient httpClient = null;

	private HttpConnectionManagerInterf httpConnectionManager = null;

	// Crawler relevant variables
	private boolean busy = false;

	
	@Override
	public void run()
	{
		this.execute();
	}

	/**
	 * 
	 */
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

				if (!this.processedUrls.isCurrentlyProcessedUrl(cUrl)
						&& !this.processedUrls.wasProcessed(cUrl))
				{
					this.processedUrls.addCurrentlyProcessedUrl(cUrl);

					if (!this.robotTxtData.containsRule(cUrl))
					{
						this.robotTxtBehavior.fetchRobotTxt(cUrl,
								this.httpConnectionManager.getNewHttpClient(), this.robotTxtData);
					}
					if (this.robotTxtData.allows(cUrl))
					{
						// check for the politeness (I don't need to check if
						// the
						// page was processed before since in the scheduler
						// usually I only have
						// single values)
						// Naaaaa change of mind:
						// It might happen that URLs get scheduled twice
						// Because of locking it is more
						// efficient to check twice if it was already
						// processed

						// Getting a timeStamp to determine if I can request
						// on
						// the host again
						long timeStamp = this.processedUrls.lastHostProcessing(cUrl)
								+ ConfigSingleton.INSTANCE.getPolitnessDelay();

						if (timeStamp <= System.currentTimeMillis())
						{
							// TODO: catch the exceptions here and not at
							// the lower method level:
							// This is relevant for the URL scheduling.

							// Fetch the page content
							AbstractPage page = this.extractData(cUrl);

							// TODO: Add statusCode to the page.

							if (page != null)
							{
								// Schedule the links (+User Implementation)
								this.scheduleURLs(page);

								// Process the page
								this.processPage(page);
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

	/**
	 * 
	 * @param url
	 * @return
	 */
	private AbstractPage extractData(URLWrapper url)
	{
		AbstractPage toReturn = null;
		HttpGet request = null;
		CloseableHttpResponse response = null;
		InputStream htmlStream = null;

		int pageStatus = -1;

		try
		{
			// Making the request
			request = new HttpGet(url.toString());
			response = (CloseableHttpResponse)this.httpClient.execute(request);

			pageStatus = response.getStatusLine().getStatusCode();

			this.customCrawlBehavior.handleStatuScode(pageStatus, url);

			if (pageStatus == HttpStatus.SC_OK)
			{
				// Getting the content
				HttpEntity entity = response.getEntity();
				
				if(entity != null)
				{
					htmlStream = entity.getContent();
					
					//Do all the document parsing here links and htmlContent
					LinkContentHandler links = new LinkContentHandler();
					ToHTMLContentHandler html = new ToHTMLContentHandler();
					TeeContentHandler teeHandler = new TeeContentHandler(links, html);
					
					Metadata metadata = new Metadata();
					metadata.add(Metadata.CONTENT_LOCATION, url.toString());
					metadata.add(Metadata.RESOURCE_NAME_KEY, url.toString());
					
					HtmlParser parser = new HtmlParser();
					parser.parse(htmlStream, 
							     teeHandler,
							     metadata,
							     new ParseContext());
					
							  // //Process the page content
									toReturn = new DefaultPage();
									toReturn.setUrl(url);
									toReturn.setHtml(html.toString());
									toReturn.setOutgoingURLs(links.getLinks());
					
					
				}
				else
				{
					//TODO: throw an exception
				}
			}
			else if (pageStatus == HttpStatus.SC_NOT_FOUND)
			{
				logger.info("URL not found: " + url.toString());

			}
			else if ((pageStatus == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (pageStatus == HttpStatus.SC_MOVED_PERMANENTLY))
			{
				Header header = response.getFirstHeader("Location");
				if (header != null)
				{
					// TODO: Implement the redirect
				}
			}

		}
		catch (ClientProtocolException e1)
		{
			e1.printStackTrace();
			// TODO: remove the processed URL from the processedUrls data holder
			// NOT SURE THEY SHOULD STAY THERE
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
			// TODO: remove the processed URL from the processedUrls data holder
			// NOT SURE THEY SHOULD STAY THERE
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			
				try {
					if(htmlStream != null) htmlStream.close();
					if(response != null) response.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return toReturn;
	}


	/**
	 * 
	 * @param page
	 */
	private void processPage(AbstractPage page)
	{
		this.customCrawlBehavior.processPage(page);
	}

	/**Ò
	 * 
	 * @param page
	 */
	private void scheduleURLs(AbstractPage page)
	{
		for (URLWrapper cUrl : page.getOutgoingURLs())
		{
			if (!this.processedUrls.wasProcessed(cUrl)
					&& !this.processedUrls.isCurrentlyProcessedUrl(cUrl))
			{

				if ((page.getUrl().getUrlDepth() + 1) != ConfigSingleton.INSTANCE
						.getMaxCrawlDepth())
				{
					cUrl.setUrlDepth(page.getUrl().getUrlDepth() + 1);
					cUrl.setParentURL(page.getUrl());

					if (this.customCrawlBehavior.shouldScheduleURL(cUrl.toString()))
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

	public void setHttpConnectionManager(HttpConnectionManagerInterf httpConnectionManager)
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

}
