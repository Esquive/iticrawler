package com.itiniu.iticrawler.behaviors.inte;

import com.itiniu.iticrawler.crawler.PageExtractionType;
import com.itiniu.iticrawler.crawler.impl.Page;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

/**
 * Method to implement in order to run the crawling. All the methods get called
 * during the crawl process. Implementing this interface makes it possible to
 * interact with the crawl process.
 * It is assured that the input page of the first is also the input page of the next methods. 
 * </br> </br>
 * BE AWARE: The class implementing this interface must have a default Constructor in order to be initialized.
 * </br>
 * </br>1: {@link ICrawlBehavior#handleStatuScode(Page)} gets called first.
 * </br>2: {@link ICrawlBehavior#processPage(Page)} gets called second.
 * </br>3: {@link ICrawlBehavior#shouldScheduleURL(Page, URLWrapper)} gets called second.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public interface ICrawlBehavior
{
	/**
	 * Implementing this method allows to log the status code of the page.
	 * When this method gets called during the crawling process, the status code of the page is set.
	 * 
	 * @param page
	 */
	public void handleStatuScode(Page page);
	
	/**
	 * Implementing this method allows to extract the content of the page.
	 * When this method gets called during the crawling process, the status code and the content is set. 
	 * </br>
	 * </br>
	 * The content can be accessed:
	 * </br>
	 * </br>-Either using the methods: {@link Page#getInStream()} or {@link Page#writeToOutputStream(java.io.OutputStream)} if you chose the {@link PageExtractionType#BY_STREAM} setting.
	 * </br>-Either using the method: {@link Page#getHtml()} if you chose the {@link PageExtractionType#BY_STRING} setting.
	 * 
	 * @param page
	 */
	public void processPage(Page page);
	
	/**
	 * Implementing this method allows to determine if an URL should be scheduled.
	 * When this method gets called during the crawling process, the page carries the outgoing URLs.
	 * 
	 * @param page
	 * @param url
	 * @return
	 */
	public boolean shouldScheduleURL(Page page, URLWrapper url);
}
