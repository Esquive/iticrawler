package com.itiniu.iticrawler.crawler.impl;

import java.io.Serializable;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;

public class RobotTxtNotFoundDirective implements IRobotTxtDirective, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4881505832193948021L;

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

	@Override
	public void addDelay(int delay)
	{
		// Do nothing
	}

	@Override
	public int getDelay()
	{
		return -1;
	}

}
