package com.itiniu.iticrawler.rotottxtdata.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

public class RobotTxtSwapHashMap extends RobotTxtAwareHashMap implements IRobotTxtStore
{
	private IRobotTxtStore diskSwap = null;
	private ExecutorService writeBehindService = null;

	public RobotTxtSwapHashMap()
	{
		this.diskSwap = new RobotTxtFileStore();
		this.writeBehindService = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads());
	}
	
	@Override
	public void insertRule(final URLWrapper url, final IRobotTxtDirective directive)
	{
		
		this.writeBehindService.execute(new Runnable()
		{
			@Override
			public void run()
			{
				diskSwap.insertRule(url, directive);
			}
		});
		
		super.insertRule(url, directive);
		
	}

	@Override
	public boolean containsRule(URLWrapper url)
	{
		boolean contains = super.containsRule(url);
		return contains ? contains : this.diskSwap.containsRule(url);
	}

	@Override
	public boolean allows(URLWrapper url)
	{
		IRobotTxtDirective directive = (this.getDirective(url) == null ) ? this.getDirective(url) : this.diskSwap.getDirective(url); 
		if(directive != null)
		{
			return directive.allows(url.toString());
		}
		return true;
	}

	@Override
	public IRobotTxtDirective getDirective(URLWrapper url)
	{ 
		IRobotTxtDirective directive;
		return ((directive = this.getDirective(url)) == null ) ? directive : this.diskSwap.getDirective(url); 
	}
	
}
