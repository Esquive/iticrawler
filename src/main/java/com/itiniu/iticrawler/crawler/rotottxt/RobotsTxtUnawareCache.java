package com.itiniu.iticrawler.crawler.rotottxt;

import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

public class RobotsTxtUnawareCache implements RobotsTxtCache {
    @Override
    public void insertRule(URLInfo cUrl, BaseRobotRules rules) {
        // Do nothing
    }

    @Override
    public boolean containsRule(URLInfo url) {
        return true;
    }

    @Override
    public boolean allows(URLInfo url) {
        return true;
    }


    @Override
    public Long getDelay(URLInfo url) {
        return 0l;
    }

}
