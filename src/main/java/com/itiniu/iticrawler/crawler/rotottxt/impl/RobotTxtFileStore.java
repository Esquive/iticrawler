package com.itiniu.iticrawler.crawler.rotottxt.impl;

import java.util.Map;

import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

public class RobotTxtFileStore implements IRobotTxtStore
{
	private DB db = null;
	private Map<String, IRobotTxtDirective> rules = null;

	public RobotTxtFileStore()
	{
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();
		this.rules = this.db.getHashMap("robotTxt");
	}
	
	@Override
	public void insertRule(URLInfo cUrl, IRobotTxtDirective directive)
	{
		this.rules.put(cUrl.getDomain(), directive);
	}

	@Override
	public boolean containsRule(URLInfo url)
	{
		return this.rules.containsKey(url.getDomain());
	}

	@Override
	public boolean allows(URLInfo url)
	{
		return this.rules.get(url.getDomain()).allows(url.toString());
	}

	@Override
	public IRobotTxtDirective getDirective(URLInfo url)
	{
		return this.rules.get(url.getDomain());
	}

	@Override
	public int getDelay(URLInfo url)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
