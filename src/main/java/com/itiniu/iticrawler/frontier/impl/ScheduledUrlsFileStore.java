package com.itiniu.iticrawler.frontier.impl;

import java.util.Queue;

import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ScheduledUrlsFileStore implements IScheduledURLStore
{
	DB db = null;
	Queue<URLWrapper> scheduled = null;

	public ScheduledUrlsFileStore()
	{
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();
		this.scheduled = db.getQueue("scheduled");
	}

	@Override
	public void scheduleURL(URLWrapper inURL)
	{
		this.scheduled.add(inURL);
	}

	@Override
	public URLWrapper getNextURL()
	{
		URLWrapper toReturn = this.scheduled.poll();
		db.commit();
		return toReturn;
	}

	@Override
	public boolean isEmpty()
	{
		return this.scheduled.isEmpty();
	}

}
