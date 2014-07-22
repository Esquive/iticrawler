package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IScheduledURLStorageFactory;
import com.itiniu.iticrawler.frontier.impl.DistributedScheduledUrlsQueue;
import com.itiniu.iticrawler.frontier.impl.ScheduledUrlsFileStore;
import com.itiniu.iticrawler.frontier.impl.ScheduledUrlsQueue;
import com.itiniu.iticrawler.frontier.inte.IScheduledURLStore;

public class ScheduledUrlsStorageFactory implements IScheduledURLStorageFactory
{

	@Override
	public IScheduledURLStore getScheduledUrlData()
	{
		IScheduledURLStore toReturn = null;
		
		switch(ConfigSingleton.INSTANCE.getScheduledUrlsStoragePolicy())
		{
			case MEMORY:
				
				toReturn = new ScheduledUrlsQueue();
				
				break;
				
			case MEMORYCLUSTER:
				
				toReturn = new DistributedScheduledUrlsQueue(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());
				
				break;
				
			case FILE:
				
				toReturn = new ScheduledUrlsFileStore();
				
				break;
				
			default:
				
				toReturn = new ScheduledUrlsQueue();
				
				break;
				
		}
		
		return toReturn;
		
	}

}
