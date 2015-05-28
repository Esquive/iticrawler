package com.itiniu.iticrawler.crawler.frontier;

import com.itiniu.iticrawler.httptools.impl.URLInfo;

import java.util.Collection;

/**
 * @author Eric Falk <erfalk at gmail dot com>
 */
public interface Frontier {

    void scheduleURL(URLInfo url);

    URLInfo getNextURL();

    Collection<URLInfo> getNextURLs(int count);

    void addCrawledHost(URLInfo url);

    void addCrawledURL(URLInfo url);

    void addCurrentlyCrawledURL(URLInfo url);

    void removeCurrentlyCrawledURL(URLInfo url);

    boolean wasURLCrawled(URLInfo url);

    Long getLastHostProcessingTimeStamp(URLInfo url);

    boolean isURLCurrentlyCrawled(URLInfo url);

    boolean canCrawlURL(URLInfo url);

    boolean isScheduleEmpty();

    void setCrawledURLStore(CrawledURLCache crawled);

    void setScheduledURLStore(ScheduledURLCache scheduled);

}
