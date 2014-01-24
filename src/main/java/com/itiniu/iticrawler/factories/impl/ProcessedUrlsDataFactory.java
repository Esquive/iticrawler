package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IProcessedURLStorageFactory;
import com.itiniu.iticrawler.livedatastorage.impl.ProcessedUrlsHashMap;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;

public class ProcessedUrlsDataFactory implements IProcessedURLStorageFactory
{

	@Override
	public IProcessedURLStore getProcessedUrlStorage()
	{
		IProcessedURLStore toReturn = null;
		
		switch(ConfigSingleton.INSTANCE.getProcessedUrlsStoragePolicy())
		{
			case inMemory:
				
				toReturn = new ProcessedUrlsHashMap();
				
				break;
				
			default:
				
				toReturn = new ProcessedUrlsHashMap();
				
				break;
		}
		
		return toReturn;
	}

}
