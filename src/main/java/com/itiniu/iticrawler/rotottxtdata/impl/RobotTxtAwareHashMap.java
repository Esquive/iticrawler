package com.itiniu.iticrawler.rotottxtdata.impl;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

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
	public void insertRule(URLWrapper url, IRobotTxtDirective directive)
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
	public boolean containsRule(URLWrapper url)
	{
        this.rwLock.readLock().lock();
        try
        {
            return this.rules.containsKey(url);
        }
        finally {
            this.rwLock.readLock().unlock();
        }

	}

	@Override
	public boolean allows(URLWrapper url)
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
	public IRobotTxtDirective getDirective(URLWrapper url)
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
	public int getDelay(URLWrapper url)
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
