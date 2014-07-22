package com.itiniu.iticrawler.crawler.impl;

import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.behaviors.impl.RobotTxtAwareBehavior;
import com.itiniu.iticrawler.behaviors.impl.RobotTxtUnawareBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.exceptions.NoCrawlBehaviorProvidedException;
import com.itiniu.iticrawler.factories.impl.ProcessedUrlsStorageFactory;
import com.itiniu.iticrawler.factories.impl.RobotTxtStorageFactory;
import com.itiniu.iticrawler.factories.impl.ScheduledUrlsStorageFactory;
import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.HttpPoolingConnectionManager;
import com.itiniu.iticrawler.httptools.impl.URLCanonicalizer;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.httptools.inte.IHttpConnectionManager;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

/**
 * AbstractCrawlController
 * 
 * This class is the entry point of the crawler this class builds and runs
 * crawling according to the configuration specified by the user inside the
 * config Singleton.
 * 
 * @author esquive
 * 
 */
public class CrawlController implements Runnable
{
	private static final Logger LOG = LogManager.getLogger(CrawlController.class);

	// ConnectionManager
	private IHttpConnectionManager httpConnectionManager = null;

	// Holders for the real-time data needed for the crawling
	private IScheduledURLStore scheduledUrls = null;
	private IProcessedURLStore processedUrls = null;
	private IRobotTxtStore robotTxtData = null;

	// Thread pool for the crawler threads
	private ExecutorService crawlerThreadPool = null;

	// Thread to monitor the crawler Threads
	private Thread crawlerMonitoringThread = null;

	private boolean hasSeeds = false;

	public CrawlController()
	{
		try
		{
			this.initComponents();
		}
		catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e)
		{
			LOG.error("An error occured while creating the http connection pool");
		}
		catch (NoCrawlBehaviorProvidedException e)
		{
			LOG.error("No CrawlBehavior was specified: Use ConfigSingleton.setCustomCallBehavior()");
		}
	}

	public void startCrawling()
	{
		if (this.hasSeeds)
		{
			try
			{
				this.crawlerThreadPool = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE
						.getNumberOfCrawlerThreads());

				for (int i = 0; i < ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads(); i++)
				{
					((ThreadPoolExecutor) this.crawlerThreadPool).execute(this.buildCrawler());
				}

				this.crawlerMonitoringThread = new Thread(this);
				this.crawlerMonitoringThread.setName("CrawlControllerMonitor");
				this.crawlerMonitoringThread.start();

			}
			catch (InstantiationException | IllegalAccessException e)
			{
				LOG.error("An error occured while creating a crawler thread");
			}
		}
		else
		{
			LOG.error("No seeds are specified. Before starting the crawling you need to specify seeds: addSeeds(String url)!");
		}
	}

	protected void initComponents() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException,
			NoCrawlBehaviorProvidedException
	{
		this.initDataHolders();
		this.initConnectionManager();
		if (ConfigSingleton.INSTANCE.getCustomCrawlBehavior() == null)
		{
			throw new NoCrawlBehaviorProvidedException("No CrawlBehabior was specified!");
		}
		LOG.info("Components are initialized!");
	}

	protected void initDataHolders()
	{
		this.scheduledUrls = new ScheduledUrlsStorageFactory().getScheduledUrlData();
		this.processedUrls = new ProcessedUrlsStorageFactory().getProcessedUrlStorage();
		this.robotTxtData = new RobotTxtStorageFactory().getRobotTxtData();
	}

	protected void initConnectionManager() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException
	{
		this.httpConnectionManager = new HttpPoolingConnectionManager();
	}

	public void addSeeds(String url)
	{
		try
		{
			this.scheduledUrls.scheduleURL(new URLWrapper(URLCanonicalizer.getCanonicalURL(url)));
			this.hasSeeds = true;
		}
		catch (MalformedURLException e)
		{
			LOG.error("The seed you provided could not be parsed: " + url);
		}
	}

	protected Crawler buildCrawler() throws InstantiationException, IllegalAccessException
	{
		Crawler crawlerThread = null;
		crawlerThread = new Crawler();
		crawlerThread.setCustomCrawlBehavior(ConfigSingleton.INSTANCE.getCustomCrawlBehavior().newInstance());
		crawlerThread.setScheduledUrlsData(this.scheduledUrls);
		crawlerThread.setProcessedUrlsData(this.processedUrls);
		crawlerThread.setRobotTxtData(this.robotTxtData);
		crawlerThread.setHttpConnectionManager(this.httpConnectionManager);
		crawlerThread.setExtractionType(ConfigSingleton.INSTANCE.getExtractionType());

		if (ConfigSingleton.INSTANCE.isConsiderRobotTxt()) crawlerThread
				.setRobotTxtBehavior(new RobotTxtAwareBehavior());
		else crawlerThread.setRobotTxtBehavior(new RobotTxtUnawareBehavior());

		return crawlerThread;
	}

	@Override
	public void run()
	{
		boolean shouldRun = true;
		int activeThreadCount = -1;

		while (shouldRun)
		{
			try
			{
				Thread.sleep(10000);

				// Check How many threads are active
				activeThreadCount = ((ThreadPoolExecutor) crawlerThreadPool).getActiveCount();

				// If somme threads are not active anymore
				if (activeThreadCount < ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads())
				{
					for (int i = activeThreadCount; i <= ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads(); i++)
					{
						try
						{
							((ThreadPoolExecutor) this.crawlerThreadPool).execute(this.buildCrawler());
						}
						catch (InstantiationException | IllegalAccessException e)
						{
							LOG.error("An error occured while creating a crawler thread");
						}
					}
				}

			}
			catch (InterruptedException e)
			{
				LOG.error("An error occured during crawling, a thread got interrupted!");
			}

		}
	}

}
