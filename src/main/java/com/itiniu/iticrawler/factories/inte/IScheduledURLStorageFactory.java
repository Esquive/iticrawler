package com.itiniu.iticrawler.factories.inte;

import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;

/**
 * Factory class, creating the data storage for the scheduled URLs
 * according to the configuration.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 */
public interface IScheduledURLStorageFactory
{
	public IScheduledURLStore getScheduledUrlData();
}
