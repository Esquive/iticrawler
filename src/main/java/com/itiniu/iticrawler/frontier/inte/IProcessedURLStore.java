package com.itiniu.iticrawler.frontier.inte;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;

/**
 * Interface defining the methods for the part of the frontier that tracks the URLs already crawled,
 * currently crawled and the list of crawled hosts.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface IProcessedURLStore {

	/**
	 * Method to add an Processed URL. 
	 * </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	public void addProcessedURL(URLWrapper url);

	/**
	 * Method to add an processed host along with the timestamp of the last processing.
	 *  </br> MUST BE THREAD SAFE </br>
	 * 
	 * @param url
	 * @param lastProcessed
	 */
	public void addProcessedHost(URLWrapper url, Long lastProcessed);
	
	/**
	 * Method to determine if an URL was processed already.
	 *  </br> MUST BE THREAD SAFE </br>
	 * 
	 * @param url
	 * @return
	 */
	public boolean wasProcessed(URLWrapper url);
	
	/**
	 * Method to determine at what time the host was crawled for the last time.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 * @return the timestamp of the last processing
	 */
	public Long lastHostProcessing(URLWrapper url);
	
	/**
	 * Method to determine if the URL is currently processed.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 * @return true if the URL is currently crawled by another thread false otherwise.
	 */
	public boolean isCurrentlyProcessedUrl(URLWrapper url);
	
	/**
	 * Add an URL that is currently processed.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	public void addCurrentlyProcessedUrl(URLWrapper url);
	
	/**
	 * Remove an URL from the currently processed list.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	public void removeCurrentlyProcessedUrl(URLWrapper url);
	
	/**
	 * Method to determine if the URL can be crawled according to the confirgured max host count.
	 *  </br> MUST BE THREAD SAFE </br> 
	 * @param url
	 * @param maxHostCount
	 * @return true if the max number of hosts is not reached. False otherwise.
	 */
	public boolean canCrawlHost(URLWrapper url, int maxHostCount);
	
}
