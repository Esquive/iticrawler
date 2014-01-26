package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;

public class ProcessedUrlsHashMap implements IProcessedURLStore
{
	private Map<String,Long> visitedUrls = null;
	private Set<String> currentlyVisitedUrls = null;
	
    private ReadWriteLock readWriteLock = null;
    private ReadWriteLock currentReadWriteLock = null;
	
	public ProcessedUrlsHashMap()
	{
		this.visitedUrls = new HashMap<>();
		this.currentlyVisitedUrls = new HashSet<>();
		
		this.readWriteLock = new ReentrantReadWriteLock(true);
		this.currentReadWriteLock = new ReentrantReadWriteLock(true);
	}
	
	
	@Override
	public void addProcessedURL(URLWrapper inURL)
	{
		this.readWriteLock.writeLock().lock();
			
		this.visitedUrls.put(inURL.toString(), null);
		
		this.readWriteLock.writeLock().unlock();
	}

	
	@Override
	public void addProcessedHost(URLWrapper inURL, Long lastProcessed)
	{
		this.readWriteLock.writeLock().lock();

		this.visitedUrls.put(inURL.getDomain(), lastProcessed);
		
		this.readWriteLock.writeLock().unlock();

	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		boolean toReturn = false;
		
		this.readWriteLock.readLock().lock();
		
		toReturn = this.visitedUrls.containsKey(inURL.toString());
		
		this.readWriteLock.readLock().unlock();
	
		return toReturn;
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		this.readWriteLock.readLock().lock();

		Long toReturn = this.visitedUrls.get(inURL.getDomain());
		
		if(toReturn == null)
		{
			toReturn = new Long(-1);
		}
		
		this.readWriteLock.readLock().unlock();
		
		return toReturn;
	}



	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		boolean toReturn = false;
		
		this.currentReadWriteLock.readLock().lock();
		
		toReturn = this.currentlyVisitedUrls.contains(inUrl.toString());
		
		this.currentReadWriteLock.readLock().unlock();
		
		return toReturn;
	}


	@Override
	public void addCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentReadWriteLock.writeLock().lock();
		
		this.currentlyVisitedUrls.add(inUrl.toString());
		
		this.currentReadWriteLock.writeLock().unlock();
	}


	@Override
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentReadWriteLock.writeLock().lock();
		
		this.currentlyVisitedUrls.remove(inUrl.toString());
		
		this.currentReadWriteLock.writeLock().unlock();
		
	}	
}
