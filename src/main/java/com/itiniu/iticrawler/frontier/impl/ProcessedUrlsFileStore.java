package com.itiniu.iticrawler.frontier.impl;

import java.util.Map;
import java.util.Set;

import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class ProcessedUrlsFileStore implements IProcessedURLStore
{
	protected DB db = null;
	protected Set<Integer> processedUrls = null;
	protected Set<Integer> currentlyProcessedUrls = null;
	protected Map<Integer, Long> processedHosts = null;

	public ProcessedUrlsFileStore()
	{
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();

		this.processedUrls = db.getHashSet("processedUrls");
		this.currentlyProcessedUrls = db.getHashSet("currentlyProcessed");
		this.processedHosts = db.getHashMap("processedHosts");
	}

	@Override
	public void addProcessedURL(URLWrapper inURL)
	{
		this.processedUrls.add(inURL.hashCode());
		db.commit();
	}

	@Override
	public void addProcessedHost(URLWrapper inURL, Long lastProcessed)
	{
		this.processedHosts.put(inURL.hashCode(), lastProcessed);
		db.commit();
	}

	@Override
	public boolean wasProcessed(URLWrapper inURL)
	{
		return this.processedUrls.contains(inURL.hashCode());
	}

	@Override
	public Long lastHostProcessing(URLWrapper inURL)
	{
		return this.processedHosts.get(inURL.hashCode());
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		return this.currentlyProcessedUrls.contains(inUrl.hashCode());
	}

	@Override
	public void addCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentlyProcessedUrls.add(inUrl.hashCode());
		db.commit();
	}

	@Override
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl)
	{
		this.currentlyProcessedUrls.remove(inUrl.hashCode());
	}

	@Override
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount)
	{
		if (maxHostCount == 0)
		{
			return true;
		}

		if (maxHostCount < this.processedHosts.size())
		{
			return true;
		}
		else if (maxHostCount == this.processedHosts.size())
		{
			return this.processedHosts.containsKey(inUrl.getDomain().hashCode());
		}
		return false;
	}

}
