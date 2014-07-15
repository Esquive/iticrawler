package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.Queue;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistQueueConfig;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;

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
