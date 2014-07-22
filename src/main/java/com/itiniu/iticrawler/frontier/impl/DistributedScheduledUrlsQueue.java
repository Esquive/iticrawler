package com.itiniu.iticrawler.frontier.impl;

import java.util.Queue;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistQueueConfig;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class DistributedScheduledUrlsQueue implements
		IScheduledURLStore{

	private Queue<URLWrapper> scheduledLinks;
	
	public DistributedScheduledUrlsQueue(Config cfg)
	{
		new DistQueueConfig().setup(cfg, "SCHED");
		
		this.scheduledLinks = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getQueue("SCHED");
	}

	@Override
	public void scheduleURL(URLWrapper inURL) {
		this.scheduledLinks.add(inURL);
	}

	@Override
	public void scheduleUniqueUrl(URLWrapper inUrl) {

		//TODO: Find another solution for the unique scheduling
		if(!this.scheduledLinks.contains(inUrl))
		{
			this.scheduledLinks.add(inUrl);
		}
	}

	@Override
	public URLWrapper getNextURL() {
		return  this.scheduledLinks.poll();
	}

	@Override
	public boolean isEmpty() {
		return this.scheduledLinks.isEmpty();
	}

}
