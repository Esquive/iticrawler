package com.itiniu.iticrawler.rotottxtdata.impl;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

public class DistributedRobotTxtMap implements IRobotTxtStore {

	private Map<String, IRobotTxtDirective> rules;
	
	public DistributedRobotTxtMap(Config cfg)
	{
		new DistMapConfig().setup(cfg, "RobotTxt");
		
		this.rules = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("RobotTxt");
	}
	
	@Override
	public void insertRule(URLWrapper url, IRobotTxtDirective directive) {
	
		this.rules.put(url.getDomain(), directive);
	}

	@Override
	public boolean containsRule(URLWrapper url) {
		return this.rules.containsKey(url.getDomain());
	}

	@Override
	public boolean allows(URLWrapper url) {
		return this.rules.get(url.getDomain()).allows(url.toString());
	}

	@Override
	public IRobotTxtDirective getDirective(URLWrapper url)
	{
		return this.rules.get(url.getDomain());
	}

	@Override
	public int getDelay(URLWrapper url)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
