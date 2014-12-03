package com.itiniu.iticrawler.crawler.frontier.impl.memory;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class DistributedCrawledUrlsMap implements ICrawledURLStore
{

	Map<Integer, Character> processedURLs;
	Map<Integer, Character> currentlyProcessedURLs;
	Map<String, Long> processedHosts;

	public DistributedCrawledUrlsMap(Config cfg)
	{
		// Setup the maps
		new DistMapConfig().setup(cfg, "PH_URL").setup(cfg, "P_URL").setup(cfg, "CP_URL");

		this.processedURLs = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("P_URL");
		this.currentlyProcessedURLs = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("CP_URL");
		this.processedHosts = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("PH_URL");
	}

	@Override
	public void addProcessedURL(URLInfo inURL)
	{
		 this.processedURLs.put(new Integer(inURL.hashCode()), new Character('0'));
	}

	@Override
	public void addProcessedHost(URLInfo inURL, Long lastProcessed)
	{
		this.processedHosts.put(inURL.getDomain(), new Long(System.currentTimeMillis()));
	}

	@Override
	public boolean wasProcessed(URLInfo inURL)
	{
		return this.processedURLs.containsKey(new Integer(inURL.hashCode()));
	}

	@Override
	public Long lastHostProcessing(URLInfo inURL)
	{
		return this.processedHosts.get(inURL.getDomain());
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLInfo inUrl)
	{
		return this.currentlyProcessedURLs.containsKey(new Integer(inUrl.hashCode()));
	}

	@Override
	public void addCurrentlyProcessedUrl(URLInfo inUrl)
	{
		this.currentlyProcessedURLs.put(new Integer(inUrl.hashCode()), new Character('0'));
	}

	@Override
	public void removeCurrentlyProcessedUrl(URLInfo inUrl)
	{
		this.currentlyProcessedURLs.remove(new Integer(inUrl.hashCode()));
	}

	@Override
	public int getHostCount() {
		return 0;
	}

	@Override
	public boolean containsHost(URLInfo url) {
		return false;
	}

}
