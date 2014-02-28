package com.itiniu.iticrawler.behaviors.inte;

import com.itiniu.iticrawler.crawler.inte.AbstractPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface ICrawlBehavior
{
	public boolean shouldScheduleURL(AbstractPage page, URLWrapper url);

	public void processPage(AbstractPage page);
		
	public void handleStatuScode(AbstractPage page);
}
