package com.itiniu.iticrawler.crawler.frontier.inte;

import com.itiniu.iticrawler.httptools.impl.URLInfo;

/**
 * Interface defining the methods for the part of the frontier that tracks the URLs already crawled,
 * currently crawled and the list of crawled hosts.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface ICrawledURLStore {

	/**
	 * Method to add an Processed URL. 
	 * </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	public void addProcessedURL(URLInfo url);

	/**
	 * Method to add an processed host along with the timestamp of the last processing.
	 *  </br> MUST BE THREAD SAFE </br>
	 * 
	 * @param url
	 * @param lastProcessed
	 */
	public void addProcessedHost(URLInfo url, Long lastProcessed);
	
	/**
	 * Method to determine if an URL was processed already.
	 *  </br> MUST BE THREAD SAFE </br>
	 * 
	 * @param url
	 * @return
	 */
	public boolean wasProcessed(URLInfo url);
	
	/**
	 * Method to determine at what time the host was crawled for the last time.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 * @return the timestamp of the last processing
	 */
	public Long lastHostProcessing(URLInfo url);
	
	/**
	 * Method to determine if the URL is currently processed.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 * @return true if the URL is currently crawled by another thread false otherwise.
	 */
	public boolean isCurrentlyProcessedUrl(URLInfo url);
	
	/**
	 * Add an URL that is currently processed.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	public void addCurrentlyProcessedUrl(URLInfo url);
	
	/**
	 * Remove an URL from the currently processed list.
	 *  </br> MUST BE THREAD SAFE </br>
	 * @param url
	 */
	public void removeCurrentlyProcessedUrl(URLInfo url);

	public int getHostCount();

	public boolean containsHost(URLInfo url);

}
