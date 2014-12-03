package com.itiniu.iticrawler.crawler.behaviors.impl;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.crawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

/**
 * Class implementing the {@link IRobotTxtBehavior} interface.
 * In case robots.txt is ignored because of the configuration nothing happens in the method call.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class RobotTxtUnawareBehavior implements IRobotTxtBehavior
{
	@Override
	public void fetchRobotTxt(URLInfo url, HttpClient httpClient,
			IRobotTxtStore robotTxtData)
	{
		//Do nothing
	}

}