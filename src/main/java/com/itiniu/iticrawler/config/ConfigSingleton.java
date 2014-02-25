package com.itiniu.iticrawler.config;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.livedatastorage.LiveDataStoragePolicy;;

public enum ConfigSingleton
{
	INSTANCE;

	//Http Connection relevant
	private int maxConnections = 100;
	private int maxConnectionsPerHost = 100;
	private int socketTimeout = 20000;
	private int connectionTimeout = 30000;
	
	private ClusterConfig clusterConfig;
		
	public void setMaxConnections(int param) {
		maxConnections = param;
	}
	
	public int getMaxConnections()
	{
		return maxConnections;
	}

	public void setMaxConnectionsPerHost(int param) {
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


//---------------------------------------------------------------------------------------------------------------

	//Real-time Data-Storage relevant
	private LiveDataStoragePolicy scheduledUrlsStoragePolicy = LiveDataStoragePolicy.inMemory;
	private LiveDataStoragePolicy processedUrlsStoragePolicy = LiveDataStoragePolicy.inMemory;
	private LiveDataStoragePolicy robotTxtDataStoragePolicy  = LiveDataStoragePolicy.inMemory;

	
	public LiveDataStoragePolicy getScheduledUrlsStoragePolicy()
	{
		return scheduledUrlsStoragePolicy;
	}

	public void setScheduledUrlsStoragePolicy(
			LiveDataStoragePolicy scheduledUrlsStoragePolicy)
	{
		this.scheduledUrlsStoragePolicy = scheduledUrlsStoragePolicy;
		
		if(this.scheduledUrlsStoragePolicy == LiveDataStoragePolicy.cluster
				   && this.clusterConfig == null)
				{
					this.clusterConfig = new ClusterConfig();
				}
	}

	public LiveDataStoragePolicy getProcessedUrlsStoragePolicy()
	{
		return processedUrlsStoragePolicy;
	}

	public void setProcessedUrlsStoragePolicy(
			LiveDataStoragePolicy processedUrlsStoragePolicy)
	{
		this.processedUrlsStoragePolicy = processedUrlsStoragePolicy;
		
		if(this.processedUrlsStoragePolicy == LiveDataStoragePolicy.cluster
		   && this.clusterConfig == null)
		{
			this.clusterConfig = new ClusterConfig();
		}
	}

	public LiveDataStoragePolicy getRobotTxtDataStoragePolicy()
	{
		return robotTxtDataStoragePolicy;
	}

	public void setRobotTxtDataStoragePolicy(
			LiveDataStoragePolicy robotTxtDataStoragePolicy)
	{
		this.robotTxtDataStoragePolicy = robotTxtDataStoragePolicy;
		
		if(this.robotTxtDataStoragePolicy == LiveDataStoragePolicy.cluster
				   && this.clusterConfig == null)
				{
					this.clusterConfig = new ClusterConfig();
				}
		
	}
	
	public ClusterConfig getClusterConfig()
	{
		return this.clusterConfig;
	}
	
	
//---------------------------------------------------------------------------------------------------------------------
	
	//Crawling relevant
	private int numberOfCrawlerThreads = 10;
	private boolean considerRobotTxt = true;
	private int politnessDelay = 1000;
	private int maxCrawlDepth = -1;
	private String userAgent = "itiCrawler";
	private boolean stopOnInactivity = false;
	private boolean processAsStream = true;
	
	private Class<? extends ICrawlBehavior> customCrawlBehavior = null;
	private ReadWriteLock behaviorLock = new ReentrantReadWriteLock();
	

	
	
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
		behaviorLock.readLock().lock();
		Class<? extends ICrawlBehavior> toReturn = customCrawlBehavior;
		behaviorLock.readLock().unlock();
		
		return toReturn;
	}

	public void setCustomCrawlBehavior(
			Class<? extends ICrawlBehavior> customCrawlBehavior)
	{
		behaviorLock.writeLock().lock();
		this.customCrawlBehavior = customCrawlBehavior;
		behaviorLock.writeLock().unlock();
	}

	public boolean isStopOnInactivity() {
		return stopOnInactivity;
	}

	public void setStopOnInactivity(boolean stopOnInactivity) {
		this.stopOnInactivity = stopOnInactivity;
	}

	
	public boolean isProcessAsStream() {
		return processAsStream;
	}
	

	public void setProcessAsStream(boolean processAsStream) {
		this.processAsStream = processAsStream;
	}
		
	

}
