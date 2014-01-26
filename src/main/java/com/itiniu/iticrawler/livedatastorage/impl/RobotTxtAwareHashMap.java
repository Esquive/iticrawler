package com.itiniu.iticrawler.livedatastorage.impl;

import java.util.HashMap;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;

//TODO: Thread Safety!!!!
public class RobotTxtAwareHashMap implements IRobotTxtStore
{
	private HashMap<String, IRobotTxtDirective> rules = null;
	
	public RobotTxtAwareHashMap()
	{
		this.rules = new HashMap<>();
	}
	
	
	@Override
	public void insertRule(URLWrapper url, IRobotTxtDirective directive)
	{
		
		String hostUrl = url.getProtocol() + "://" + url.getSubDomain() + "." + url.getDomain();
		this.rules.put(hostUrl, directive);
	}

	@Override
	public boolean containsRule(URLWrapper url)
	{
		String hostUrl = url.getProtocol() + "://" + url.getSubDomain() + "." + url.getDomain();
		return this.rules.containsKey(hostUrl);
	}

	@Override
	public boolean allows(URLWrapper url)
	{
		String hostUrl = url.getProtocol() + "://" + url.getSubDomain() + "." + url.getDomain();
		return this.rules.get(hostUrl).allows(url.toString());
	}

}
