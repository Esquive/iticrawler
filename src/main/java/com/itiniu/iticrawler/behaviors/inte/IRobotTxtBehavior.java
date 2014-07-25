package com.itiniu.iticrawler.behaviors.inte;

import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

/**
 * Interface defining the way the robots.txt are fetched.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public interface IRobotTxtBehavior
{
	public void fetchRobotTxt(URLWrapper url, HttpClient httpClient, IRobotTxtStore robotTxtData);
	
}
