package com.itiniu.iticrawler.factories;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.frontier.*;
import com.itiniu.iticrawler.crawler.frontier.ScheduledURLCache;

/**
 * Created by eric on 03.12.14.
 */
public class FrontierFactory {

    private FrontierFactory()
    {}

    public static Frontier getFrontier() {

        Frontier frontier = new DistributedFrontier();
        CrawledURLCache crawled = null;
        ScheduledURLCache scheduled = null;


        crawled = new DistributedCrawledURLCache(ConfigSingleton.INSTANCE.getClusterConfig());
        scheduled = new DistributedScheduledURLCache(ConfigSingleton.INSTANCE.getClusterConfig());

        frontier.setCrawledURLStore(crawled);
        frontier.setScheduledURLStore(scheduled);
        return frontier;
    }
}
