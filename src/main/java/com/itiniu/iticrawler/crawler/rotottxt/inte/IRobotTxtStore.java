package com.itiniu.iticrawler.crawler.rotottxt.inte;

import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public interface IRobotTxtStore {

	void insertRule(URLInfo url, BaseRobotRules rules);

	boolean containsRule(URLInfo url);

	boolean allows(URLInfo url);

	Long getDelay(URLInfo url);

	BaseRobotRules getDirective(URLInfo url);
	
}
