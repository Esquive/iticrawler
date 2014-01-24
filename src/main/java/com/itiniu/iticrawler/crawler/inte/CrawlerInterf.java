package com.itiniu.iticrawler.crawler.inte;

import org.apache.http.client.HttpClient;

import com.itiniu.iticrawler.behaviors.inte.IProcessPageBehavior;
import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.behaviors.inte.IScheduleUrlBehavior;
import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;
import com.itiniu.iticrawler.livedatastorage.inte.IScheduledURLStore;


public interface CrawlerInterf extends Runnable
{
	public void setScheduleUrlBehavior(IScheduleUrlBehavior scheduleUrlBehavior);
	
	public void setProcessPageBehavior(IProcessPageBehavior processPageBehavior);
	
	public void setRobotTxtBehavior(IRobotTxtBehavior robotTxtBehavior);
	
	public void setScheduledUrlsData(IScheduledURLStore scheduledUrls);
	
	public void setProcessedUrlsData(IProcessedURLStore processedUrls);
	
	public void setRobotTxtData(IRobotTxtStore robotTxtData);
	
	public void setHttpClient(HttpClient httpClient);

}
