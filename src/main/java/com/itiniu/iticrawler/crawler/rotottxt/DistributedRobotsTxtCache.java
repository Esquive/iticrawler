package com.itiniu.iticrawler.crawler.rotottxt;

import java.util.Map;

import com.hazelcast.core.Hazelcast;
import com.itiniu.iticrawler.config.ClusterConfig;
import com.itiniu.iticrawler.config.DistMapConfig;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtCache;

public class DistributedRobotsTxtCache implements RobotsTxtCache {

	public static final String ROBOTSTXT_CACHE = "ROBOTSTXT_CACHE";


	private Map<String, BaseRobotRules> rules;
	
	public DistributedRobotsTxtCache(ClusterConfig cfg)
	{
		new DistMapConfig().setup(cfg.getMemoryClusterConfig(), ROBOTSTXT_CACHE, null);
		
		this.rules = Hazelcast.getHazelcastInstanceByName(ClusterConfig.MEMORY_CLUSTER_NAME).getMap(ROBOTSTXT_CACHE);
	}
	
	@Override
	public void insertRule(URLInfo url, BaseRobotRules directive) {
		this.rules.put(url.getDomain(), directive);
	}

	@Override
	public boolean containsRule(URLInfo url) {
		return this.rules.containsKey(url.getDomain());
	}

	@Override
	public boolean allows(URLInfo url) {
		//todo return something in case the robot txt is not contained
		return this.rules.get(url.getDomain()).isAllowed(url.toString());
	}

	@Override
	public Long getDelay(URLInfo url)
	{
		return this.rules.get(url.getDomain()).get_crawlDelay();
	}

}
