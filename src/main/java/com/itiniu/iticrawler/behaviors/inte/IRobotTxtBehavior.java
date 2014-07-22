package com.itiniu.iticrawler.behaviors.inte;

import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

public interface IRobotTxtBehavior
{
	public void fetchRobotTxt(URLWrapper url, HttpClient httpClient, IRobotTxtStore robotTxtData);
	
}
