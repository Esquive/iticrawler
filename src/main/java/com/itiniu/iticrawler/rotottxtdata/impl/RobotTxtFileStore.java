package com.itiniu.iticrawler.rotottxtdata.impl;

import java.util.Map;

import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

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
	public void insertRule(URLWrapper cUrl, IRobotTxtDirective directive)
	{
		this.rules.put(cUrl.getDomain(), directive);
	}

	@Override
	public boolean containsRule(URLWrapper url)
	{
		return this.rules.containsKey(url.getDomain());
	}

	@Override
	public boolean allows(URLWrapper url)
	{
		return this.rules.get(url.getDomain()).allows(url.toString());
	}

	@Override
	public IRobotTxtDirective getDirective(URLWrapper url)
	{
		return this.rules.get(url.getDomain());
	}

	@Override
	public int getDelay(URLWrapper url)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
