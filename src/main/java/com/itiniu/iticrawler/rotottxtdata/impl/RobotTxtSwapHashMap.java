package com.itiniu.iticrawler.rotottxtdata.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;
import com.itiniu.iticrawler.util.eviction.lfu.LFUCache;

public class RobotTxtSwapHashMap implements IRobotTxtStore
{
	private LFUCache<String, IRobotTxtDirective> rules = null;
	private IRobotTxtStore diskSwap = null;
	private ReentrantReadWriteLock lock = null;
	private ExecutorService writeBehindService = null;

	//TODO: add parameter for the Eviction strategy
	public RobotTxtSwapHashMap(int maxSize)
	{
		this.rules = new LFUCache<>(maxSize);
		this.diskSwap = new RobotTxtFileStore();
		this.lock = new ReentrantReadWriteLock(true);
		this.writeBehindService = Executors.newFixedThreadPool(ConfigSingleton.INSTANCE.getNumberOfCrawlerThreads());
	}
	
	@Override
	public void insertRule(final URLWrapper url, final IRobotTxtDirective directive)
	{
		this.lock.writeLock().lock();
		try
		{
			this.rules.put(url.getDomain(), directive);
			
			this.writeBehindService.execute(new Runnable()
			{
				@Override
				public void run()
				{
					diskSwap.insertRule(url, directive);
				}
			});
		}
		finally
		{
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public boolean containsRule(URLWrapper url)
	{
		this.lock.readLock().lock();
		try
		{
			boolean contains = false;
			contains = this.rules.containsKey(url.getDomain());
			if(!contains)
			{
				contains = this.diskSwap.containsRule(url);
				if(contains)
				{
					this.reloadToMemory(url, this.diskSwap.getDirective(url));
				}
			}
			
			return contains;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		
	}

	@Override
	public boolean allows(URLWrapper url)
	{
		this.lock.readLock().lock();
		try
		{
			boolean allows = true;
			IRobotTxtDirective directive = this.rules.get(url.getDomain());
			if(directive == null)
			{
				directive = this.diskSwap.getDirective(url); 
				this.reloadToMemory(url, directive);
			}
			
			if(directive != null)
			{
				allows = directive.allows(url.toString());
			}
			return allows;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		
	}

	
	@Override
	public int getDelay(URLWrapper url)
	{
		this.lock.readLock().lock();
		try
		{
			int delay = 0;
			IRobotTxtDirective directive = this.rules.get(url.getDomain());
			if(directive == null)
			{
				directive = this.diskSwap.getDirective(url); 
				this.reloadToMemory(url, directive);
			}
			
			if(directive != null)
			{
				delay = directive.getDelay();
			}
			return delay;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}


	@Override
	public IRobotTxtDirective getDirective(URLWrapper url)
	{
		this.lock.readLock().lock();
		try
		{
			IRobotTxtDirective directive = this.rules.get(url.getDomain());
			if(directive == null)
			{
				directive = this.diskSwap.getDirective(url); 
				this.reloadToMemory(url, directive);
			}
			
			return directive;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	private void reloadToMemory(URLWrapper url, IRobotTxtDirective directive)
	{
		this.lock.readLock().unlock();
		this.lock.writeLock().lock();
		try
		{
			this.rules.put(url.getDomain(), directive);
		}
		finally
		{
			this.lock.writeLock().unlock();
			this.lock.readLock().lock();
		}
	}
	
	
}
