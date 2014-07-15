package com.itiniu.iticrawler.livedatastorage.impl;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;

public class ProcessedUrlsFileStore implements IProcessedURLStore
{
	DB db = null;
	Set<Integer> processedUrls = null;
	Set<Integer> currentlyProcessedUrls = null;
	Map<Integer, Long> processedHosts = null;

	public ProcessedUrlsFileStore()
	{
		db = DBMaker.newFileDB(new File("storage/frontier.db")).asyncWriteEnable().make();

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
