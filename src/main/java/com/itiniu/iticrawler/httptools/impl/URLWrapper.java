package com.itiniu.iticrawler.httptools.impl;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import com.itiniu.iticrawler.httptools.inte.URLExtensionInterface;

public class URLWrapper implements URLExtensionInterface, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1960291945681398107L;
	
	
	private URL url = null;
	protected long lastCrawlTime;
	protected int urlDepth;
	protected URLWrapper parentURL;
	
	protected URLWrapper()
	{
		
	}
	
	public URLWrapper(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}
	
	
	public String toString()
	{
		return this.url.toString();
	}

	@Override
	public String getRedirectURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain()
	{
		return this.getDomain(this.url.getHost());
	}
	
	public String getDomain(String domain)
	{
		String toReturn = null;

		String[] tokens = domain.split("\\.");
		
		//TODO: Stabilize the function to avoid the index out of bounce execption
		toReturn = tokens[tokens.length - 2] + "." + tokens[tokens.length -1]; 
		
		return toReturn;
	}

	@Override
	public String getSubDomain()
	{
		return this.getSubdomain(this.url.getHost());
	}
	
	public String getSubdomain(String host)
	{

		String toReturn = "";
		
		int toplevel = host.lastIndexOf(".");
			
		int subDomainEndingIndex = host.lastIndexOf(".", toplevel - 1);
		
		if(subDomainEndingIndex != -1)
		{
			toReturn = host.substring(0, subDomainEndingIndex);
		}
		
		return toReturn;
	}

	@Override
	public String getAnchor()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public long getLastCrawlTime()
	{
		return this.lastCrawlTime;
	}
	
	public int getUrlDepth()
	{
		return this.urlDepth;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public void setLastCrawlTime(long lastCrawlTime)
	{
		this.lastCrawlTime = lastCrawlTime;
	}

	public void setUrlDepth(int urlDepth)
	{
		this.urlDepth = urlDepth;
	}

	public URLWrapper getParentURL()
	{
		return parentURL;
	}

	public void setParentURL(URLWrapper parentURL)
	{
		this.parentURL = parentURL;
	}

	@Override
	public String getProtocol()
	{
		
		return this.url.getProtocol();
	}

	@Override
	public int getPort()
	{
		return this.url.getPort();
	}
	
	
	
	

}
