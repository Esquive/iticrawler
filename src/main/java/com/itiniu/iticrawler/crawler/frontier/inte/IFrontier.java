package com.itiniu.iticrawler.crawler.frontier.inte;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import java.util.Collection;

/**
 * @author Eric Falk <erfalk at gmail dot com>
 */
public interface IFrontier {

    public void scheduleURL(URLInfo url);

    public URLInfo getNextURL();

    public Collection<URLInfo> getNextURLs(int count);

    public void addCrawledHost(URLInfo url);

    public void addCrawledURL(URLInfo url);

    public void addCurrentlyCrawledURL(URLInfo url);

    public void removeCurrentlyCrawledURL(URLInfo url);

    public boolean wasURLCrawled(URLInfo url);

    public Long getLastHostProcessingTimeStamp(URLInfo url);

    public boolean isURLCurrentlyCrawled(URLInfo url);

    public boolean canCrawlURL(URLInfo url);

    public boolean isScheduleEmpty();

    public void setCrawledURLStore(ICrawledURLStore crawled);

    public void setScheduledURLStore(IScheduledURLStore scheduled);

}
