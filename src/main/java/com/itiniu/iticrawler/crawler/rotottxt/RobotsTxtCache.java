package com.itiniu.iticrawler.crawler.rotottxt;

import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public interface RobotsTxtCache {

	void insertRule(URLInfo url, BaseRobotRules rules);

	boolean containsRule(URLInfo url);

	boolean allows(URLInfo url);

	Long getDelay(URLInfo url);

}
