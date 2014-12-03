package com.itiniu.iticrawler.crawler.frontier.inte;

import com.itiniu.iticrawler.httptools.impl.URLInfo;

/**
 * Interface defining the methods for the part of the frontier holding the
 * information about scheduled URLs.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public interface IScheduledURLStore
{
	/**
	 * Method for scheduling an URL.
	 * 
	 * @param url
	 */
	public void scheduleURL(URLInfo url);

	/**
	 * Returns the next URL to crawl.
	 * 
	 * @return
	 */
	public URLInfo getNextURL();

	/**
	 * Check if the frontier is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty();

}
