package com.itiniu.iticrawler.rotottxtdata.inte;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface IRobotTxtStore {

	public void insertRule(URLWrapper url, IRobotTxtDirective directive);

	public boolean containsRule(URLWrapper url);

	public boolean allows(URLWrapper url);
	
	public int getDelay(URLWrapper url);

	public IRobotTxtDirective getDirective(URLWrapper url);
	
}
