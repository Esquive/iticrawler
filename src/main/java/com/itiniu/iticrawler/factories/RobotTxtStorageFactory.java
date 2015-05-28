package com.itiniu.iticrawler.factories;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.rotottxt.DistributedRobotsTxtCache;
import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtUnawareCache;
import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtCache;

/**
 * Default implementation of the {@link RobotTxtStorageFactory} interface.
 *
 * @author Eric Falk <erfalk at gmail dot com>
 */
public class RobotTxtStorageFactory {

    private RobotTxtStorageFactory()
    {}

    public static RobotsTxtCache getRobotTxtData() {
        RobotsTxtCache toReturn = null;

        if (!ConfigSingleton.INSTANCE.isConsiderRobotTxt()) {
            toReturn = new RobotsTxtUnawareCache();
        } else {
            toReturn = new DistributedRobotsTxtCache(ConfigSingleton.INSTANCE.getClusterConfig());
        }
        return toReturn;
    }

}
