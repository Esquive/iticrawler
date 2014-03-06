package com.itiniu.iticrawler.crawler.inte;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.sax.Link;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.itiniu.iticrawler.httptools.impl.NormalizedURLWrapper;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;



public abstract class AbstractPage
{
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	protected URLWrapper url = null;
	protected String html = null;
	protected List<URLWrapper> outgoingURLs;
	protected InputStream inStream;
    protected OutputStream outStream;
    protected boolean continueProcessing = true;
	
	protected int statusCode = -1;
	
	public URLWrapper getUrl()
	{
		return url;
	}
	
	public void setUrl(URLWrapper curURL)
	{
		this.url = curURL;
	}
	
	public String getHtml()
	{
		return html;
	}
	
	public void setHtml(String html)
	{
		this.html = html;
	}
		
	public List<URLWrapper> getOutgoingURLs()
	{
		if(this.outgoingURLs == null)
		{
			return new ArrayList<>();
		}
		return this.outgoingURLs;
	}
	
	public void setOutgoingURLs(List<Link> urls)
	{
		if(urls !=null && urls.size() > 0)
		{
			this.outgoingURLs = new ArrayList<>(urls.size());
			String uri = "";
		for(Link link : urls)
		{
			try {
				
				uri = link.getUri().toString();
				
				//Only consider http protocols
				//TODO: Maybe allow more protocols here just to know where the site is pointing to.
				//Therefore I would need to extend the URLWrapper, to differentiate between just links, and URLs to crawl. 
				if(uri.startsWith("http"))
				{
						this.outgoingURLs.add(
						
						new URLWrapper.Builder()
						.url( new URL(new NormalizedURLWrapper(link.getUri()).toString()) )
						.isImage(link.isImage())
						.isAnchor(link.isAnchor())
						.rel(link.getRel())
						.text(link.getText())
						.title(link.getTitle())
						.fullLink(link.toString())
						.build()
						
						);
				}
				
			} catch (MalformedURLException e) {
				logger.error("url error: " + uri,e);
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

	public InputStream getInStream() {
		return this.inStream;
	}

	public void setInStream(InputStream stream) {
		this.inStream = stream;
	}

    public boolean isContinueProcessing() {
        return continueProcessing;
    }

    public void setContinueProcessing(boolean continueProcessing) {
        this.continueProcessing = continueProcessing;
    }

    public void setOutStream(OutputStream out)
    {
        this.outStream = out;
    }

    public void writeToOutputStream(OutputStream outputStream) throws IOException
    {
        try {
			IOUtils.copy(this.inStream, outputStream);
		} catch (IOException e) {
			throw e;
		}
        finally
        {
        	this.inStream.close();
        }
    }

}
