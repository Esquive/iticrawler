package com.itiniu.iticrawler.crawler.rotottxt.impl;

import java.io.Serializable;

import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;

/**
 * Class implementing the {@link IRobotTxtDirective} Whenever no robots.txt gets
 * found for a site this directive is used. It allows every url.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public class RobotTxtNotFoundDirective implements IRobotTxtDirective, Serializable
{

	private static final long serialVersionUID = -4881505832193948021L;

	@Override
	public void addAllowEntry(String entry)
	{
		// Do nothing

	}

	@Override
	public void addDisallowEntry(String entry)
	{
		// Do nothing

	}

	@Override
	public boolean allows(String path)
	{
		// Always return true
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
