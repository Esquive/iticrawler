package com.itiniu.iticrawler.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.SimpleRobotRules;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.util.StorageCluster;
import com.itiniu.iticrawler.util.serialization.SimpleRobotRulesSerializer;
import com.itiniu.iticrawler.util.serialization.URLInfoSerializer;

/**
 * This class is used to configure the Hazelcast clustered Collections.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class ClusterConfig {

	public static final String MEMORY_CLUSTER_NAME = "itiCrawlerMemoryCluster";
	public static final String STORAGE_CLUSTER_NAME = "TestCluster";
	private Config memoryClusterConfig;
	private StorageCluster storageClusterConfig;

	public ClusterConfig()
	{
		this.setupHazelcast();
		this.setupCassandra();
	}

	private void setupHazelcast()
	{
		this.memoryClusterConfig = new Config();
		
		this.memoryClusterConfig.setInstanceName(MEMORY_CLUSTER_NAME);
		
		NetworkConfig nConfig = memoryClusterConfig.getNetworkConfig();
		MulticastConfig mcConfig = new MulticastConfig();

		//TODO: Refactor the config Values
		//Set Port config
		nConfig.setPort(5701);
		nConfig.setPortAutoIncrement(true);
		
		//MulticastConfig
		mcConfig.setMulticastGroup("224.2.2.3");
		mcConfig.setMulticastPort(54327);
		
		nConfig.getJoin().setMulticastConfig(mcConfig);

		//SerializationConfig
		this.memoryClusterConfig.getSerializationConfig().getSerializerConfigs()
				.add(new SerializerConfig().setTypeClass(BaseRobotRules.class).setImplementation(new SimpleRobotRulesSerializer()));
		this.memoryClusterConfig.getSerializationConfig().getSerializerConfigs()
				.add(new SerializerConfig().setTypeClass(URLInfo.class).setImplementation(new URLInfoSerializer()));


		//And finally initialize the cluster
		Hazelcast.newHazelcastInstance(this.memoryClusterConfig);
	}

	private void setupCassandra()
	{
		this.storageClusterConfig = new StorageCluster(STORAGE_CLUSTER_NAME);
	}
	
	/**
	 * Returns the Config Object to create the actual Collections.
	 * 
	 * @return
	 */
	public Config getMemoryClusterConfig()
	{
		return this.memoryClusterConfig;
	}

	public StorageCluster getStorageClusterConfig()
	{
		return this.storageClusterConfig;
	}


}
