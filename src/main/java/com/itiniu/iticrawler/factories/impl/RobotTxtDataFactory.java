package com.itiniu.iticrawler.factories.impl;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.factories.inte.IRobotTxtStorageFactory;
import com.itiniu.iticrawler.livedatastorage.impl.RobotTxtAwareHashMap;
import com.itiniu.iticrawler.livedatastorage.impl.RobotTxtUnawareData;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

public class RobotTxtDataFactory implements IRobotTxtStorageFactory
{

	@Override
	public IRobotTxtStore getRobotTxtData()
	{
		IRobotTxtStore toReturn = null;
		
		if(ConfigSingleton.INSTANCE.isConsiderRobotTxt())
		{
			toReturn = new RobotTxtAwareHashMap();
		}
		else
		{
			toReturn = new RobotTxtUnawareData();
		}
		
		return toReturn;
	}

}
