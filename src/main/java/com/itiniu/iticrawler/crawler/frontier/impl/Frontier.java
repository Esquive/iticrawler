package com.itiniu.iticrawler.crawler.frontier.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.frontier.inte.IFrontier;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.crawler.frontier.inte.IScheduledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * * @author Eric Falk <erfalk at gmail dot com> on 03.12.14.
 */
public class Frontier implements IFrontier {

    private ICrawledURLStore crawled = null;
    private IScheduledURLStore scheduled = null;

    public Frontier(ICrawledURLStore crawled, IScheduledURLStore scheduled) {
        this.crawled = crawled;
        this.scheduled = scheduled;
    }

    public Frontier() {

    }

    @Override
    public void scheduleURL(URLInfo url) {
        this.scheduled.scheduleURL(url);
    }

    @Override
    public URLInfo getNextURL() {
        return this.scheduled.getNextURL();
    }

    @Override
    public synchronized Collection<URLInfo> getNextURLs(int count) {
        List<URLInfo> urls = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            urls.add(this.scheduled.getNextURL());
        }
        return urls;
    }

    @Override
    public void addCrawledHost(URLInfo url) {
        this.crawled.addProcessedHost(url, System.currentTimeMillis());
    }

    @Override
    public void addCrawledURL(URLInfo url) {
        this.crawled.addProcessedURL(url);
    }

    @Override
    public void addCurrentlyCrawledURL(URLInfo url) {
        this.crawled.addCurrentlyProcessedUrl(url);
    }

    @Override
    public void removeCurrentlyCrawledURL(URLInfo url) {
        this.crawled.removeCurrentlyProcessedUrl(url);
    }

    @Override
    public boolean wasURLCrawled(URLInfo url) {
        return this.crawled.wasProcessed(url);
    }

    @Override
    public Long getLastHostProcessingTimeStamp(URLInfo url) {
        return this.crawled.lastHostProcessing(url);
    }

    @Override
    public boolean isURLCurrentlyCrawled(URLInfo url) {
        return this.crawled.isCurrentlyProcessedUrl(url);
    }

    @Override
    public synchronized boolean canCrawlURL(URLInfo url) {

        if (((url.getUrlDepth() + 1) * -1) <= ConfigSingleton.INSTANCE.getMaxCrawlDepth()) return true;

        if (this.crawled.getHostCount() < ConfigSingleton.INSTANCE.getMaxHostsToCrawl()) {
            return true;
        } else if (this.crawled.getHostCount() == ConfigSingleton.INSTANCE.getMaxHostsToCrawl()) {
            return this.crawled.containsHost(url);
        }

        return false;
    }

    @Override
    public boolean isScheduleEmpty() {
        return this.scheduled.isEmpty();
    }

    @Override
    public void setCrawledURLStore(ICrawledURLStore crawled) {
        this.crawled = crawled;
    }

    @Override
    public void setScheduledURLStore(IScheduledURLStore scheduled) {
        this.scheduled = scheduled;
    }
}
