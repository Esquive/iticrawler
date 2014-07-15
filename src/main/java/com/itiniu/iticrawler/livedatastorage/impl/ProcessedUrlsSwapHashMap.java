package com.itiniu.iticrawler.livedatastorage.impl;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ProcessedUrlsSwapHashMap extends ProcessedUrlsHashMap
{
	public ProcessedUrlsSwapHashMap()
	{
		super();
	}
	
	
	@Override
	public void addProcessedURL(URLWrapper inURL)
	{
		this.readWriteLock.writeLock().lock();
		try
		{
			super.addProcessedURL(inURL);
			//TODO: write behind the data to disk
		}
		finally
		{
			this.readWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void addProcessedHost(URLWrapper inURL, Long lastProcessed)
	{
		this.currentReadWriteLock.writeLock().lock();
		try
		{
			super.addProcessedHost(inURL, lastProcessed);
			//TODO: SWAP TO DISK
		}
		finally
		{
			this.currentReadWriteLock.writeLock().unlock();
		}
	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		this.crawledHostWriteLock.readLock().lock();
		try
		{
			Long time = super.lastHostProcessing(inURL);
			
			//TODO: if the result is null, Check on the swap
			
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentReadWriteLock.writeLock().lock();
		try
		{
			super.addCurrentlyProcessedUrl(inUrl);
			//TODO: SWAP
		}
		finally
		{
			this.currentReadWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentReadWriteLock.readLock().lock();
		try
		{
			super.removeCurrentlyProcessedUrl(inUrl);
			//TODO: currentReadWriteLock;
		}
		finally
		{
			this.currentReadWriteLock.readLock().unlock();
		}
	}

	@Override
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
