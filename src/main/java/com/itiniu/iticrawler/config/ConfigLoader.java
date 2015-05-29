package com.itiniu.iticrawler.config;

import com.itiniu.iticrawler.behaviors.crawler.CrawlBehavior;
import com.itiniu.iticrawler.util.enums.EvictionPolicy;
import com.itiniu.iticrawler.util.enums.PageExtractionType;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * Created by ericfalk on 29/05/15.
 */
public class ConfigLoader {

    private static final Logger LOG = LogManager.getLogger(ConfigLoader.class);
    private boolean fileConfigLoaded = false;
    private boolean clusterConfigLoaded = false;

    public ConfigLoader loadConfigFromFile()
    {
        LOG.info("Loading Configuration File: crawler.properties");
        try {
            Configuration config = new PropertiesConfiguration("crawler.properties");
            Iterator<String> keyIt = config.getKeys();
            String key;
            while (keyIt.hasNext()) {
                key = keyIt.next();
                switch (key) {
                    case "http.maxconnections":
                        ConfigSingleton.INSTANCE.setMaxConnections(config.getInt(key));
                        break;
                    case "http.maxconnectionsperhost":
                        ConfigSingleton.INSTANCE.setMaxConnectionsPerHost(config.getInt(key));
                        break;
                    case "http.sockettimeout":
                        ConfigSingleton.INSTANCE.setSocketTimeout(config.getInt(key));
                        break;
                    case "http.connectiontimeout":
                        ConfigSingleton.INSTANCE.setConnectionTimeout(config.getInt(key));
                        break;
                    case "frontier.eviction.policy":
                        ConfigSingleton.INSTANCE.setEviction(EvictionPolicy.valueOf(config.getString(key)));
                        break;
                    case "frontier.eviction.maxelements":
                        ConfigSingleton.INSTANCE.setMaxInMemoryElements(config.getInt(key));
                        break;
                    case "frontier.storage.location=storage":
                        ConfigSingleton.INSTANCE.setFileStorage(config.getString(key));
                        break;
                    case "crawler.threads":
                        ConfigSingleton.INSTANCE.setNumberOfCrawlerThreads(config.getInt(key));
                        break;
                    case "crawler.considerrobottxt":
                        ConfigSingleton.INSTANCE.setConsiderRobotTxt(config.getBoolean(key));
                        break;
                    case "crawler.politnessdealy":
                        ConfigSingleton.INSTANCE.setPolitnessDelay(config.getInt(key));
                        break;
                    case "crawler.maxcrawldepth":
                        ConfigSingleton.INSTANCE.setMaxCrawlDepth(config.getInt(key));
                        break;
                    case "crawler.useragent":
                        ConfigSingleton.INSTANCE.setUserAgent(config.getString(key));
                        break;
                    case "crawler.maxhosts":
                        ConfigSingleton.INSTANCE.setMaxHostsToCrawl(config.getInt(key));
                        break;
                    case "crawler.followredirect":
                        ConfigSingleton.INSTANCE.setFollowRedirect(config.getBoolean(key));
                        break;
                    case "crawler.pageextraction":
                        ConfigSingleton.INSTANCE.setExtractionType(PageExtractionType.valueOf(config.getString(key)));
                        break;
                    case "crawler.crawlbehavior":
                       ConfigSingleton.INSTANCE.setCustomCrawlBehavior((Class<? extends CrawlBehavior>) Class.forName(config.getString(key)));
                        break;
                    default:

                        break;
                }
            }
            this.fileConfigLoaded = true;
            LOG.info("...File: crawler.properties loaded successfully!");
        } catch (ConfigurationException e) {
            LOG.error("An error occurred while loading the crawler.properties file.", e);
        } catch (ClassNotFoundException e) {
            LOG.error("The Custom defined CrawlBehavior cannot be found in the classpath.", e);
        }

        return this;
    }

    public ConfigLoader loadClusterConfig()
    {
        //todo handle the exceptions from the cluster initialization
        LOG.info("Loading the cluster configuration.");
        ConfigSingleton.INSTANCE.setClusterConfig(new ClusterConfig());
        LOG.info("...Cluster configuration successfully loaded.");

        return this;
    }

    public ConfigLoader setCustomCrawlBehavior(Class<? extends CrawlBehavior> crawlBehavior)
    {
        ConfigSingleton.INSTANCE.setCustomCrawlBehavior(crawlBehavior);
        return this;
    }



}
