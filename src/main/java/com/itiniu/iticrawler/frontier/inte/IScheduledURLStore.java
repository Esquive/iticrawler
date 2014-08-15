package com.itiniu.iticrawler.frontier.inte;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;

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
	public void scheduleURL(URLWrapper url);

	/**
	 * Returns the next URL to crawl.
	 * 
	 * @return
	 */
	public URLWrapper getNextURL();

	/**
	 * Check if the frontier is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty();

}
