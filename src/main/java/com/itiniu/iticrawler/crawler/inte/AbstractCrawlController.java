package com.itiniu.iticrawler.crawler.inte;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.poi.hssf.model.InternalSheet;

import com.itiniu.iticrawler.behaviors.impl.RobotTxtAwareBehavior;
import com.itiniu.iticrawler.behaviors.impl.RobotTxtUnawareBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.impl.DefaultCrawler;
import com.itiniu.iticrawler.factories.impl.ProcessedUrlsStorageFactory;
import com.itiniu.iticrawler.factories.impl.RobotTxtStorageFactory;
import com.itiniu.iticrawler.factories.impl.ScheduledUrlsStorageFactory;
import com.itiniu.iticrawler.httptools.impl.HttpPoolingConnectionManager;
import com.itiniu.iticrawler.httptools.impl.NormalizedURLWrapper;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.httptools.impl.UrlNormalizer;
import com.itiniu.iticrawler.httptools.inte.HttpConnectionManagerInterf;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;



/**
 * AbstractCrawlController
 * 
 * This class is the entry point of the crawler this class builds and runs crawling according
 * to the configuration specified by the user inside the config Singleton.
 * 
 * @author esquive
 *
 */
public abstract class AbstractCrawlController implements Runnable
{
	
	//The Behavior got moved to the config singleton.
	//Behavior Classes for the crawlers
	//private Class<CustomCrawlBehaviorInterf> customCrawlBehavior = null;

	//ConnectionManager: the connection Manager also gives you the HttpClients
	//for the crawler threads
	private HttpConnectionManagerInterf httpConnectionManager = null;
	
	//Holders for the real-time data needed for the crawling
	private IScheduledURLStore scheduledUrls = null;
	private IProcessedURLStore processedUrls = null;
	private IRobotTxtStore 	   robotTxtData  = null;
	
	//Thread pool for the crawler threads
	private ExecutorService crawlerThreadPool = null;
	
	//Thread to monitor the crawler Threads
	private Thread crawlerMonitoringThread = null;
	
	
	//Check if the controller was initiated
	private boolean wasInitiated = false;
	private boolean hasSeeds = false;
	
	//TODO: Add booleans for the Custom DataStorage mode	

	/**
	 * Method to start the crawling
	 * 
	 * -Here the crawler threads are initialized and run
	 */
	public void startCrawling()
	{
		if(this.wasInitiated && this.hasSeeds)
		{
			//Ok so now we are here we can finally start all our threaded components
			this.crawlerThreadPool = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads());
				
			
			//Build the threads and soon we are ready to go.
			for(int i= 0; i < ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads(); i++ )
			{
				try
				{
					((ThreadPoolExecutor)this.crawlerThreadPool).execute(this.buildCrawler());
				}
				catch(NullPointerException nPE)
				{
					//TODO: Implement
					nPE.printStackTrace();
				}
			}
			
			this.crawlerMonitoringThread = new Thread(this);
			this.crawlerMonitoringThread.setName("CrawlControllerMonitor");
			this.crawlerMonitoringThread.start();
		
		}
		else
		{
			//TODO: give back the error that the Crawler was not initiated properly
		}
	}

	
	
	/**
	 * Method to initialize the data holders and connection manager
	 * 
	 * - 1: This method calls initDataHolders
	 * - 2: this method calls initConnectionManager
	 */
	public void initComponents()
	{
		//getting the DataStorages
		this.initDataHolders();
		
		//Initialize the HttpConnection Manager
		this.initConnectionManager();
		
		this.wasInitiated = true;
	}

	protected void initDataHolders()
	{
		this.scheduledUrls = new ScheduledUrlsStorageFactory().getScheduledUrlData();
		this.processedUrls = new ProcessedUrlsStorageFactory().getProcessedUrlStorage();
		this.robotTxtData = new RobotTxtStorageFactory().getRobotTxtData();
		
	}

	protected void initConnectionManager()
	{
		this.httpConnectionManager = new HttpPoolingConnectionManager();
	}
	
	
	
	/**
	 * Method to add seeds to begin with
	 * 
	 * -The method calls the scheduleUnique method of the scheduledUrls data holder
	 * this should be avoided later on since this is less efficient. With the default
	 * implementation it is better to check if the URL was processed or not.
	 * 
	 * @param url
	 */
	public void addSeeds(String url)
	{
		URLWrapper toSchedule = null;
		
		try
		{
//			url = UrlNormalizer.normalize(url);
			
			toSchedule = new URLWrapper(new NormalizedURLWrapper(url).toString());
			
			this.scheduledUrls.scheduleURL(toSchedule);
			
			this.hasSeeds = true;
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	
	
	/**
	 * Factory Method to build the crawlers according to the blueprint given by 
	 * config Singleton
	 * 
	 * @return
	 */
	protected AbstractCrawler buildCrawler()
	{
		AbstractCrawler toReturn = null;
		
		try
		{
			toReturn = new DefaultCrawler();
			
			toReturn.setHttpClient(this.httpConnectionManager.getNewHttpClient());
			
			toReturn.setScheduledUrlsData(this.scheduledUrls);
			toReturn.setProcessedUrlsData(this.processedUrls);
			toReturn.setRobotTxtData(this.robotTxtData);
			toReturn.setHttpConnectionManager(this.httpConnectionManager);
			toReturn.setExtractionType(ConfigSingleton.INSTANCE.getExtractionType());
			
			//TODO: Check for null
			toReturn.setCustomCrawlBehavior(ConfigSingleton.INSTANCE.getCustomCrawlBehavior().newInstance());
			
			if(ConfigSingleton.INSTANCE.isConsiderRobotTxt())
			{
				//toReturn.setRobotTxtBehavior(robotTxtBehavior);
				toReturn.setRobotTxtBehavior(new RobotTxtAwareBehavior());
			}
			else
			{
				toReturn.setRobotTxtBehavior(new RobotTxtUnawareBehavior());
			}
			
			
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return toReturn;
		
	}


	
	/**
	 * Method to run the crawl monitoring in a threaded manner
	 */
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
				activeThreadCount = ((ThreadPoolExecutor) crawlerThreadPool)
						.getActiveCount();

				// If somme threads are not active anymore
				if (activeThreadCount < ConfigSingleton.INSTANCE
						.getNumberOfCrawlerThreads())
				{
					// We check if the scheduledUrls are empty
					if (this.scheduledUrls.isEmpty())
					{
						// Ok so we print out a message and we stop.
						// TODO: Print a message

						shouldRun = false;

					}
					else
					{
						for (int i = activeThreadCount; i <= ConfigSingleton.INSTANCE
								.getNumberOfCrawlerThreads(); i++)
						{
							((ThreadPoolExecutor)this.crawlerThreadPool).execute(this.buildCrawler());
						}
					}

				}

			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		//TODO: Only stop if the flag is set in the config
		this.crawlerThreadPool.shutdownNow();		
	}

	

	
	
	
	//---------------GETTERS---------------------//
	
	public HttpConnectionManagerInterf getHttpConnectionManager()
	{
		return httpConnectionManager;
	}

	public IScheduledURLStore getScheduledUrls()
	{
		return scheduledUrls;
	}

	public IProcessedURLStore getProcessedUrls()
	{
		return processedUrls;
	}

	public IRobotTxtStore getRobotTxtData()
	{
		return robotTxtData;
	}
	
	
	
}
