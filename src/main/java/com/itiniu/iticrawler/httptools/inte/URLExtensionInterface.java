package com.itiniu.iticrawler.httptools.inte;

public interface URLExtensionInterface
{
	public String getRedirectURL();
	
	public String getDomain();
	
	public String getSubDomain();
	
	public String getAnchor();
	
	public String getProtocol();
	
	public int getPort();
	
	public String toString();
}
