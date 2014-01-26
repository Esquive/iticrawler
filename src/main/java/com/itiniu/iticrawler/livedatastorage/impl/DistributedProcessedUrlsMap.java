package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;

//TODO: Do some experiments on thread safety of the distributed datasctructures.
//Maybe I can have an overall performance gain if I sychronize each localy
public class DistributedProcessedUrlsMap implements IProcessedURLStore {

	//Since the Distributed Set implementation of hazelcast does not provide
	//A disk swap feature I use a map instead of the Set. 
	//As current practice in Cassandra I only use the "Colunmname" to store the value of interest.
	//Lets see how the swap works on null values, I might come back and write a dist Set implementation.
	//Therefore the TODO: Write a Distributed Set implementation to allow disk swapping.
	Map<String,Long> processedURLs;
	Map<String,Object> currentlyProcessedURLs;
	
	
	public DistributedProcessedUrlsMap(Config cfg)
	{
		//Setup the maps
		new DistMapConfig().setup(cfg, "WP_URL").setup(cfg, "IP_URL");
		
		this.processedURLs = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("WP_URL");
		this.currentlyProcessedURLs = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("IP_URL");
	}
	
	
	@Override
	public void addProcessedURL(URLWrapper inURL) {
	
		this.processedURLs.put(inURL.toString(), null);

	}

	@Override
	public void addProcessedHost(URLWrapper inURL, Long lastProcessed) {
		
		this.processedURLs.put(inURL.getDomain(), lastProcessed);

	}

	@Override
	public boolean wasProcessed(URLWrapper inURL) {
		return this.processedURLs.containsKey(inURL.toString());
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL) {
		Long toReturn = this.processedURLs.get(inURL.getDomain());
		
		if(toReturn == null)
		{
			toReturn = new Long(-1);
		}
		
		return toReturn;
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl) {
		return this.currentlyProcessedURLs.containsKey(inUrl.toString());
	}

	@Override
	public void addCurrentlyProcessedUrl(URLWrapper inUrl) {
		this.currentlyProcessedURLs.put(inUrl.toString(), null);

	}

	@Override
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl) {
		this.currentlyProcessedURLs.remove(inUrl.toString());
	}

}
