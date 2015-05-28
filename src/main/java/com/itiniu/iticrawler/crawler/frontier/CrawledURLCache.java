package com.itiniu.iticrawler.crawler.frontier;

import com.itiniu.iticrawler.httptools.impl.URLInfo;

/**
 * Interface defining the methods for the part of the frontier that tracks the URLs already crawled,
 * currently crawled and the list of crawled hosts.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface CrawledURLCache {

	/**
	 * Method to add an Processed URL. 
	 * </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	void addProcessedURL(URLInfo url);

	/**
	 * Method to add an processed host along with the timestamp of the last processing.
	 *  </br> MUST BE THREAD SAFE </br>
	 * 
	 * @param url
	 * @param lastProcessed
	 */
	void addProcessedHost(URLInfo url, Long lastProcessed);
	
	/**
	 * Method to determine if an URL was processed already.
	 *  </br> MUST BE THREAD SAFE </br>
	 * 
	 * @param url
	 * @return
	 */
	boolean wasProcessed(URLInfo url);
	
	/**
	 * Method to determine at what time the host was crawled for the last time.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 * @return the timestamp of the last processing
	 */
	Long lastHostProcessing(URLInfo url);
	
	/**
	 * Method to determine if the URL is currently processed.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 * @return true if the URL is currently crawled by another thread false otherwise.
	 */
	boolean isCurrentlyProcessedUrl(URLInfo url);
	
	/**
	 * Add an URL that is currently processed.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	void addCurrentlyProcessedUrl(URLInfo url);
	
	/**
	 * Remove an URL from the currently processed list.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	void removeCurrentlyProcessedUrl(URLInfo url);

	int getHostCount();

	boolean containsHost(URLInfo url);

}
