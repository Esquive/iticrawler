package com.itiniu.iticrawler.behaviors.inte;


import com.itiniu.iticrawler.crawler.impl.Page;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface IProcessPageBehavior
{
	public void processPage(Page page);
	
	public void handleStatuScode(int pageStatus, URLWrapper url);
}
