package com.itiniu.iticrawler.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.util.enums.PageExtractionType;
import com.itiniu.iticrawler.httptools.impl.URLCanonicalizer;

/**
 * Type containing the information gained throughout the crawling process.
 * 
 * Each method from the CrawlBehavior Interface gets a URLInfo type as input.
 * Depending on the insides gained from the information of the page at a certain stage of the crawling process
 * continuing processing the page is of no use: call URLInfo.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class Page
{
	private static final Logger logger = LogManager.getLogger(Page.class);

	protected URLInfo url = null;
	protected String html = null;
	protected List<URLInfo> outgoingURLs;
	protected InputStream inStream;
	protected boolean continueProcessing = true;
	protected int statusCode = -1;
	protected Long contentLength = 0l;

	
 	public URLInfo getUrl()
	{
		return url;
	}

	public void setUrl(URLInfo curURL)
	{
		this.url = curURL;
	}

	/**
	 * Method to get the HTML String of the page in case {@link PageExtractionType#BY_STRING} is configured.
	 * @return {@link String}
	 */
	public String getHtml()
	{
		return html;
	}

	public void setHtml(String html)
	{
		this.html = html;
	}

	/**
	 * Returns the collection of outgoing URLs. 
	 * 
	 * @return List<URLInfo>
	 */
	public List<URLInfo> getOutgoingURLs()
	{
		 return (this.outgoingURLs == null) ? new ArrayList<URLInfo>() : this.outgoingURLs;
	}

	/**
	 * Method to set the outgoing URLs.
	 * 
	 * @param urls
	 */
	public void setOutgoingURLs(List<org.apache.tika.sax.Link> urls)
	{
		if (urls != null && urls.size() > 0)
		{
			this.outgoingURLs = new ArrayList<>(urls.size());
			String uri = "";
			for (org.apache.tika.sax.Link link : urls)
			{
				try
				{
					uri = link.getUri().toString();

					// Only consider http protocols
					// TODO: Maybe allow more protocols here just to know where
					// the site is pointing to.
					// Therefore I would need to extend the URLInfo, to
					// differentiate between just links, and URLs to crawl.
					if (uri.startsWith("http"))
					{
						this.outgoingURLs.add(

						new URLInfo.Builder().urlString(URLCanonicalizer.getCanonicalURL(uri))
								.isImage(link.isImage()).isAnchor(link.isAnchor()).rel(link.getRel())
								.text(link.getText()).title(link.getTitle()).fullLink(link.toString()).build()

						);
					}
				}
				catch (MalformedURLException e)
				{
					logger.error("url error: " + uri, e);
					e.printStackTrace();
				}
			}
		}
	}

	public int getStatusCode()
	{
		return this.statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	/**
	 * In case the configured content extraction mode is {@link PageExtractionType#BY_STREAM},
	 * the content the content can be accessed via this {@link InputStream}.
	 *
	 * @throws IOException
	 */
	public InputStream getInStream()
	{
		return this.inStream;
	}

	public void setInStream(InputStream stream)
	{
		this.inStream = stream;
	}

	public boolean isContinueProcessing()
	{
		return continueProcessing;
	}

	/**
	 * Calling this method prevents the page from the next crawling steps.
	 */
	public void setContinueProcessing(boolean continueProcessing)
	{
		this.continueProcessing = continueProcessing;
	}

	/**
	 * Returns the content length of the page
	 * 
	 * @return
	 */
	public Long getContentLength()
	{
		return contentLength;
	}

	public void setContentLength(Long contentLength)
	{
		this.contentLength = contentLength;
	}
	
	public void consumeStream()
	{
		try
		{
			while(this.inStream.read() != -1)
			{
				//Consume the stream
			}
		}
		catch (IOException e)
		{
			// TODO Log
			e.printStackTrace();
		}
		finally{
			try
			{
				this.inStream.close();
			}
			catch (IOException e)
			{
				//Close silently
			}
		}
	}

	/**
	 * In case the configured content extraction mode is {@link PageExtractionType#BY_STREAM},
	 * the content gets written to the provided {@link OutputStream}.
	 * 
	 * @param {@link OutputStream}
	 * @throws {@link IOException}
	 */
	public void writeToOutputStream(OutputStream outputStream) throws IOException
	{
		try
		{
			IOUtils.copy(this.inStream, outputStream);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			this.inStream.close();
			outputStream.flush();
			outputStream.close();
		}
	}

}
