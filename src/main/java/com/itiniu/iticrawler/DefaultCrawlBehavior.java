package com.itiniu.iticrawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.crawler.inte.AbstractCrawler;
import com.itiniu.iticrawler.crawler.inte.AbstractPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class DefaultCrawlBehavior implements ICrawlBehavior
{
	//Getting the logger
	protected static final Logger logger = LogManager.getLogger(DefaultCrawlBehavior.class.getName());
	

	@Override
	public boolean shouldScheduleURL(String inUrl)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void processPage(AbstractPage page)
	{
		logger.info("Crawling: " + page.getUrl().toString());
	}

	@Override
	public void handleStatuScode(int pageStatus, URLWrapper url)
	{
		// TODO Auto-generated method stub

	}

}
