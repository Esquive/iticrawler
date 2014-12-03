package com.itiniu.iticrawler.crawler.frontier.impl.file;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.util.eviction.EvictionPolicy;
import com.itiniu.iticrawler.util.eviction.lfu.LFUCache;
import com.itiniu.iticrawler.util.eviction.lru.LRUCache;

public class CrawledURLsSwapHashMap implements ICrawledURLStore
{

	private int memoryMaxStorage;

	private Map<String, Boolean> crawledURLs = null;
	private Set<Integer> currentlyProcessedUrls = null;
	private Map<Integer, Long> processedHosts = null;

	private AtomicInteger processedUrlsCount = null;
	private AtomicInteger processedHostsCount = null;
	private AtomicInteger currentlyProcessedCounter = null;

	private CrawledURLsFileStore fileSwap = null;
	private Executor writeBehindPool = null;

	private ReentrantLock puLock = null;
	private ReentrantLock phLock = null;
	private ReadWriteLock cpLock = null;
	

	public CrawledURLsSwapHashMap(int maxStorageSize, EvictionPolicy eviction)
	{
		this.memoryMaxStorage = maxStorageSize;

		this.currentlyProcessedUrls = new HashSet<>();
		if (eviction == EvictionPolicy.LFU)
		{
			this.processedHosts = new LFUCache<>(this.memoryMaxStorage);
			this.crawledURLs = new LFUCache<>(this.memoryMaxStorage);
		}
		else if (eviction == EvictionPolicy.LRU)
		{
			this.processedHosts = new LRUCache<>(this.memoryMaxStorage);
			this.crawledURLs = new LRUCache<>(this.memoryMaxStorage);
		}

		this.processedUrlsCount = new AtomicInteger(0);
		this.processedHostsCount = new AtomicInteger(0);
		this.currentlyProcessedCounter = new AtomicInteger(0);

		this.fileSwap = new CrawledURLsFileStore();
		this.writeBehindPool = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads());

		this.phLock = new ReentrantLock(true);
		this.puLock = new ReentrantLock(true);
		this.cpLock = new ReentrantReadWriteLock(true);
	}

	@Override
	public void addProcessedURL(final URLInfo inURL)
	{
		this.puLock.lock();
		try
		{
			this.crawledURLs.put(inURL.toString(), Boolean.TRUE);
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
			this.puLock.unlock();
		}
	}

	@Override
	public void addProcessedHost(final URLInfo inURL, final Long lastProcessed)
	{
		this.phLock.lock();
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
			this.phLock.unlock();
		}
	}

	@Override
	public boolean wasProcessed(URLInfo inURL)
	{
		this.puLock.lock();
		try
		{
			boolean wasProcessed = this.crawledURLs.containsKey(inURL.hashCode());
			wasProcessed = this.fileSwap.wasProcessed(inURL);

			if (wasProcessed)
			{
				this.reloadUrlToMemory(inURL);
			}

			return wasProcessed;
		}
		finally
		{
			this.puLock.unlock();
		}
	}

	@Override
	public Long lastHostProcessing(URLInfo inURL)
	{
		this.phLock.lock();
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
			this.phLock.unlock();
		}
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLInfo inUrl)
	{
		this.cpLock.readLock().lock();
		try
		{
			boolean contains = this.currentlyProcessedUrls.contains(inUrl.hashCode());
			return contains ? contains : this.fileSwap.isCurrentlyProcessedUrl(inUrl);
		}
		finally
		{
			this.cpLock.readLock().unlock();
		}

	}

	@Override
	public void addCurrentlyProcessedUrl(final URLInfo inUrl)
	{
		this.cpLock.writeLock().lock();
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
			this.cpLock.writeLock().unlock();
		}
	}

	@Override
	public void removeCurrentlyProcessedUrl(final URLInfo inUrl)
	{
		this.cpLock.writeLock().lock();
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
			this.cpLock.writeLock().unlock();
		}

	}

	@Override
	public int getHostCount() {
		return 0;
	}

	@Override
	public boolean containsHost(URLInfo url) {
		return false;
	}



	private void reloadHostToMemory(URLInfo url, long lastProcessing)
	{
		this.phLock.lock();
		try
		{
			this.processedHosts.put(url.getDomain().hashCode(), lastProcessing);
		}
		finally
		{
			this.phLock.unlock();
		}
	}

	private void reloadUrlToMemory(URLInfo url)
	{
		this.puLock.lock();
		try
		{
			this.crawledURLs.put(url.toString(), Boolean.TRUE);
		}
		finally
		{
			this.puLock.unlock();
		}
	}

}
