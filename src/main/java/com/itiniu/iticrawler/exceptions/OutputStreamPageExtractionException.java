package com.itiniu.iticrawler.exceptions;

/**
 * Created by falk.e on 03/03/14.
 */
public class OutputStreamPageExtractionException extends Exception {

    public OutputStreamPageExtractionException(String message, Exception innerException)
    {
        super(message,innerException);
    }

    public OutputStreamPageExtractionException(String message)
    {
        super(message);
    }

    public OutputStreamPageExtractionException()
    {
        super();
    }

}
