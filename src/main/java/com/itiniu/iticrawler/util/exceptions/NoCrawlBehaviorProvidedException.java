package com.itiniu.iticrawler.util.exceptions;

public class NoCrawlBehaviorProvidedException extends Exception
{
	private static final long serialVersionUID = -4264851329439596928L;

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
