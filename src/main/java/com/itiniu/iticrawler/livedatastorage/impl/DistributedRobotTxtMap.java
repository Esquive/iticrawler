package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

public class DistributedRobotTxtMap implements IRobotTxtStore {

	private Map<String, IRobotTxtDirective> rules;
	
	public DistributedRobotTxtMap(Config cfg)
	{
		new DistMapConfig().setup(cfg, "RobotTxt");
		
		this.rules = Hazelcast.getHazelcastInstanceByName("itiCrawlerCluster").getMap("RobotTxt");
	}
	
	@Override
	public void insertRule(URLWrapper cUrl, IRobotTxtDirective directive) {
	
		this.rules.put(this.getHostURL(cUrl), directive);
	}

	@Override
	public boolean containsRule(URLWrapper url) {
		return this.rules.containsKey(this.getHostURL(url));
	}

	@Override
	public boolean allows(URLWrapper url) {
		return this.rules.get(this.getHostURL(url)).allows(url.toString());
	}
	
	
	private String getHostURL(URLWrapper url)
	{
		return url.getProtocol() + "://" + url.getDomain();
	}

}
