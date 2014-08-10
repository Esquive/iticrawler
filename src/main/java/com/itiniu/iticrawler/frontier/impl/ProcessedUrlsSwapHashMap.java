package com.itiniu.iticrawler.frontier.impl;

import java.util.Collection;
import java.util.HashMap;
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
import com.itiniu.iticrawler.util.lfu.ContentNode;
import com.itiniu.iticrawler.util.lfu.LFUHeap;

public class ProcessedUrlsSwapHashMap implements IProcessedURLStore
{

	private int memoryMaxStorage;

	private Set<Integer> processedUrls = null;
	private Set<Integer> currentlyProcessedUrls = null;
	private Map<Integer, Long> processedHosts = null;

	private LFUHeap<Integer> processedUrlsHeap = null;
	private LFUHeap<Integer> processedHostsHeap = null;

	private AtomicInteger processedUrlsCount = null;
	private AtomicInteger processedHostsCount = null;
	private AtomicInteger currentlyProcessedCounter = null;

	private ProcessedUrlsFileStore fileSwap = null;
	private Executor writeBehindPool = null;

	private ReadWriteLock rwLock = null;
	private ReadWriteLock crawledHostWriteLock = null;
	private ReadWriteLock currentReadWriteLock = null;

	public ProcessedUrlsSwapHashMap(int maxStorageSize)
	{
		this.memoryMaxStorage = maxStorageSize;

		this.processedUrls = new HashSet<>();
		this.currentlyProcessedUrls = new HashSet<>();
		this.processedHosts = new HashMap<>();

		this.processedUrlsHeap = new LFUHeap<>();
		this.processedHostsHeap = new LFUHeap<>();

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
			if (this.memoryMaxStorage >= this.processedUrlsCount.get())
			{
				Collection<ContentNode<Integer>> toEvict = this.processedUrlsHeap.getNodesToEvict();
				for (ContentNode<Integer> cont : toEvict)
				{
					this.processedUrls.remove(cont.getContent());
					this.processedUrlsCount.decrementAndGet();
				}

			}

			// All elements are written to disk in a write behind fashion.
			this.writeBehindPool.execute(new Runnable() {

				@Override
				public void run()
				{
					fileSwap.addProcessedURL(inURL);
				}
			});

			this.processedUrls.add(inURL.hashCode());
			this.processedUrlsHeap.addNode(new ContentNode<Integer>(inURL.hashCode()));
			this.processedUrlsCount.incrementAndGet();
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
			if (this.memoryMaxStorage >= this.processedHostsCount.get())
			{
				Collection<ContentNode<Integer>> toEvict = this.processedHostsHeap.getNodesToEvict();
				for (ContentNode<Integer> cont : toEvict)
				{
					this.processedHosts.remove(cont.getContent());
					this.processedHostsCount.decrementAndGet();
				}
			}

			this.writeBehindPool.execute(new Runnable() {

				@Override
				public void run()
				{
					fileSwap.addProcessedHost(inURL, lastProcessed);
				}
			});

			this.processedHosts.put(inURL.getDomain().hashCode(), lastProcessed);
			this.processedHostsHeap.addNode(new ContentNode<Integer>(inURL.getDomain().hashCode()));
			this.processedHostsCount.incrementAndGet();

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
			//TODO: reload it in memory
			boolean wasProcessed = this.processedUrls.contains(inURL.hashCode());
			return (!wasProcessed) ? this.fileSwap.wasProcessed(inURL) : wasProcessed;
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
			//TODO: reload to memory
			Long time = this.processedHosts.get(inURL.getDomain().hashCode());
			return (time != null) ? time : this.fileSwap.lastHostProcessing(inURL);
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
		//TODO:
		return true;
	}

}
