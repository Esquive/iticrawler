package com.itiniu.iticrawler.crawler.rotottxt.inte;

/**
 * Interface used for robots.txt directives stored inside the robots.txt storage.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface IRobotTxtDirective
{
	/**
	 * Method to add an explicit "allow" entry.
	 * 
	 * @param {@link String}
	 */
	public void addAllowEntry(String entry);
	
	/**
	 * Method to add an explicit "disallow" entry.
	 * 
	 * @param {@link String}
	 */
	public void addDisallowEntry(String entry);
	
	/**
	 * Call this method to determine if crawling of the provided URL is allowed.
	 * 
	 * @param {@link String}
	 * @return true if the URL can be crawled
	 */
	public boolean allows(String url);

	/**
	 * Method to add the crawl delay if provided by the robots.txt.
	 * 
	 * @param {@link String}
	 */
	public void addDelay(int delay);
	
	/**
	 * Returns the crawl dealy.
	 * 
	 * @param {@link String}
	 * @return The crawl delay in seconds.
	 */
	public int getDelay();
	
}
