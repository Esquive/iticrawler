package com.itiniu.iticrawler.exceptions;

public class NoCrawlBehaviorProvidedException extends Exception
{
	public NoCrawlBehaviorProvidedException()
	{
		super();
	}
	
	public NoCrawlBehaviorProvidedException(String message)
	{
		super(message);
	}
	
	public NoCrawlBehaviorProvidedException(String message, Exception ex)
	{
		super(message, ex);
	}
}
