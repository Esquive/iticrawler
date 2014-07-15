package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

//TODO: Thread Safety!!!!
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
		    this.rules.put(this.formatURL(url), directive);
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
            return this.rules.containsKey(this.formatURL(url));
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
		    return this.rules.get(this.formatURL(url)).allows(url.toString());
        }
        finally {
            this.rwLock.readLock().unlock();
        }

	}

    private String formatURL(URLWrapper url)
    {
        return url.getProtocol() + "://" + url.getDomain();
    }

}
