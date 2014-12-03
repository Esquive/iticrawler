package com.itiniu.iticrawler.crawler.rotottxt.impl;

import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

public class RobotTxtUnawareData implements IRobotTxtStore
{
	@Override
	public void insertRule(URLInfo cUrl, IRobotTxtDirective directive)
	{
		// Do nothing		
	}

	@Override
	public boolean containsRule(URLInfo url)
	{
		return true;
	}

	@Override
	public boolean allows(URLInfo url)
	{
		return true;
	}

	@Override
	public IRobotTxtDirective getDirective(URLInfo url)
	{
		return null;
	}

	@Override
	public int getDelay(URLInfo url)
	{
		return 0;
	}
	
}
