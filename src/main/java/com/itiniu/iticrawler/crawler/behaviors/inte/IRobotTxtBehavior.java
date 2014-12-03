package com.itiniu.iticrawler.crawler.behaviors.inte;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

/**
 * Interface defining the way the robots.txt are fetched.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface IRobotTxtBehavior
{
	public void fetchRobotTxt(URLInfo url, HttpClient httpClient, IRobotTxtStore robotTxtData);
	
}
