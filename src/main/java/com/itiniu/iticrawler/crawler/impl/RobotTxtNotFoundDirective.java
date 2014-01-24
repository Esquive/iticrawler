package com.itiniu.iticrawler.crawler.impl;

import com.itiniu.iticrawler.crawler.inte.RobotTxtDirectiveInterf;

public class RobotTxtNotFoundDirective implements RobotTxtDirectiveInterf
{

	@Override
	public void addAllowEntry(String entry)
	{
		//Do nothing

	}

	@Override
	public void addDisallowEntry(String entry)
	{
		//Do nothing

	}

	@Override
	public boolean allows(String path)
	{
		//Always return true
		return true;
	}

}
