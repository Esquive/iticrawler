package com.itiniu.iticrawler.httptools.impl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.Consts;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.httptools.inte.IHttpConnectionManager;

/**
 * Implementation of the {@link IHttpConnectionManager}. This implementation
 * handles the connections with an connection pool under the hood. HTTP
 * Connection Pool for the crawler threads. This class contains a factory method
 * the {@link HttpClient} used by the crawler threads. These clients are managed
 * by a http connection pool.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public class HttpPoolingConnectionManager implements IHttpConnectionManager, Runnable
{

	private static final Logger LOG = LogManager.getLogger(HttpPoolingConnectionManager.class);

	private HttpClientConnectionManager mConnectionManager = null;

	private Thread httpConnectionMonitoringThread = null;
	private Lock httpClientGenerationLock = null;

	/**
	 * Implementation of the {@link Runnable@run()} method. The monitoring of
	 * the connection pool is performed by a dedicated thread
	 */
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(5000);
				this.mConnectionManager.closeExpiredConnections();
				this.mConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				LOG.error("Connection Pool Monitoring Thread got interrupted", e);
			}
		}

	}

	public HttpPoolingConnectionManager() throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException
	{
		// Creating protocol schemes
		Registry<ConnectionSocketFactory> registry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", new PlainConnectionSocketFactory())
				.register(
						"https",
						new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null,
								new TrustSelfSignedStrategy()).build())).build();

		// Creating the connectionManager
		this.mConnectionManager = new PoolingHttpClientConnectionManager(registry);
		((PoolingHttpClientConnectionManager) this.mConnectionManager).setMaxTotal(ConfigSingleton.INSTANCE
				.getMaxConnections());
		((PoolingHttpClientConnectionManager) this.mConnectionManager).setDefaultMaxPerRoute(ConfigSingleton.INSTANCE
				.getMaxConnectionsPerHost());
		((PoolingHttpClientConnectionManager) this.mConnectionManager).setDefaultConnectionConfig(ConnectionConfig
				.custom().setCharset(Consts.UTF_8).build());
		((PoolingHttpClientConnectionManager) this.mConnectionManager).setDefaultSocketConfig(SocketConfig.custom()
				.setSoTimeout(ConfigSingleton.INSTANCE.getSocketTimeout()).build());

		// Run a Thread to Monitor the connections
		this.httpConnectionMonitoringThread = new Thread(this);
		this.httpConnectionMonitoringThread.setName("HttpConnectionMonitoringThread");
		this.httpConnectionMonitoringThread.start();

		// Initialize the lock for the multi-threaded access
		this.httpClientGenerationLock = new ReentrantLock();
	}

	@Override
	public HttpClient getHttpClient()
	{
		this.httpClientGenerationLock.lock();
		try
		{
			HttpClient toReturn = HttpClients
					.custom()
					.setConnectionManager(this.mConnectionManager)
					.setDefaultRequestConfig(
							RequestConfig.custom().setConnectTimeout(ConfigSingleton.INSTANCE.getConnectionTimeout())
									.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).setExpectContinueEnabled(false)
									.build()).addInterceptorFirst(new GzipEncodedRequestInterceptor())
					.addInterceptorFirst(new GzipEncodedResponseInterceptor())
					.setRedirectStrategy(new RedirectStrategy() {

						@Override
						public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
								throws ProtocolException
						{
							// We don't follow redirects because we do it ourself
							return false;
						}

						@Override
						public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response,
								HttpContext context) throws ProtocolException
						{
							return null;
						}
					}).setUserAgent(ConfigSingleton.INSTANCE.getUserAgent()).build();

			return toReturn;

		}
		finally
		{
			this.httpClientGenerationLock.unlock();
		}
	}

}
