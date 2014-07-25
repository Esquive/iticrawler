package com.itiniu.iticrawler.httptools.inte;

import org.apache.http.client.HttpClient;

/**
 * Interface for HTTP connections handler, and client factory.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface IHttpConnectionManager
{		
	/**
	 * Factory method returning an {@link HttpClient} for the use with crawler threads.
	 * 
	 * @return {@link HttpClient}
	 */
	public HttpClient getHttpClient();
}
