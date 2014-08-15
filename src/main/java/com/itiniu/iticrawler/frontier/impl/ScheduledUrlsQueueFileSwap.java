package com.itiniu.iticrawler.frontier.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ScheduledUrlsQueueFileSwap implements IScheduledURLStore
{
	private int maxSize = 0;
	private boolean appendToMem = true;
	private DB db = null;
	private Queue<URLWrapper> scheduled = null;
	private Queue<URLWrapper> fileScheduled = null;

	private ReentrantLock rLock = null;
	private ReentrantLock wLock = null;

	public ScheduledUrlsQueueFileSwap(int maxSize)
	{
		this.maxSize = maxSize;

		this.db = FileStorageConfig.INSTANCE.getStorageProvider();
		this.fileScheduled = db.getQueue("scheduled");
		this.scheduled = new LinkedList<>();

		this.rLock = new ReentrantLock(true);
		this.wLock = new ReentrantLock(true);
	}

	@Override
	public void scheduleURL(URLWrapper url)
	{
		this.wLock.lock();
		try
		{
			this.fileScheduled.add(url);
			this.db.commit();
			if (this.appendToMem)
			{
				this.scheduled.add(url);
				if (this.maxSize == this.scheduled.size())
				{
					this.appendToMem = false;
				}
			}
		}
		finally
		{
			this.wLock.unlock();
		}
	}

	@Override
	public URLWrapper getNextURL()
	{
		this.rLock.lock();
		try
		{

			if (this.scheduled.size() == 0)
			{
				for (int i = 0; i < this.maxSize; i++)
				{
					if (this.fileScheduled.isEmpty())
					{
						break;
					}
					this.scheduled.add(this.fileScheduled.poll());
					this.db.commit();
				}
				this.appendToMem = true;

				return this.scheduled.poll();
			}
			else
			{
				this.fileScheduled.poll();
				this.db.commit();
				return this.scheduled.poll();
			}
		}
		finally
		{
			this.rLock.unlock();
		}

	}

	@Override
	public boolean isEmpty()
	{
		this.rLock.lock();
		this.wLock.lock();
		try
		{
			return this.scheduled.isEmpty() ? this.scheduled.isEmpty() : this.fileScheduled.isEmpty();
		}
		finally
		{
			this.wLock.unlock();
			this.rLock.unlock();
		}
	}

}
