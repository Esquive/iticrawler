package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IRobotTxtStorageFactory;
import com.itiniu.iticrawler.crawler.rotottxt.impl.DistributedRobotTxtMap;
import com.itiniu.iticrawler.crawler.rotottxt.impl.RobotTxtUnawareData;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

/**
 * Default implementation of the {@link RobotTxtStorageFactory} interface.
 *
 * @author Eric Falk <erfalk at gmail dot com>
 */
public class RobotTxtStorageFactory implements IRobotTxtStorageFactory {

    @Override
    public IRobotTxtStore getRobotTxtData() {
        IRobotTxtStore toReturn = null;

        if (!ConfigSingleton.INSTANCE.isConsiderRobotTxt()) {
            toReturn = new RobotTxtUnawareData();
        } else {


            toReturn = new DistributedRobotTxtMap(ConfigSingleton.INSTANCE.getClusterConfig());


        }

        return toReturn;
    }

}
