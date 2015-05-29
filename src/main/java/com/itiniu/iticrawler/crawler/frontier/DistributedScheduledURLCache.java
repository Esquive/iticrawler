package com.itiniu.iticrawler.crawler.frontier;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IQueue;
import com.itiniu.iticrawler.config.ClusterConfig;
import com.itiniu.iticrawler.config.DistQueueConfig;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.util.ScheduledURLStore;

public class DistributedScheduledURLCache implements ScheduledURLCache {
    private static final String QUEUE_NAME = "SCHEDULED_URL";


    private BlockingQueue<URLInfo> scheduledLinks;

    public DistributedScheduledURLCache(ClusterConfig cfg) {
        new DistQueueConfig().setup(cfg.getMemoryClusterConfig(), QUEUE_NAME, new ScheduledURLStore(cfg.getStorageClusterConfig()));

        this.scheduledLinks = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getQueue(QUEUE_NAME);
    }

    @Override
    public void scheduleURL(URLInfo inURL) {
        try {
            this.scheduledLinks.put(inURL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public URLInfo getNextURL() {
        URLInfo toReturn = null;

        try {
            toReturn = this.scheduledLinks.poll(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public boolean isEmpty() {
        return this.scheduledLinks.isEmpty();
    }

}
