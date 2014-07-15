package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IProcessedURLStorageFactory;
import com.itiniu.iticrawler.livedatastorage.impl.DistributedProcessedUrlsMap;
import com.itiniu.iticrawler.livedatastorage.impl.ProcessedUrlsHashMap;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;

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
				
			default:
				
				toReturn = new ProcessedUrlsHashMap();
				
				break;
		}
		
		return toReturn;
	}

}
