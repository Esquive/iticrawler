package com.itiniu.iticrawler.livedatastorage.impl;

import java.io.File;
import java.util.Queue;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;

public class ScheduledUrlsFileStore implements IScheduledURLStore
{
	DB db = null;
	Queue<URLWrapper> scheduled = null;
	
	public ScheduledUrlsFileStore()
	{
		this.db = DBMaker.newFileDB(new File("storage/frontier.db")).asyncWriteEnable().make();
		this.scheduled = db.getQueue("scheduled");
	}
	
	@Override
	public void scheduleURL(URLWrapper inURL)
	{
		this.scheduled.add(inURL);
	}

	@Override
	public void scheduleUniqueUrl(URLWrapper inUrl)
	{
		if(!this.scheduled.contains(inUrl))
		{
			this.scheduled.add(inUrl);
			this.db.commit();
		}	
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
