package com.itiniu.iticrawler.crawler.frontier.impl.memory;

import java.util.Queue;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistQueueConfig;
import com.itiniu.iticrawler.crawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class DistributedScheduledUrlsQueue implements IScheduledURLStore
{

	private Queue<URLInfo> scheduledLinks;

	public DistributedScheduledUrlsQueue(Config cfg)
	{
		new DistQueueConfig().setup(cfg, "SCHED");

		this.scheduledLinks = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getQueue("SCHED");
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

	@Override
	public boolean isEmpty()
	{
		return this.scheduledLinks.isEmpty();
	}

}
