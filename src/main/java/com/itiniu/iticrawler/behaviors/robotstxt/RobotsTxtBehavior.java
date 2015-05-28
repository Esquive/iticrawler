package com.itiniu.iticrawler.behaviors.robotstxt;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtCache;

/**
 * Interface defining the way the robots.txt are fetched.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface RobotsTxtBehavior
{
	void fetchRobotTxt(URLInfo url, HttpClient httpClient, RobotsTxtCache robotTxtData);
}
