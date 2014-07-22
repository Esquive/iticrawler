package com.itiniu.iticrawler.exceptions;

/**
 * Created by falk.e on 28/02/14.
 */
public class InputStreamPageExtractionException extends Exception
{
	private static final long serialVersionUID = 3144195582958495224L;

	public InputStreamPageExtractionException(String message, Exception innerException)
    {
        super(message, innerException);
    }

    public InputStreamPageExtractionException(String message)
    {
        super(message);
    }

    public InputStreamPageExtractionException()
    {

    }

}
