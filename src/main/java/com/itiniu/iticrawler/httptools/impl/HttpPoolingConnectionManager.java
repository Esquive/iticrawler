package com.itiniu.iticrawler.httptools.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.httptools.inte.HttpConnectionManagerInterf;



public class HttpPoolingConnectionManager implements HttpConnectionManagerInterf, Runnable
{
	
	private ClientConnectionManager mConnectionManager = null;
	private HttpParams mHttpParams = null;
	
	private HttpRequestInterceptor gzipEncodedRequest= null;
	private HttpResponseInterceptor gzipEncodedRespone = null;
	
	private Thread httpConnectionMonitoringThread = null;
	private Lock httpClientGenerationLock = null;
	
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(5000);
				this.mConnectionManager.closeExpiredConnections();
				this.mConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}

	public HttpPoolingConnectionManager()
	{
		
		//Creating basic Http parameters
		mHttpParams = new BasicHttpParams();
		HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(this.mHttpParams);
		paramsBean.setVersion(HttpVersion.HTTP_1_1);
		paramsBean.setContentCharset("UTF-8");
		paramsBean.setUseExpectContinue(false);
		mHttpParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		mHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, ConfigSingleton.INSTANCE.getUserAgent());
		mHttpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, ConfigSingleton.INSTANCE.getSocketTimeout());
		mHttpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,ConfigSingleton.INSTANCE.getConnectionTimeout());

		//I handle the redirects myself so that I can store the redirect
		mHttpParams.setBooleanParameter("http.protocol.handle-redirects", false);

		//What protocols will the crawler handle
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		//Creating the connectionManager
		this.mConnectionManager = new PoolingClientConnectionManager(schemeRegistry);
		((PoolingClientConnectionManager)this.mConnectionManager).setMaxTotal(ConfigSingleton.INSTANCE.getMaxConnections());
		((PoolingClientConnectionManager)this.mConnectionManager).setDefaultMaxPerRoute(ConfigSingleton.INSTANCE.getMaxConnectionsPerHost());
		
		
		//Initilize the request/response interceptors
		this.gzipEncodedRequest = new GzipEncodedRequestInterceptor();
		this.gzipEncodedRespone = new GzipEncodedResponseInterceptor();
		
		//Initialize the lock for the multi-threaded access
		
		//Run a Thread to Monitor the connections
		this.httpConnectionMonitoringThread = new Thread(this);
		this.httpConnectionMonitoringThread.setName("HttpConnectionMonitoringThread");
		this.httpConnectionMonitoringThread.start();
		
		this.httpClientGenerationLock = new ReentrantLock();
		
	}
	
	
	@Override
	public ClientConnectionManager getClientConnectionManager()
	{
		return mConnectionManager;
	}

	@Override
	public HttpParams getHttpParams()
	{
		return mHttpParams;
	}


	@Override
	public HttpClient getNewHttpClient()
	{
		this.httpClientGenerationLock.lock();
		
		HttpClient toReturn = new DefaultHttpClient(this.mConnectionManager, this.mHttpParams);
		((DefaultHttpClient)toReturn).addRequestInterceptor(this.gzipEncodedRequest);
		((DefaultHttpClient)toReturn).addResponseInterceptor(this.gzipEncodedRespone);
		
		this.httpClientGenerationLock.unlock();
		
		return toReturn;
		
	}


	
	
	

}
