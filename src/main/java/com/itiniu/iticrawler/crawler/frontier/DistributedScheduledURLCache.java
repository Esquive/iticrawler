package com.itiniu.iticrawler.crawler.frontier;

import java.util.Queue;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.ClusterConfig;
import com.itiniu.iticrawler.config.DistQueueConfig;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class DistributedScheduledURLCache implements ScheduledURLCache
{
	private static final String QUEUE_NAME = "SCHEDULED_URL";


	private Queue<URLInfo> scheduledLinks;

	public DistributedScheduledURLCache(Config cfg)
	{
		new DistQueueConfig().setup(cfg, QUEUE_NAME);

		this.scheduledLinks = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getQueue(QUEUE_NAME);
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
