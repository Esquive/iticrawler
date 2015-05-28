package com.itiniu.iticrawler.behaviors.robotstxt;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtCache;

/**
 * Class implementing the {@link RobotsTxtBehavior} interface.
 * In case robots.txt is ignored because of the configuration nothing happens in the method call.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class RobotTxtUnawareBehavior implements RobotsTxtBehavior
{
	@Override
	public void fetchRobotTxt(URLInfo url, HttpClient httpClient,
			RobotsTxtCache robotTxtData)
	{
		//Do nothing
	}

}