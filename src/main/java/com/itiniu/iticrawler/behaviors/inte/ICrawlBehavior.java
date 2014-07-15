package com.itiniu.iticrawler.behaviors.inte;

import com.itiniu.iticrawler.crawler.impl.Page;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface ICrawlBehavior
{
	public boolean shouldScheduleURL(Page page, URLWrapper url);

	public void processPage(Page page);
		
	public void handleStatuScode(Page page);
}
