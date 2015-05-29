package com.itiniu.iticrawler.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;

public class DistMapConfig {

    public DistMapConfig setup(Config cfg, String name, Object storeImplementation) {
        MapConfig mapConfig = new MapConfig();

        //TODO: Refactor the config options
        mapConfig.setName(name);
        mapConfig.setBackupCount(1);

        if (storeImplementation != null) {

            MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
            //todo Refactor this to config
            maxSizeConfig.setSize(1000);

            MapStoreConfig store = new MapStoreConfig();
            store.setImplementation(storeImplementation);

            mapConfig.setMaxSizeConfig(maxSizeConfig);
            mapConfig.setMapStoreConfig(store);
        }

        cfg.addMapConfig(mapConfig);

        return this;
    }

}
