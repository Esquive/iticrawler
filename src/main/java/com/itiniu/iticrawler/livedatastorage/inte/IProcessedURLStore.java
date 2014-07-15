package com.itiniu.iticrawler.livedatastorage.inte;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface IProcessedURLStore {

	public void addProcessedURL(URLWrapper inURL);

	public void addProcessedHost(URLWrapper inURL, Long lastProcessed);
	
	public boolean wasProcessed(URLWrapper inURL);
	
	public Long lastHostProcessing(URLWrapper inURL);
	
	public boolean isCurrentlyProcessedUrl(URLWrapper inUrl);
	
	public void addCurrentlyProcessedUrl(URLWrapper inUrl);
	
	public void removeCurrentlyProcessedUrl(URLWrapper inUrl);
	
	public boolean canCrawlHost(URLWrapper inUrl, int maxHostCount);
	
}
