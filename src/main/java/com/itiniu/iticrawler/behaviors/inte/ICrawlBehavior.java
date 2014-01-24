package com.itiniu.iticrawler.behaviors.inte;

import com.itiniu.iticrawler.crawler.inte.AbstractPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface ICrawlBehavior
{
	public boolean shouldScheduleURL(String inUrl);

	public void processPage(AbstractPage page);
		
	public void handleStatuScode(int pageStatus, URLWrapper url);	
}
