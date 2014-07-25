package com.itiniu.iticrawler.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;

/**
 * This class is used to configure the Hazelcast clustered Collections.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
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
	
	/**
	 * Returns the Config Object to create the actual Collections.
	 * 
	 * @return
	 */
	public Config getConfig()
	{
		if(this.cfg == null)
		{
			this.setup();
		}
		
		return this.cfg;
	}

}
