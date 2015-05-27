package com.itiniu.iticrawler.crawler.rotottxt.impl;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.ClusterConfig;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

public class DistributedRobotTxtMap implements IRobotTxtStore {

	private Map<String, IRobotTxtDirective> rules;
	
	public DistributedRobotTxtMap(ClusterConfig cfg)
	{
		new DistMapConfig().setup(cfg.getMemoryClusterConfig(), "RobotTxt", null);
		
		this.rules = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getMap("RobotTxt");
	}
	
	@Override
	public void insertRule(URLInfo url, IRobotTxtDirective directive) {
	
		this.rules.put(url.getDomain(), directive);
	}

	@Override
	public boolean containsRule(URLInfo url) {
		return this.rules.containsKey(url.getDomain());
	}

	@Override
	public boolean allows(URLInfo url) {
		return this.rules.get(url.getDomain()).allows(url.toString());
	}

	@Override
	public IRobotTxtDirective getDirective(URLInfo url)
	{
		return this.rules.get(url.getDomain());
	}

	@Override
	public int getDelay(URLInfo url)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
