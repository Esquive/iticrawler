package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IRobotTxtStorageFactory;
import com.itiniu.iticrawler.rotottxtdata.impl.DistributedRobotTxtMap;
import com.itiniu.iticrawler.rotottxtdata.impl.RobotTxtAwareHashMap;
import com.itiniu.iticrawler.rotottxtdata.impl.RobotTxtFileStore;
import com.itiniu.iticrawler.rotottxtdata.impl.RobotTxtUnawareData;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

public class RobotTxtStorageFactory implements IRobotTxtStorageFactory
{

	@Override
	public IRobotTxtStore getRobotTxtData()
	{
		IRobotTxtStore toReturn = null;
		
		if(!ConfigSingleton.INSTANCE.isConsiderRobotTxt())
		{
			toReturn = new RobotTxtUnawareData();
		}
		else
		{
			switch(ConfigSingleton.INSTANCE.getRobotTxtDataStoragePolicy())
			{
				case MEMORY:
				
				toReturn = new RobotTxtAwareHashMap();
				
				break;
				
				case MEMORYCLUSTER:
					toReturn = new DistributedRobotTxtMap(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());
					break;
					
				case FILE:
					toReturn = new RobotTxtFileStore();
					break;
			default:
				toReturn = new RobotTxtAwareHashMap();
				break;
			}
		}
		
		return toReturn;
	}

}
