package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IProcessedURLStorageFactory;
import com.itiniu.iticrawler.frontier.impl.DistributedProcessedUrlsMap;
import com.itiniu.iticrawler.frontier.impl.ProcessedUrlsFileStore;
import com.itiniu.iticrawler.frontier.impl.ProcessedUrlsHashMap;
import com.itiniu.iticrawler.frontier.impl.ProcessedUrlsSwapHashMap;
import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;

/**
 * Default implementation of the {@link IProcessedURLStorageFactory} interface.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 */
public class ProcessedUrlsStorageFactory implements IProcessedURLStorageFactory
{

	@Override
	public IProcessedURLStore getProcessedUrlStorage()
	{
		IProcessedURLStore toReturn = null;
		
		switch(ConfigSingleton.INSTANCE.getProcessedUrlsStoragePolicy())
		{
			case MEMORY:
				
				toReturn = new ProcessedUrlsHashMap();
				
				break;
				
			case MEMORYCLUSTER:
				
				toReturn = new DistributedProcessedUrlsMap(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());
				
				break;
				
			case FILE: 
				
				toReturn = new ProcessedUrlsFileStore();
				
				break;
				
			case MEMORY_FILE_SWAP:
				toReturn = new ProcessedUrlsSwapHashMap(ConfigSingleton.INSTANCE.getMaxInMemoryElements());
				break;
				
			default:
				
				toReturn = new ProcessedUrlsHashMap();
				
				break;
		}
		
		return toReturn;
	}

}
