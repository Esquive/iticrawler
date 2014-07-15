package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;


public class ScheduledUrlsQueue implements IScheduledURLStore
{
	private Queue<URLWrapper> scheduledLinks = null;
	private Lock readLock = null;
	private Lock writeLock = null;
	
	public ScheduledUrlsQueue()
	{
		this.scheduledLinks = new LinkedList<>();
		this.readLock = new ReentrantLock();
		this.writeLock = new ReentrantLock();
	}
	
	
	@Override
	public boolean isEmpty()
	{
		this.readLock.lock();

        try{
            return this.scheduledLinks.isEmpty();
        }
        finally
        {
            this.readLock.unlock();
        }
	}

	@Override
	public void scheduleURL(URLWrapper inURL)
	{
		this.writeLock.lock();
		try{
            this.scheduledLinks.add(inURL);
        }
        finally
        {
            this.writeLock.unlock();
        }

	}

	@Override
	public URLWrapper getNextURL()
	{
		this.readLock.lock();

        try {
            return this.scheduledLinks.poll();
        }
        finally {
            this.readLock.unlock();
        }

	}


	@Override
	public void scheduleUniqueUrl(URLWrapper inUrl)
	{
		this.readLock.lock();
        this.writeLock.lock();
        try
        {
		    if(!this.scheduledLinks.contains(inUrl))
		    {
			    this.readLock.unlock();
			
			    this.scheduledLinks.add(inUrl);
		    }
        }
        finally {
            this.writeLock.unlock();
            this.readLock.unlock();
        }
    }
	
}
