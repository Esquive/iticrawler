package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;

public class DistributedProcessedUrlsMap implements IProcessedURLStore
{

	Map<Integer, Character> processedURLs;
	Map<Integer, Character> currentlyProcessedURLs;
	Map<String, Long> processedHosts;

	public DistributedProcessedUrlsMap(Config cfg)
	{
		// Setup the maps
		new DistMapConfig().setup(cfg, "PH_URL").setup(cfg, "P_URL").setup(cfg, "CP_URL");

		this.processedURLs = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("P_URL");
		this.currentlyProcessedURLs = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("CP_URL");
		this.processedHosts = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("PH_URL");
	}

	@Override
	public void addProcessedURL(URLWrapper inURL)
	{
		 this.processedURLs.put(new Integer(inURL.hashCode()), new Character('0'));
	}

	@Override
	public void addProcessedHost(URLWrapper inURL, Long lastProcessed)
	{
		this.processedHosts.put(inURL.getDomain(), new Long(System.currentTimeMillis()));
	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		return this.processedURLs.containsKey(new Integer(inURL.hashCode()));
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		return this.processedHosts.get(inURL.getDomain());
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		return this.currentlyProcessedURLs.containsKey(new Integer(inUrl.hashCode()));
	}

	@Override
	public void addCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentlyProcessedURLs.put(new Integer(inUrl.hashCode()), new Character('0'));
	}

	@Override
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentlyProcessedURLs.remove(new Integer(inUrl.hashCode()));
	}

	@Override
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount)
	{
		if (maxHostCount == 0 || this.processedHosts.containsKey(inUrl.getDomain()))
		{
			return true;
		}
		else
		{
			if (this.processedHosts.size() < maxHostCount)
			{
				return true;
			}

			return false;
		}
	}

}
