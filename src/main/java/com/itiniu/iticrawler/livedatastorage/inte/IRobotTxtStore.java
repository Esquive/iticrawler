package com.itiniu.iticrawler.livedatastorage.inte;

import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface IRobotTxtStore {

	public void insertRule(URLWrapper cUrl, IRobotTxtDirective directive);

	public boolean containsRule(URLWrapper url);

	public boolean allows(URLWrapper url);
	
}
