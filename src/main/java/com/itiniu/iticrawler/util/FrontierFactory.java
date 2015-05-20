package com.itiniu.iticrawler.util;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.frontier.impl.Frontier;
import com.itiniu.iticrawler.crawler.frontier.impl.memory.DistributedCrawledUrlsMap;
import com.itiniu.iticrawler.crawler.frontier.impl.memory.DistributedScheduledUrlsQueue;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.crawler.frontier.inte.IFrontier;
import com.itiniu.iticrawler.crawler.frontier.inte.IScheduledURLStore;

/**
 * Created by eric on 03.12.14.
 */
public class FrontierFactory {

    public static IFrontier getFrontier() {

        IFrontier frontier = new Frontier();
        ICrawledURLStore crawled = null;
        IScheduledURLStore scheduled = null;


        crawled = new DistributedCrawledUrlsMap(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());
        scheduled = new DistributedScheduledUrlsQueue(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());

        //TODO: Change it again and use the constructor.
        frontier.setCrawledURLStore(crawled);
        frontier.setScheduledURLStore(scheduled);
        return frontier;
    }
}
