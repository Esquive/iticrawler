package com.itiniu.iticrawler.util;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.frontier.impl.Frontier;
import com.itiniu.iticrawler.crawler.frontier.impl.file.CrawledURLsFileStore;
import com.itiniu.iticrawler.crawler.frontier.impl.file.CrawledURLsSwapHashMap;
import com.itiniu.iticrawler.crawler.frontier.impl.file.ScheduledUrlsFileStore;
import com.itiniu.iticrawler.crawler.frontier.impl.file.ScheduledUrlsQueueFileSwap;
import com.itiniu.iticrawler.crawler.frontier.impl.memory.CrawledURLsMap;
import com.itiniu.iticrawler.crawler.frontier.impl.memory.DistributedCrawledUrlsMap;
import com.itiniu.iticrawler.crawler.frontier.impl.memory.DistributedScheduledUrlsQueue;
import com.itiniu.iticrawler.crawler.frontier.impl.memory.ScheduledURLsQueue;
import com.itiniu.iticrawler.crawler.frontier.inte.ICrawledURLStore;
import com.itiniu.iticrawler.crawler.frontier.inte.IFrontier;
import com.itiniu.iticrawler.crawler.frontier.inte.IScheduledURLStore;

/**
 * Created by eric on 03.12.14.
 */
public class FrontierFactory  {

    public static IFrontier getFrontier() {

        IFrontier frontier = new Frontier();
        ICrawledURLStore crawled = null;
        IScheduledURLStore scheduled = null;

        //Getting the crawled store
        switch(ConfigSingleton.INSTANCE.getProcessedUrlsStoragePolicy())
            {
                case MEMORY:

                    crawled = new CrawledURLsMap();

                    break;

                case MEMORYCLUSTER:

                    crawled = new DistributedCrawledUrlsMap(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());

                    break;

                case FILE:

                    crawled = new CrawledURLsFileStore();

                    break;

                case MEMORY_FILE_SWAP:
                    crawled = new CrawledURLsSwapHashMap(ConfigSingleton.INSTANCE.getMaxInMemoryElements(), ConfigSingleton.INSTANCE.getEviction());
                    break;

                default:

                    crawled = new CrawledURLsMap();
                    break;
            }

        //getting the scheduled store
        switch(ConfigSingleton.INSTANCE.getScheduledUrlsStoragePolicy())
        {
            case MEMORY:

                scheduled = new ScheduledURLsQueue();

                break;

            case MEMORYCLUSTER:

                scheduled = new DistributedScheduledUrlsQueue(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());

                break;

            case FILE:

                scheduled = new ScheduledUrlsFileStore();

                break;

            case MEMORY_FILE_SWAP:

                scheduled = new ScheduledUrlsQueueFileSwap(ConfigSingleton.INSTANCE.getMaxInMemoryElements());
                break;

            default:

                scheduled = new ScheduledURLsQueue();

                break;
        }

        //TODO: Change it again and use the constructor.
        frontier.setCrawledURLStore(crawled);
        frontier.setScheduledURLStore(scheduled);
        return frontier;
    }
}
