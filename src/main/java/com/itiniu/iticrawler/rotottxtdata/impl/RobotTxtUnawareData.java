package com.itiniu.iticrawler.rotottxtdata.impl;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

public class RobotTxtUnawareData implements IRobotTxtStore
{
	@Override
	public void insertRule(URLWrapper cUrl, IRobotTxtDirective directive)
	{
		// Do nothing		
	}

	@Override
	public boolean containsRule(URLWrapper url)
	{
		return true;
	}

	@Override
	public boolean allows(URLWrapper url)
	{
		return true;
	}

	@Override
	public IRobotTxtDirective getDirective(URLWrapper url)
	{
		return null;
	}

	@Override
	public int getDelay(URLWrapper url)
	{
		return 0;
	}
	
}
