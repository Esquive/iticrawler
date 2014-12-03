package com.itiniu.iticrawler.crawler.frontier.impl.memory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.itiniu.iticrawler.crawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class ScheduledURLsQueue implements IScheduledURLStore
{
	private Queue<URLInfo> scheduledLinks = null;

	public ScheduledURLsQueue()
	{
		this.scheduledLinks = new ConcurrentLinkedQueue<>();
	}

	@Override
	public boolean isEmpty()
	{
		return this.scheduledLinks.isEmpty();
	}

	@Override
	public void scheduleURL(URLInfo inURL)
	{
		this.scheduledLinks.add(inURL);
	}

	@Override
	public URLInfo getNextURL()
	{
		return this.scheduledLinks.poll();
	}

}
