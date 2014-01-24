package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IScheduledURLStorageFactory;
import com.itiniu.iticrawler.livedatastorage.impl.ScheduledUrlsQueue;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;

public class ScheduledUrlsDataFactory implements IScheduledURLStorageFactory
{

	@Override
	public IScheduledURLStore getScheduledUrlData()
	{
		IScheduledURLStore toReturn = null;
		
		switch(ConfigSingleton.INSTANCE.getScheduledUrlsStoragePolicy())
		{
			case inMemory:
				
				toReturn = new ScheduledUrlsQueue();
				
				break;
				
			default:
				
				toReturn = new ScheduledUrlsQueue();
				
				break;
				
		}
		
		return toReturn;
		
	}

}
