package com.itiniu.iticrawler.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;

public class DistQueueConfig
{

	public DistQueueConfig setup(Config cfg, String name)
	{

		QueueConfig qConfig = new QueueConfig();

		//TODO: Refactor the configuration entries
		qConfig.setName(name);
		qConfig.setBackupCount(1);

		// TODO: Add a store to persist on disk in case memory reaches its
		// limits.

		cfg.addQueueConfig(qConfig);

		return this;
	}

}
