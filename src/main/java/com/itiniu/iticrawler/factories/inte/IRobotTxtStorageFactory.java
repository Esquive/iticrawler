package com.itiniu.iticrawler.factories.inte;

import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

/**
 * Factory class, creating the data storage for the robots.txt directives.
 * according to the configuration.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 */
public interface IRobotTxtStorageFactory
{
	public IRobotTxtStore getRobotTxtData();
}
