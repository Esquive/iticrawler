package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IRobotTxtStorageFactory;
import com.itiniu.iticrawler.livedatastorage.impl.DistributedRobotTxtMap;
import com.itiniu.iticrawler.livedatastorage.impl.RobotTxtAwareHashMap;
import com.itiniu.iticrawler.livedatastorage.impl.RobotTxtUnawareData;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

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
				case inMemory:
				
				toReturn = new RobotTxtAwareHashMap();
				
				break;
				
				case cluster:
					toReturn = new DistributedRobotTxtMap(ConfigSingleton.INSTANCE.getClusterConfig().getConfig());
					break;
			default:
				toReturn = new RobotTxtAwareHashMap();
				break;
			}
		}
		
		return toReturn;
	}

}
