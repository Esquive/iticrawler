package com.itiniu.iticrawler.crawler.rotottxt.impl;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

public class RobotTxtAwareHashMap implements IRobotTxtStore
{
	private HashMap<String, IRobotTxtDirective> rules = null;
    private ReentrantReadWriteLock rwLock = null;
	
	public RobotTxtAwareHashMap()
	{
		this.rules = new HashMap<>();
        this.rwLock = new ReentrantReadWriteLock();
	}
	
	@Override
	public void insertRule(URLInfo url, IRobotTxtDirective directive)
	{
        this.rwLock.writeLock().lock();
        try{
		    this.rules.put(url.getDomain(), directive);
        }
        finally
        {
            this.rwLock.writeLock().unlock();
        }
    }

	@Override
	public boolean containsRule(URLInfo url)
	{
        this.rwLock.readLock().lock();
        try
        {
            return this.rules.containsKey(url.getDomain());
        }
        finally {
            this.rwLock.readLock().unlock();
        }

	}

	@Override
	public boolean allows(URLInfo url)
	{
        this.rwLock.readLock().lock();
        try {
		    return this.rules.get(url.getDomain()).allows(url.toString());
        }
        finally {
            this.rwLock.readLock().unlock();
        }

	}

	@Override
	public IRobotTxtDirective getDirective(URLInfo url)
	{
		this.rwLock.readLock().lock();
		try
		{
			return this.rules.get(url.getDomain());
		}
		finally
		{
			this.rwLock.readLock().unlock();
		}
	}

	@Override
	public int getDelay(URLInfo url)
	{
		this.rwLock.readLock().lock();
		try
		{
			return this.rules.get(url.getDomain()).getDelay();
		}
		finally
		{
			this.rwLock.readLock().unlock();
		}
	}


}
