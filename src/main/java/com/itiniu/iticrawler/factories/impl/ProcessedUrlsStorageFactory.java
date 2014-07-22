package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IProcessedURLStorageFactory;
import com.itiniu.iticrawler.frontier.impl.DistributedProcessedUrlsMap;
import com.itiniu.iticrawler.frontier.impl.ProcessedUrlsFileStore;
import com.itiniu.iticrawler.frontier.impl.ProcessedUrlsHashMap;
import com.itiniu.iticrawler.frontier.inte.IProcessedURLStore;

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
				
			default:
				
				toReturn = new ProcessedUrlsHashMap();
				
				break;
		}
		
		return toReturn;
	}

}
