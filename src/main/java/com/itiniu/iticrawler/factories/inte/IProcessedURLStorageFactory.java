package com.itiniu.iticrawler.factories.inte;

import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;

/**
 * Factory class, creating the data storage for the Processed URLs
 * according to the configuration.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 */
public interface IProcessedURLStorageFactory
{
	public IProcessedURLStore getProcessedUrlStorage();
}
