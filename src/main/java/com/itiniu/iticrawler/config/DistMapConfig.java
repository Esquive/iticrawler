package com.itiniu.iticrawler.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;

public class DistMapConfig
{

	public DistMapConfig setup(Config cfg, String name)
	{
		MapConfig mapConfig = new MapConfig();

		mapConfig.setName(name);
		mapConfig.setBackupCount(1);

		cfg.addMapConfig(mapConfig);

		return this;
	}

}
