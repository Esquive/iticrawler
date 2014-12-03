package com.itiniu.iticrawler.crawler.frontier.impl.file;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.crawler.frontier.inte.IScheduledURLStore;

public class ScheduledUrlsFileStore implements IScheduledURLStore
{
	DB db = null;
	Queue<URLInfo> scheduled = null;
	Lock lock = null;

	public ScheduledUrlsFileStore()
	{
		this.lock = new ReentrantLock(true);
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();
		this.scheduled = db.getQueue("scheduled");
	}

	@Override
	public void scheduleURL(URLInfo inURL)
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
	public URLInfo getNextURL()
	{
		this.lock.lock();
		try
		{
			URLInfo toReturn = this.scheduled.poll();
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
