package com.itiniu.iticrawler.httptools.impl;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class GzipEncodedRequestInterceptor implements HttpRequestInterceptor

{

	@Override
	public void process(HttpRequest httpRequest, HttpContext httpContext)
			throws HttpException, IOException
	{

		if (!httpRequest.containsHeader("Accept-Encoding"))
		{
			httpRequest.addHeader("Accept-Encoding", "gzip");
		}
	}

}
