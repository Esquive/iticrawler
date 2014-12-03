package com.itiniu.iticrawler.crawler.rotottxt.inte;

import com.itiniu.iticrawler.httptools.impl.URLInfo;

public interface IRobotTxtStore {

	public void insertRule(URLInfo url, IRobotTxtDirective directive);

	public boolean containsRule(URLInfo url);

	public boolean allows(URLInfo url);
	
	public int getDelay(URLInfo url);

	public IRobotTxtDirective getDirective(URLInfo url);
	
}
