package com.itiniu.iticrawler.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;

/**
 * Class that configures the cluster. Only basic configuration is performed.
 * It is rather provided to have a possibility of adapting some things. And to have an 
 * entry point for advanced tweaking. 
 * 
 * @author esquive
 *
 */
public class ClusterConfig {
	
	Config cfg;
	
	public void setup()
	{
		this.cfg = new Config();
		
		//TODO: put this in a static read only variable
		this.cfg.setInstanceName("itiCrawlerCluster");
		
		NetworkConfig nConfig = cfg.getNetworkConfig();
		MulticastConfig mcConfig = new MulticastConfig();
		
		//Set Port config
		nConfig.setPort(5701);
		nConfig.setPortAutoIncrement(true);
		
		//MulticastConfig
		mcConfig.setMulticastGroup("224.2.2.3");
		mcConfig.setMulticastPort(54327);
		
		nConfig.getJoin().setMulticastConfig(mcConfig);
		
		
		//And finally initialize the cluster
		Hazelcast.newHazelcastInstance(this.cfg);
	}
	
	public Config getConfig()
	{
		if(this.cfg == null)
		{
			this.setup();
		}
		
		return this.cfg;
	}

}
