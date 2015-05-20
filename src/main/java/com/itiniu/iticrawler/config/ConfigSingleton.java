package com.itiniu.iticrawler.config;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.crawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.util.PageExtractionType;
import com.itiniu.iticrawler.util.eviction.EvictionPolicy;

@SuppressWarnings("unchecked")
public enum ConfigSingleton
{
	INSTANCE;
	
	ConfigSingleton()
	{
		loadConfigFromFile();
	}
	
	private final Logger LOG = LogManager.getLogger(ConfigSingleton.class);
	
	public void loadConfigFromFile()
	{
		try
		{
			LOG.info("ITICRAWLER: Loading properties!");
			Configuration config = new PropertiesConfiguration("crawler.properties");
			Iterator<String> keyIt = config.getKeys();
			String key = "";
			while (keyIt.hasNext())
			{
				key = keyIt.next();
				switch (key)
				{
					case "http.maxconnections":
						this.maxConnections = config.getInt(key);
						break;
					case "http.maxconnectionsperhost":
						this.maxConnectionsPerHost = config.getInt(key);
						break;
					case "http.sockettimeout":
						this.socketTimeout = config.getInt(key);
						break;
					case "http.connectiontimeout":
						this.connectionTimeout = config.getInt(key);
						break;
					case "frontier.eviction.policy":
						this.eviction = EvictionPolicy.valueOf(config.getString(key));
						break;
					case "frontier.eviction.maxelements":
						this.maxInMemoryElements = config.getInt(key);
						break;
					case "crawler.threads":
						this.numberOfCrawlerThreads = config.getInt(key);
						break;
					case "crawler.considerrobottxt":
						this.considerRobotTxt = config.getBoolean(key);
						break;
					case "crawler.politnessdealy":
						this.politnessDelay = config.getInt(key);
						break;
					case "crawler.maxcrawldepth":
						this.maxCrawlDepth = config.getInt(key);
						break;
					case "crawler.useragent":
						this.userAgent = config.getString(key);
						break;
					case "crawler.maxhosts":
						this.maxHostsToCrawl = config.getInt(key);
						break;
					case "crawler.followredirect":
						this.followRedirect = config.getBoolean(key);
						break;
					case "crawler.pageextraction":
						this.extractionType = PageExtractionType.valueOf(config.getString(key));
						break;
					case "crawler.crawlbehavior":
						this.customCrawlBehavior = (Class<? extends ICrawlBehavior>) Class.forName(config.getString(key));
						break;
				}
			}

		}
		catch (ConfigurationException e)
		{
			LOG.warn("Could not load the properties file. Please make sure everything is configured properly.");
		}
		catch (ClassNotFoundException e)
		{
			LOG.error("The class specified as crawl behavior is not accessible, please review your settings");
		}

	}

	// Http Connection relevant
	private int maxConnections = 100;
	private int maxConnectionsPerHost = 100;
	private int socketTimeout = 20000;
	private int connectionTimeout = 30000;

	private ClusterConfig clusterConfig;

	public void setMaxConnections(int param)
	{
		maxConnections = param;
	}

	public int getMaxConnections()
	{
		return maxConnections;
	}

	public void setMaxConnectionsPerHost(int param)
	{
		maxConnectionsPerHost = param;
	}

	public int getMaxConnectionsPerHost()
	{
		return maxConnectionsPerHost;
	}

	public int getSocketTimeout()
	{
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout)
	{
		this.socketTimeout = socketTimeout;
	}

	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	// ---------------------------------------------------------------------------------------------------------------

	// Real-time Data-Storage relevant


	private EvictionPolicy eviction = EvictionPolicy.LRU;
	private int maxInMemoryElements = 100;


	public ClusterConfig getClusterConfig()
	{
		return this.clusterConfig;
	}



	public int getMaxInMemoryElements()
	{
		return maxInMemoryElements;
	}

	public void setMaxInMemoryElements(int maxInMemoryElements)
	{
		this.maxInMemoryElements = maxInMemoryElements;
	}

	public EvictionPolicy getEviction()
	{
		return eviction;
	}

	public void setEviction(EvictionPolicy eviction)
	{
		this.eviction = eviction;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	// Crawling relevant
	private int numberOfCrawlerThreads = 10;
	private boolean considerRobotTxt = true;
	private int politnessDelay = 1000;
	private int maxCrawlDepth = -1;
	private String userAgent = "itiCrawler";
	private boolean stopOnInactivity = false;
	private PageExtractionType extractionType = PageExtractionType.BY_STREAM;
	private boolean followRedirect = true;
	private int maxHostsToCrawl = 0;

	private Class<? extends ICrawlBehavior> customCrawlBehavior = null;

	public int getNumberOfCrawlerThreads()
	{
		return numberOfCrawlerThreads;
	}

	public void setNumberOfCrawlerThreads(int numberOfCrawlerThreads)
	{
		this.numberOfCrawlerThreads = numberOfCrawlerThreads;
	}

	public boolean isConsiderRobotTxt()
	{
		return considerRobotTxt;
	}

	public void setConsiderRobotTxt(boolean considerRobotTxt)
	{
		this.considerRobotTxt = considerRobotTxt;
	}

	public int getPolitnessDelay()
	{
		return politnessDelay;
	}

	public void setPolitnessDelay(int politnessDelay)
	{
		this.politnessDelay = politnessDelay;
	}

	public String getUserAgent()
	{
		return userAgent;
	}

	public void setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
	}

	public int getMaxCrawlDepth()
	{
		return maxCrawlDepth;
	}

	public void setMaxCrawlDepth(int maxCrawDepth)
	{
		this.maxCrawlDepth = maxCrawDepth;
	}

	public Class<? extends ICrawlBehavior> getCustomCrawlBehavior()
	{
		Class<? extends ICrawlBehavior> toReturn = customCrawlBehavior;

		return toReturn;
	}

	public void setCustomCrawlBehavior(Class<? extends ICrawlBehavior> customCrawlBehavior)
	{
		this.customCrawlBehavior = customCrawlBehavior;
	}

	public boolean isStopOnInactivity()
	{
		return stopOnInactivity;
	}

	public void setStopOnInactivity(boolean stopOnInactivity)
	{
		this.stopOnInactivity = stopOnInactivity;
	}

	public PageExtractionType getExtractionType()
	{
		return extractionType;
	}

	public void setExtractionType(PageExtractionType extractionType)
	{
		this.extractionType = extractionType;
	}

	public boolean isFollowRedirect()
	{
		return followRedirect;
	}

	public void setFollowRedirect(boolean followRedirect)
	{
		this.followRedirect = followRedirect;
	}

	public int getMaxHostsToCrawl()
	{
		return maxHostsToCrawl;
	}

	public void setMaxHostsToCrawl(int maxHostsToCrawl)
	{
		this.maxHostsToCrawl = maxHostsToCrawl;
	}

}
