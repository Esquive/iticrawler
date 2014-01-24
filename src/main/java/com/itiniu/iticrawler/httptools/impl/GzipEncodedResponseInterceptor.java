package com.itiniu.iticrawler.httptools.impl;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.protocol.HttpContext;

public class GzipEncodedResponseInterceptor implements HttpResponseInterceptor
{

	@Override
	public void process(HttpResponse httpResponse, HttpContext httpContext)
			throws HttpException, IOException
	{
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null)
		{
			Header ceheader = entity.getContentEncoding();
			if (ceheader != null)
			{
				HeaderElement[] codecs = ceheader.getElements();
				for (int i = 0; i < codecs.length; i++)
				{
					if (codecs[i].getName().equalsIgnoreCase("gzip"))
					{
						httpResponse.setEntity(new GzipDecompressingEntity(
								httpResponse.getEntity()));
						return;
					}
				}
			}
		}
	}

}
