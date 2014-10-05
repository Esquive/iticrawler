package com.itiniu.iticrawler.frontier.impl;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ScheduledUrlsFileStore implements IScheduledURLStore
{
	DB db = null;
	Queue<URLWrapper> scheduled = null;
	Lock lock = null;

	public ScheduledUrlsFileStore()
	{
		this.lock = new ReentrantLock(true);
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();
		this.scheduled = db.getQueue("scheduled");
	}

	@Override
	public void scheduleURL(URLWrapper inURL)
	{
		this.lock.lock();
		try
		{
			this.scheduled.add(inURL);
			this.db.commit();
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public URLWrapper getNextURL()
	{
		this.lock.lock();
		try
		{
			URLWrapper toReturn = this.scheduled.poll();
			this.db.commit();
			return toReturn;
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public boolean isEmpty()
	{
		return this.scheduled.isEmpty();
	}

}
