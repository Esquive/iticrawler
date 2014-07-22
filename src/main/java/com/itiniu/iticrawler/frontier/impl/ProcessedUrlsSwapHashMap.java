package com.itiniu.iticrawler.frontier.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ProcessedUrlsSwapHashMap extends ProcessedUrlsHashMap
{
	private Executor writeBehindPool = null;
	//TODO: Use parameter
	private int memoryMaxStorage = 100;
	private ProcessedUrlsFileStore fileSwap = null;
	
	private AtomicInteger processedUrlsCount = null;
	private AtomicInteger processedHostsCount = null;
	private AtomicInteger currentlyProcessedCounter = null;
	
	
	public ProcessedUrlsSwapHashMap()
	{
		super();
		this.processedUrlsCount = new AtomicInteger(0);
		this.processedHostsCount = new AtomicInteger(0);
		this.currentlyProcessedCounter = new AtomicInteger(0);
		this.fileSwap = new ProcessedUrlsFileStore();
		this.writeBehindPool = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads());
	}
	
	@Override
	public void addProcessedURL(final URLWrapper inURL)
	{
		this.readWriteLock.writeLock().lock();
		try
		{
			if(this.memoryMaxStorage >= this.processedUrlsCount.get())
			{
				this.writeBehindPool.execute(
				new Runnable() {
					
					@Override
					public void run()
					{
						fileSwap.addProcessedURL(inURL);
					}
				});
			}
			else
			{
				super.addProcessedURL(inURL);
				this.processedUrlsCount.incrementAndGet();
			}
		}
		finally
		{
			this.readWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void addProcessedHost(final URLWrapper inURL, final Long lastProcessed)
	{
		this.crawledHostWriteLock.writeLock().lock();
		try
		{
			if(this.memoryMaxStorage >= this.processedHostsCount.get())
			{
				this.writeBehindPool.execute(
						new Runnable() {
							
							@Override
							public void run()
							{
								fileSwap.addProcessedHost(inURL, lastProcessed);
							}
						});
			}
			else
			{
				super.addProcessedHost(inURL, lastProcessed);
				this.processedHostsCount.incrementAndGet();
			}
			
		}
		finally
		{
			this.crawledHostWriteLock.writeLock().unlock();
		}
	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		boolean wasProcessed = super.wasProcessed(inURL);
		return (!wasProcessed) ? this.fileSwap.wasProcessed(inURL) : wasProcessed;
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		Long time = super.lastHostProcessing(inURL);
		return (time != null) ? time : this.fileSwap.lastHostProcessing(inURL);
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		boolean contains = super.isCurrentlyProcessedUrl(inUrl);
		return contains ? contains : this.fileSwap.isCurrentlyProcessedUrl(inUrl);
	}

	@Override
	public void addCurrentlyProcessedUrl(final URLWrapper inUrl)
	{
		this.currentReadWriteLock.writeLock().lock();
		try
		{
			if(this.memoryMaxStorage <= this.currentlyProcessedCounter.get())
			{
				this.writeBehindPool.execute(
						new Runnable() {
							
							@Override
							public void run()
							{
								fileSwap.addCurrentlyProcessedUrl(inUrl);
							}
						});
			}
			else
			{
				super.addCurrentlyProcessedUrl(inUrl);
				this.currentlyProcessedCounter.incrementAndGet();
			}
			
		}
		finally
		{
			this.currentReadWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void removeCurrentlyProcessedUrl(final URLWrapper inUrl)
	{
		this.writeBehindPool.execute(
				new Runnable() {
					
					@Override
					public void run()
					{
						fileSwap.removeCurrentlyProcessedUrl(inUrl);
					}
				});
		super.removeCurrentlyProcessedUrl(inUrl);
		
	}

	@Override
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount)
	{
		boolean canI = super.canCrawlHost(inUrl, maxHostCount);
		return canI ? canI : this.fileSwap.canCrawlHost(inUrl, maxHostCount);
	}

}
