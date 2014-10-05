package com.itiniu.iticrawler.frontier.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ProcessedUrlsHashMap implements IProcessedURLStore
{
	protected Map<String,Long> crawledUrls = null;
	protected Set<String> currentlyCrawledUrls = null;
	protected Map<String, Long> crawledHosts = null;
	
    protected ReadWriteLock crawledUrlRWLock = null;
    protected ReadWriteLock currentCrawledRWLock = null;
    protected ReadWriteLock crawledHostRWLock = null;
    
    
	public ProcessedUrlsHashMap()
	{
		this.crawledUrls = new HashMap<>();
		this.currentlyCrawledUrls = new HashSet<>();
		this.crawledHosts = new HashMap<>();
		
		this.crawledUrlRWLock = new ReentrantReadWriteLock(true);
		this.currentCrawledRWLock = new ReentrantReadWriteLock(true);
		this.crawledHostRWLock = new ReentrantReadWriteLock(true);
	}
	
	@Override
	public void addProcessedURL(URLWrapper inURL)
	{
		this.crawledUrlRWLock.writeLock().lock();
		try
        {
		this.crawledUrls.put(inURL.toString(), null);
        }
        finally
        {
		this.crawledUrlRWLock.writeLock().unlock();
        }
    }

	
	@Override
	public void addProcessedHost(URLWrapper inURL, Long lastProcessed)
	{
		this.crawledHostRWLock.writeLock().lock();

        try
        {
        	this.crawledHosts.put(inURL.getDomain(), lastProcessed);
        }
        finally
        {
		this.crawledHostRWLock.writeLock().unlock();
        }
	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		this.crawledUrlRWLock.readLock().lock();
        try
        {
		    return this.crawledUrls.containsKey(inURL.toString());
        }
        finally
        {
		    this.crawledUrlRWLock.readLock().unlock();
        }
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		this.crawledHostRWLock.readLock().lock();
        try
        {

		Long toReturn = this.crawledUrls.get(inURL.getDomain());
		
		if(toReturn == null)
		{
			toReturn = new Long(-1);
		}
            return toReturn;
        }
		finally
        {
		this.crawledHostRWLock.readLock().unlock();
        }

	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentCrawledRWLock.readLock().lock();
	    try
        {
		    return this.currentlyCrawledUrls.contains(inUrl.toString());
        }
		finally {
            this.currentCrawledRWLock.readLock().unlock();
        }
	}

	@Override
	public void addCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentCrawledRWLock.writeLock().lock();
		try
        {
		this.currentlyCrawledUrls.add(inUrl.toString());
        }
        finally {
            this.currentCrawledRWLock.writeLock().unlock();
        }
	}

	@Override
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentCrawledRWLock.writeLock().lock();
        try{
		
		this.currentlyCrawledUrls.remove(inUrl.toString());
        }
        finally {
            this.currentCrawledRWLock.writeLock().unlock();
        }
	}
	
	public int getHostCount()
	{
		this.crawledHostRWLock.readLock().lock();
		try
		{
			return this.crawledHosts.size();
		}
		finally
		{
			this.crawledHostRWLock.readLock().unlock();
		}
	}

	@Override
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount)
	{
		this.crawledHostRWLock.readLock().lock();
		try
		{
			if (maxHostCount == 0 || this.crawledHosts.containsKey(inUrl.getDomain()))
			{
				return true;
			}
			else
			{
				if (this.crawledHosts.size() < maxHostCount)
				{
					return true;
				}

				return false;
			}
		}
		finally
		{
			this.crawledHostRWLock.readLock().unlock();
		}
	}	
}
