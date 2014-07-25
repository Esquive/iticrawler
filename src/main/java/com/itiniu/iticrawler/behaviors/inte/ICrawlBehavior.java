package com.itiniu.iticrawler.behaviors.inte;

import com.itiniu.iticrawler.crawler.PageExtractionType;
import com.itiniu.iticrawler.crawler.impl.Page;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

/**
 * Method to implement in order to run the crawling. All the methods get called
 * during the crawl process. Implementing this interface makes it possible to
 * interact with the crawl process. It is assured that the input page of the
 * first is also the input page of the next methods. </br> </br> BE AWARE: The
 * class implementing this interface must have a default Constructor in order to
 * be initialized. </br> </br>1: {@link ICrawlBehavior#handleStatuScode(Page)}
 * gets called first. </br>2: {@link ICrawlBehavior#handleContentSize(Page)}
 * gets called second. </br>3: {@link ICrawlBehavior#processPage(Page)} gets
 * called third. </br>4: {@link ICrawlBehavior#handleOutgoingURLs(Page)} gets
 * called forth. </br>5: {@link ICrawlBehavior#shouldScheduleURL(URLWrapper)}
 * gets called fifth as many times as the page has outgoing URLs.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public interface ICrawlBehavior
{
	/**
	 * Implementing this method allows to log the status code of the page. When
	 * this method gets called during the crawling process, the status code of
	 * the page is set.
	 * 
	 * @param page
	 */
	public void handleStatuScode(Page page);

	/**
	 * Implementing this method allows to be aware of the content size. When
	 * this method gets called during the crawling process, the status code and
	 * the content size is set. </br></br> If the content length cannot be
	 * fetched -1 is returned. If the length reaches {@link Long#MAX_VALUE} -1
	 * is returned. The page might not be extractable using the
	 * {@link PageExtractionType#BY_STRING} config. But surely using streams
	 * with the {@link PageExtractionType#BY_STREAM} config.
	 * </br></br>
	 * Use {@link Page#getContentLength()}. 
	 * 
	 * @param page
	 */
	public void handleContentSize(Page page);

	/**
	 * Implementing this method allows to extract the content of the page. When
	 * this method gets called during the crawling process, the status code and
	 * the content is set. </br> </br> The content can be accessed: </br>
	 * </br>-Either using the methods: {@link Page#getInStream()} or
	 * {@link Page#writeToOutputStream(java.io.OutputStream)} if you chose the
	 * {@link PageExtractionType#BY_STREAM} setting. </br>-Either using the
	 * method: {@link Page#getHtml()} if you chose the
	 * {@link PageExtractionType#BY_STRING} setting.
	 * 
	 * @param page
	 */
	public void processPage(Page page);

	/**
	 * Implementing this method allows to handle all the outgoing URLs of the
	 * page, for instance for back linking metrics. When this method gets called
	 * during the crawling process, the page carries the status code, the
	 * content size and the outgoing URLs. </br></br> The method:
	 * {@link Page#getOutgoingURLs()} can be used. </br></br> BE AWARE: Should
	 * you manipulate the collection of outgoing URLs at that stage: (for
	 * example removing some) They will not be considered in the scheduling
	 * process!
	 * </br></br>
	 * Use {@link Page#getOutgoingURLs()}.
	 * 
	 * @param page
	 * @param url
	 * @return
	 */
	public void handleOutgoingURLs(Page page);

	/**
	 * Implementing this method allows to determine if an URL should be
	 * scheduled.
	 * 
	 * @param page
	 * @param url
	 * @return
	 */
	public boolean shouldScheduleURL(URLWrapper url);
}
