package com.itiniu.iticrawler.crawler.frontier.impl.memory;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.ClusterConfig;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.util.CrawledURLStore;

public class DistributedCrawledUrlsMap implements ICrawledURLStore {

    //TODO: Reconsider the hashcode approach
    private static final String CRAWLED_URL_MAP = "CRAWLED_URL_MAP";
    private static final String CURRENTLY_CRAWLED_URL_MAP = "CURRENTLY_CRAWLED_URL_MAP";
    private static final String CRAWLED_HOST_MAP = "CRAWLED_HOST_MAP";

    Map<Integer, URLInfo> processedURLs;
    Map<Integer, Character> currentlyProcessedURLs;
    Map<String, Long> processedHosts;

    public DistributedCrawledUrlsMap(ClusterConfig cfg) {
        // Setup the maps
        new DistMapConfig().setup(cfg.getMemoryClusterConfig(), CRAWLED_URL_MAP, new CrawledURLStore(cfg.getStorageClusterConfig()))
                .setup(cfg.getMemoryClusterConfig(), CURRENTLY_CRAWLED_URL_MAP, null)
                .setup(cfg.getMemoryClusterConfig(), CRAWLED_HOST_MAP, null);

        this.processedURLs = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getMap(CRAWLED_URL_MAP);
        this.currentlyProcessedURLs = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getMap(CURRENTLY_CRAWLED_URL_MAP);
        this.processedHosts = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getMap(CRAWLED_HOST_MAP);
    }

    @Override
    public void addProcessedURL(URLInfo inURL) {
        this.processedURLs.put(new Integer(inURL.hashCode()), inURL);
    }

    @Override
    public void addProcessedHost(URLInfo inURL, Long lastProcessed) {
        this.processedHosts.put(inURL.getDomain(), new Long(System.currentTimeMillis()));
    }

    @Override
    public boolean wasProcessed(URLInfo inURL) {
        return this.processedURLs.containsKey(new Integer(inURL.hashCode()));
    }

    @Override
    public Long lastHostProcessing(URLInfo inURL) {
        return this.processedHosts.get(inURL.getDomain());
    }

    @Override
    public boolean isCurrentlyProcessedUrl(URLInfo inUrl) {
        return this.currentlyProcessedURLs.containsKey(new Integer(inUrl.hashCode()));
    }

    @Override
    public void addCurrentlyProcessedUrl(URLInfo inUrl) {
        this.currentlyProcessedURLs.put(new Integer(inUrl.hashCode()), new Character('0'));
    }

    @Override
    public void removeCurrentlyProcessedUrl(URLInfo inUrl) {
        this.currentlyProcessedURLs.remove(new Integer(inUrl.hashCode()));
    }

    @Override
    public int getHostCount() {
        return 0;
    }

    @Override
    public boolean containsHost(URLInfo url) {
        return false;
    }

}
