package com.itiniu.iticrawler.livedatastorage.impl;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

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
	
}
