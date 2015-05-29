package com.itiniu.iticrawler.crawler;

import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.itiniu.iticrawler.crawler.frontier.Frontier;
import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtCache;
import com.itiniu.iticrawler.factories.FrontierFactory;
import com.itiniu.iticrawler.factories.RobotTxtStorageFactory;
import com.itiniu.iticrawler.httptools.impl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.behaviors.robotstxt.RobotTxtAwareBehavior;
import com.itiniu.iticrawler.behaviors.robotstxt.RobotTxtUnawareBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.exceptions.NoCrawlBehaviorProvidedException;
import com.itiniu.iticrawler.httptools.inte.IHttpConnectionManager;

/**
 * CrawlController is the class to steer the crawling. it instantiates the
 * crawler threads and monitors them. The monitoring is performed by a dedicated
 * thread.
 * 
 * To use the CrawlController:
 * 
 * <pre>
 * CrawlController controller = new CrawlController();
 * controller.addSeed(<some url String>);
 * controller.startCrawling();
 * </pre>
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public class CrawlController implements Runnable
{
	private static final Logger LOG = LogManager.getLogger(CrawlController.class);

	// ConnectionManager
	private IHttpConnectionManager httpConnectionManager = null;

	// Holders for the real-time data needed for the crawling
	private Frontier frontier = null;

	//RobotTxt
	private RobotsTxtCache robotTxtData = null;

	// Thread pool for the crawler threads
	private ExecutorService crawlerThreadPool = null;

	// Thread to monitor the crawler Threads
	private Thread crawlerMonitoringThread = null;

	private boolean hasSeeds = false;

	/**
	 * Default Constructor the the CrawlController. All required types are
	 * instantiated.
	 */
	public CrawlController()
	{
		try
		{
			this.initComponents();
			final ExecutorService pool = this.crawlerThreadPool;
			Runtime.getRuntime().addShutdownHook(new Thread(){
				
				@Override
				public void run()
				{
					try
					{
                        if(pool!=null) {
                            pool.shutdown();
                            pool.awaitTermination(20, TimeUnit.SECONDS);
                        }
					}
					catch (InterruptedException e)
					{
						LOG.error("Error while shuting down the threadpool");
					}
				}
				
			});
		}
		catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e)
		{
			LOG.error("An error occured while creating the http connection pool", e);
		}
		catch (NoCrawlBehaviorProvidedException e)
		{
			LOG.error("No CrawlBehavior was specified: Use ConfigSingleton.setCustomCallBehavior()", e);
		}
	}

	/**
	 * Method starting the crawling thread pool. Call this method once seeds
	 * where provided using
	 */
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
				LOG.error("An error occurred while creating a crawler thread",e);
			}
		}
		else
		{
			LOG.error("No seeds are specified. Before starting the crawling you need to add seeds: addSeeds(String url)!");
		}
	}

	/**
	 * Internal wrapper method calling all the particular initializer methods.
	 * 
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws NoCrawlBehaviorProvidedException
	 */
	protected void initComponents() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException,
			NoCrawlBehaviorProvidedException
	{
		//Check if the custom Crawl behavior is set.
		if (ConfigSingleton.INSTANCE.getCustomCrawlBehavior() == null)
		{
			throw new NoCrawlBehaviorProvidedException("No CrawlBehavior was specified!");
		}

		//Initialize the HTTP Connection Pool
		this.initConnectionManager();

		//Initialize the data storage
		this.initDataCache();

		LOG.info("!!!Components are initialized!!!");
	}

	/**
	 * Internal Method to initialize the data holders: the frontier and the
	 * storage for the robots.txt
	 */
	protected void initDataCache()
	{
		LOG.info("Initialize Frontier Cache...");
		this.frontier = FrontierFactory.getFrontier();
		LOG.info("...Frontier Cache Initialized.");

		LOG.info("Initialize Robots.txt Cache...");
		this.robotTxtData = RobotTxtStorageFactory.getRobotTxtData();
		LOG.info("...Robots.txt Cache Initialized.");
	}

	/**
	 * Internal Method to initialize the http connection pool.
	 * 
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 */
	protected void initConnectionManager() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException
	{
		LOG.info("Initialize HTTP Connection Pool...");
		this.httpConnectionManager = new HttpPoolingConnectionManager();
		LOG.info("...HTTP Connection Pool Initialized.");
	}

	/**
	 * Use this method to add a seed from which crawling can start. To start
	 * crawling at least one seed must be defined.
	 * 
	 * @param url
	 */
	public void addSeed(String url)
	{
		try
		{
			this.frontier.scheduleURL(new URLInfo(URLCanonicalizer.getCanonicalURL(url)));
			this.hasSeeds = true;
		}
		catch (MalformedURLException e)
		{
			LOG.error("The provided seed could not be parsed: " + url, e);
		}
	}

	/**
	 * Internal Factory method to build the crawler threads and add them to the
	 * crawler thread pool.
	 * 
	 * @return Crawler
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected Crawler buildCrawler() throws InstantiationException, IllegalAccessException
	{
		Crawler crawlerThread = null;
		crawlerThread = new Crawler(this.frontier, this.robotTxtData, ConfigSingleton.INSTANCE
				.getCustomCrawlBehavior().newInstance(),
				ConfigSingleton.INSTANCE.isConsiderRobotTxt() ? new RobotTxtAwareBehavior()
						: new RobotTxtUnawareBehavior(), this.httpConnectionManager,
				ConfigSingleton.INSTANCE.getExtractionType());

		return crawlerThread;
	}

	/**
	 * The code contained in the run method monitors the status of the crawler
	 * thread pool in a dedicated thread.
	 */
	@Override
	public void run()
	{
		//TODO: Change the handling of no work situation
		int activeThreadCount;
		while (true)
		{
			try
			{
				Thread.sleep(10000);

				// Check How many threads are active
				activeThreadCount = ((ThreadPoolExecutor) crawlerThreadPool).getActiveCount();

				// If somme threads are not active anymore
				if (activeThreadCount < ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads())
				{
					if (!this.frontier.isScheduleEmpty())
					{
						for (int i = activeThreadCount; i <= ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads(); i++)
						{
							try
							{
								this.crawlerThreadPool.execute(this.buildCrawler());
							}
							catch (InstantiationException | IllegalAccessException e)
							{
								LOG.error("An error occurred while creating a crawler thread");
							}
						}
					}
					else
					{
						//We exit
						this.crawlerThreadPool.awaitTermination(10, TimeUnit.MINUTES);
						break;
					}
				}

			}
			catch (InterruptedException e)
			{
				LOG.error("An error occurred during crawling, a thread got interrupted!");
			}
		}
	}

}
