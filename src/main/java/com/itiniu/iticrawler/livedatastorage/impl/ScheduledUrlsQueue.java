package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.Collection;
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
//		System.out.println(Thread.currentThread().getName() + " : aquired readlock of Queue");
		
		boolean toReturn = this.scheduledLinks.isEmpty();
		
		this.readLock.unlock();
//		System.out.println(Thread.currentThread().getName() + " : released readlock of Queue");
		
		return toReturn;
	}

	@Override
	public void scheduleURL(URLWrapper inURL)
	{
		this.writeLock.lock();
//		System.out.println(Thread.currentThread().getName() + " : aquired writelock of Queue");
		
		this.scheduledLinks.add(inURL);
		this.writeLock.unlock();
//		System.out.println(Thread.currentThread().getName() + " : released writelock of Queue");
		
	}

	@Override
	public URLWrapper getNextURL()
	{
		this.readLock.lock();
//		System.out.println(Thread.currentThread().getName() + " : aquired readlock of Queue");
		
		URLWrapper toReturn = this.scheduledLinks.poll();
		
		this.readLock.unlock();
//		System.out.println(Thread.currentThread().getName() + " : released readlock of Queue");
		
		return toReturn;
	}

	@Override
	public Collection<URLWrapper> getNextURLRange(int number)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void scheduleUniqueUrl(URLWrapper inUrl)
	{
		this.readLock.lock();
		
		if(!this.scheduledLinks.contains(inUrl))
		{
			this.readLock.unlock();
			
			this.writeLock.lock();
//			System.out.println(Thread.currentThread().getName() + " : aquired writelock of Queue");
			
			this.scheduledLinks.add(inUrl);
			
			this.writeLock.unlock();
//			System.out.println(Thread.currentThread().getName() + " : released writelock of Queue");
			
		}
		else
		{
			this.readLock.unlock();
		}		
	}
	
}
