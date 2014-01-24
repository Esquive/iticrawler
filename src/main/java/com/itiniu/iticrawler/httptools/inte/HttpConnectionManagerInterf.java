package com.itiniu.iticrawler.httptools.inte;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;

public interface HttpConnectionManagerInterf
{	
	public ClientConnectionManager getClientConnectionManager();
	
	public HttpParams getHttpParams();
	
	public HttpClient getNewHttpClient();
}
