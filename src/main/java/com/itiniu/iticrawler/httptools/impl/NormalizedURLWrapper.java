package com.itiniu.iticrawler.httptools.impl;

import java.io.Serializable;
import java.net.MalformedURLException;

import ch.sentric.*;

public class NormalizedURLWrapper extends URLWrapper implements Serializable
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -250279166177801662L;
	
	
	private URL url;

	public NormalizedURLWrapper(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}
	
	@Override
	public String toString()
	{
		return this.url.getRepairedUrl();
	}
	
	@Override
	public int getPort()
	{
		return this.url.getAuthority().getPort();
	}
	
	@Override
	public String getSubDomain()
	{
		return super.getSubdomain(this.url.getAuthority().getHostName().getAsString());
	}
	
	@Override 
	public String getDomain()
	{
		return super.getDomain(this.url.getAuthority().getHostName().getAsString());
	}
	
	@Override
	public String getProtocol()
	{
		return this.url.getScheme();
	}
	

}
