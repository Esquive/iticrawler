package com.itiniu.iticrawler.behaviors.impl;

import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

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
	public void fetchRobotTxt(URLWrapper url, HttpClient httpClient,
			IRobotTxtStore robotTxtData)
	{
		//Do nothing
	}

}