package com.itiniu.iticrawler.frontier.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.util.eviction.lfu.LFUCache;

public class ProcessedUrlsSwapHashMap implements IProcessedURLStore
{

	private int memoryMaxStorage;

	private Map<Integer, Boolean> processedUrls = null;
	private Set<Integer> currentlyProcessedUrls = null;
	private Map<Integer, Long> processedHosts = null;

	private AtomicInteger processedUrlsCount = null;
	private AtomicInteger processedHostsCount = null;
	private AtomicInteger currentlyProcessedCounter = null;

	private ProcessedUrlsFileStore fileSwap = null;
	private Executor writeBehindPool = null;

	private ReadWriteLock rwLock = null;
	private ReadWriteLock crawledHostWriteLock = null;
	private ReadWriteLock currentReadWriteLock = null;

	//TODO: add a parameter for switching eviction algorithms
	public ProcessedUrlsSwapHashMap(int maxStorageSize)
	{
		this.memoryMaxStorage = maxStorageSize;

		this.processedUrls = new LFUCache<>(this.memoryMaxStorage);
		this.currentlyProcessedUrls = new HashSet<>();
		this.processedHosts = new LFUCache<>(this.memoryMaxStorage);

		this.processedUrlsCount = new AtomicInteger(0);
		this.processedHostsCount = new AtomicInteger(0);
		this.currentlyProcessedCounter = new AtomicInteger(0);

		this.fileSwap = new ProcessedUrlsFileStore();
		this.writeBehindPool = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads());

		this.rwLock = new ReentrantReadWriteLock(true);
		this.crawledHostWriteLock = new ReentrantReadWriteLock(true);
		this.currentReadWriteLock = new ReentrantReadWriteLock(true);
	}

	@Override
	public void addProcessedURL(final URLWrapper inURL)
	{
		this.rwLock.writeLock().lock();
		try
		{
			this.processedUrls.put(inURL.hashCode(), Boolean.TRUE);
			this.processedUrlsCount.incrementAndGet();

			// All elements are written to disk in a write behind fashion.
			this.writeBehindPool.execute(new Runnable() {

				@Override
				public void run()
				{
					fileSwap.addProcessedURL(inURL);
				}
			});
		}
		finally
		{
			this.rwLock.writeLock().unlock();
		}
	}

	@Override
	public void addProcessedHost(final URLWrapper inURL, final Long lastProcessed)
	{
		this.crawledHostWriteLock.writeLock().lock();
		try
		{
			this.processedHosts.put(inURL.hashCode(), lastProcessed);
			this.processedHostsCount.incrementAndGet();

			this.writeBehindPool.execute(new Runnable() {

				@Override
				public void run()
				{
					fileSwap.addProcessedHost(inURL, lastProcessed);
				}
			});
		}
		finally
		{
			this.crawledHostWriteLock.writeLock().unlock();
		}
	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		this.rwLock.readLock().lock();
		try
		{
			boolean wasProcessed = this.processedUrls.containsKey(inURL.hashCode());
			wasProcessed = this.fileSwap.wasProcessed(inURL);
			
			if(wasProcessed)
			{
				this.reloadUrlToMemory(inURL);
			}
			
			return wasProcessed;
		}
		finally
		{
			this.rwLock.readLock().unlock();
		}
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		this.crawledHostWriteLock.readLock().lock();
		try
		{
			Long time = this.processedHosts.get(inURL.getDomain().hashCode());
			time = this.fileSwap.lastHostProcessing(inURL);
			if (time != null)
			{
				this.reloadHostToMemory(inURL, time);
			}
			
			return time;
		}
		finally
		{
			this.crawledHostWriteLock.readLock().unlock();
		}
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentReadWriteLock.readLock().lock();
		try
		{
			boolean contains = this.currentlyProcessedUrls.contains(inUrl.hashCode());
			return contains ? contains : this.fileSwap.isCurrentlyProcessedUrl(inUrl);
		}
		finally
		{
			this.currentReadWriteLock.readLock().unlock();
		}
		
	}

	@Override
	public void addCurrentlyProcessedUrl(final URLWrapper inUrl)
	{
		this.currentReadWriteLock.writeLock().lock();
		try
		{
			if (this.memoryMaxStorage <= this.currentlyProcessedCounter.get())
			{
				this.writeBehindPool.execute(new Runnable() {

					@Override
					public void run()
					{
						fileSwap.addCurrentlyProcessedUrl(inUrl);
					}
				});
			}
			else
			{
				this.currentlyProcessedUrls.add(inUrl.hashCode());
			}
			
			this.currentlyProcessedCounter.incrementAndGet();

		}
		finally
		{
			this.currentReadWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void removeCurrentlyProcessedUrl(final URLWrapper inUrl)
	{
		this.currentReadWriteLock.writeLock().lock();
		try
		{
			this.writeBehindPool.execute(new Runnable() {

				@Override
				public void run()
				{
					fileSwap.removeCurrentlyProcessedUrl(inUrl);
				}
			});
			this.currentlyProcessedUrls.remove(inUrl.hashCode());
			this.currentlyProcessedCounter.decrementAndGet();
		}
		finally
		{
			this.currentReadWriteLock.writeLock().unlock();
		}

	}

	@Override
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount)
	{
		if(this.processedHostsCount.get() < maxHostCount)
			return true;
		
		if(this.processedHostsCount.get() == maxHostCount)
		{
			if(this.processedHosts.containsKey(inUrl.getDomain().hashCode()) || this.fileSwap.canCrawlHost(inUrl, maxHostCount))
				return true;
		}
		
		return false;
	}

	private void reloadHostToMemory(URLWrapper url, long lastProcessing)
	{
		this.crawledHostWriteLock.readLock().unlock();
		this.crawledHostWriteLock.writeLock().lock();
		try
		{
			this.processedHosts.put(url.getDomain().hashCode(),lastProcessing);
		}
		finally
		{
			this.crawledHostWriteLock.writeLock().unlock();
			this.crawledHostWriteLock.readLock().lock();
		}
	}
	
	private void reloadUrlToMemory(URLWrapper url)
	{
		this.rwLock.readLock().unlock();
		this.rwLock.writeLock().lock();
		try
		{
			this.processedUrls.put(url.hashCode(),Boolean.TRUE);
		}
		finally
		{
			this.rwLock.writeLock().unlock();
			this.rwLock.readLock().lock();
		}
	}
	
	
}
