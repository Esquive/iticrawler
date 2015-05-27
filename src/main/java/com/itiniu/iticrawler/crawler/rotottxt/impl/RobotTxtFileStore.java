package com.itiniu.iticrawler.crawler.rotottxt.impl;

import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.SimpleRobotRules;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mapdb.DB;

import com.itiniu.iticrawler.config.FileStorageConfig;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtStore;

import java.io.IOException;
import java.util.Map;

public class RobotTxtFileStore implements IRobotTxtStore
{
	private DB db = null;
	private Map<String, String> rules = null;

	public RobotTxtFileStore()
	{
		this.db = FileStorageConfig.INSTANCE.getStorageProvider();
		this.rules = this.db.getHashMap("robotTxt");
	}
	
	@Override
	public void insertRule(URLInfo cUrl, BaseRobotRules rules) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			this.rules.put(cUrl.getDomain(),mapper.writeValueAsString(rules));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		this.rules.put(cUrl.getDomain(), rules);
	}

	@Override
	public boolean containsRule(URLInfo url)
	{
		return this.rules.containsKey(url.getDomain());
	}

	@Override
	public boolean allows(URLInfo url)
	{
		ObjectMapper mapper = null;
		BaseRobotRules rules = null;
		try
		{
			mapper = new ObjectMapper();
			rules = mapper.readValue(this.rules.get(url.getDomain()), SimpleRobotRules.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//todo return something in case rules are null
		return rules!=null?rules.isAllowed(url.toString()):true;
//		return this.rules.get(url.getDomain()).isAllowed(url.toString());
	}

	@Override
	public BaseRobotRules getDirective(URLInfo url)
	{
		ObjectMapper mapper = null;
		BaseRobotRules rules = null;
		try
		{
			mapper = new ObjectMapper();
			rules = mapper.readValue(this.rules.get(url.getDomain()), SimpleRobotRules.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rules;
//		return this.rules.get(url.getDomain());
	}

	@Override
	public Long getDelay(URLInfo url)
	{
		ObjectMapper mapper = null;
		BaseRobotRules rules = null;
		try
		{
			mapper = new ObjectMapper();
			rules = mapper.readValue(this.rules.get(url.getDomain()), SimpleRobotRules.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rules!=null?rules.getCrawlDelay():null;

//		return this.rules.get(url.getDomain()).getCrawlDelay();
	}
}
