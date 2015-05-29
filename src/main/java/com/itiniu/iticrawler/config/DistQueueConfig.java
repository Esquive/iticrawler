package com.itiniu.iticrawler.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.QueueStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class DistQueueConfig
{

	public DistQueueConfig setup(Config cfg, String name, QueueStore<URLInfo> storeImplementation)
	{

		QueueConfig qConfig = new QueueConfig();

		//TODO: Refactor the configuration entries
		qConfig.setName(name);
		qConfig.setBackupCount(1);

		if(storeImplementation != null)
		{
            QueueStoreConfig storeConfig = new QueueStoreConfig();
			storeConfig.setStoreImplementation(storeImplementation);
            storeConfig.setProperty("memory-limit", "30");
            qConfig.setQueueStoreConfig(storeConfig);
			//todo Refactor to Config

		}
		cfg.addQueueConfig(qConfig);


		return this;
	}

}
