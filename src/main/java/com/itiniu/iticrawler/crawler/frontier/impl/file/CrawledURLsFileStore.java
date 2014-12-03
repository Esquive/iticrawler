package com.itiniu.iticrawler.crawler.frontier.impl.file;

import java.util.Map;
import java.util.Set;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;

public class CrawledURLsFileStore implements ICrawledURLStore
{
	protected DB db = null;
	protected Set<String> crawledURLs = null;
	protected Set<String> currentlyCrawledURLs = null;
	protected Map<String, Long> crawledHosts = null;

	public CrawledURLsFileStore()
	{
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();

		this.crawledURLs = db.getHashSet("crawledUrls");
		this.currentlyCrawledURLs = db.getHashSet("currentlyCrawled");
		this.currentlyCrawledURLs.clear();
		this.crawledHosts = db.getHashMap("crawledHosts");
	}

	@Override
	public void addProcessedURL(URLInfo inURL)
	{
		this.crawledURLs.add(inURL.toString());
		db.commit();
	}

	@Override
	public void addProcessedHost(URLInfo inURL, Long lastProcessed)
	{
		this.crawledHosts.put(inURL.getDomain(), lastProcessed);
		db.commit();
	}

	@Override
	public boolean wasProcessed(URLInfo inURL)
	{
		return this.crawledURLs.contains(inURL.hashCode());
	}

	@Override
	public Long lastHostProcessing(URLInfo inURL)
	{
		return this.crawledHosts.get(inURL.getDomain().hashCode());
	}

	@Override
	public boolean isCurrentlyProcessedUrl(URLInfo inUrl)
	{
		return this.currentlyCrawledURLs.contains(inUrl.hashCode());
	}

	@Override
	public void addCurrentlyProcessedUrl(URLInfo inUrl)
	{
		this.currentlyCrawledURLs.add(inUrl.toString());
		db.commit();
	}

	@Override
	public void removeCurrentlyProcessedUrl(URLInfo inUrl)
	{
		this.currentlyCrawledURLs.remove(inUrl.hashCode());
	}

	@Override
	public int getHostCount() {
		return this.crawledHosts.size();
	}

	@Override
	public boolean containsHost(URLInfo url) {
		return this.crawledHosts.containsKey(url.getDomain());
	}
}
