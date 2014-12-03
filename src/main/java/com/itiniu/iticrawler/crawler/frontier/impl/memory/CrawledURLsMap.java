package com.itiniu.iticrawler.crawler.frontier.impl.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class CrawledURLsMap implements ICrawledURLStore {
    protected Map<String, Long> crawledUrls = null;
    protected Set<String> currentlyCrawledUrls = null;
    protected Map<String, Long> crawledHosts = null;

    protected ReadWriteLock crawledUrlRWLock = null;
    protected ReadWriteLock currentCrawledRWLock = null;
    protected ReadWriteLock crawledHostRWLock = null;


    public CrawledURLsMap() {
        this.crawledUrls = new HashMap<>();
        this.currentlyCrawledUrls = new HashSet<>();
        this.crawledHosts = new HashMap<>();

        this.crawledUrlRWLock = new ReentrantReadWriteLock(true);
        this.currentCrawledRWLock = new ReentrantReadWriteLock(true);
        this.crawledHostRWLock = new ReentrantReadWriteLock(true);
    }

    @Override
    public void addProcessedURL(URLInfo inURL) {
        this.crawledUrlRWLock.writeLock().lock();
        try {
            this.crawledUrls.put(inURL.toString(), null);
        } finally {
            this.crawledUrlRWLock.writeLock().unlock();
        }
    }


    @Override
    public void addProcessedHost(URLInfo inURL, Long lastProcessed) {
        this.crawledHostRWLock.writeLock().lock();

        try {
            this.crawledHosts.put(inURL.getDomain(), lastProcessed);
        } finally {
            this.crawledHostRWLock.writeLock().unlock();
        }
    }

    @Override
    public boolean wasProcessed(URLInfo inURL) {
        this.crawledUrlRWLock.readLock().lock();
        try {
            return this.crawledUrls.containsKey(inURL.toString());
        } finally {
            this.crawledUrlRWLock.readLock().unlock();
        }
    }

    @Override
    public Long lastHostProcessing(URLInfo inURL) {
        this.crawledHostRWLock.readLock().lock();
        try {

            Long toReturn = this.crawledUrls.get(inURL.getDomain());

            if (toReturn == null) {
                toReturn = new Long(-1);
            }
            return toReturn;
        } finally {
            this.crawledHostRWLock.readLock().unlock();
        }

    }

    @Override
    public boolean isCurrentlyProcessedUrl(URLInfo inUrl) {
        this.currentCrawledRWLock.readLock().lock();
        try {
            return this.currentlyCrawledUrls.contains(inUrl.toString());
        } finally {
            this.currentCrawledRWLock.readLock().unlock();
        }
    }

    @Override
    public void addCurrentlyProcessedUrl(URLInfo inUrl) {
        this.currentCrawledRWLock.writeLock().lock();
        try {
            this.currentlyCrawledUrls.add(inUrl.toString());
        } finally {
            this.currentCrawledRWLock.writeLock().unlock();
        }
    }

    @Override
    public void removeCurrentlyProcessedUrl(URLInfo inUrl) {
        this.currentCrawledRWLock.writeLock().lock();
        try {

            this.currentlyCrawledUrls.remove(inUrl.toString());
        } finally {
            this.currentCrawledRWLock.writeLock().unlock();
        }
    }

    public int getHostCount() {
        this.crawledHostRWLock.readLock().lock();
        try {
            return this.crawledHosts.size();
        } finally {
            this.crawledHostRWLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsHost(URLInfo url) {
       return this.crawledHosts.containsKey(url.getDomain());
    }

}
