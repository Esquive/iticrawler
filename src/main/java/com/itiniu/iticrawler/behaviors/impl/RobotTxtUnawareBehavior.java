package com.itiniu.iticrawler.behaviors.impl;

import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

public class RobotTxtUnawareBehavior implements IRobotTxtBehavior
{
	@Override
	public void fetchRobotTxt(URLWrapper url, HttpClient httpClient,
			IRobotTxtStore robotTxtData)
	{
		//Do nothing
	}

}
