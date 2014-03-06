package com.itiniu.iticrawler;



import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.PageExtractionType;
import com.itiniu.iticrawler.crawler.inte.AbstractCrawlController;
import com.itiniu.iticrawler.crawler.impl.DefaultCrawlController;
import com.itiniu.iticrawler.livedatastorage.LiveDataStoragePolicy;


public class Main
{

	/**
	 * Entry point of the program this part is not needed once compiled to lib
	 * @param args
	 */
	public static void main(String[] args)
	{
		ConfigSingleton.INSTANCE.setNumberOfCrawlerThreads(1);
//		ConfigSingleton.INSTANCE.setRobotTxtDataStoragePolicy(LiveDataStoragePolicy.cluster);
//		ConfigSingleton.INSTANCE.setProcessedUrlsStoragePolicy(LiveDataStoragePolicy.cluster);
//		ConfigSingleton.INSTANCE.setScheduledUrlsStoragePolicy(LiveDataStoragePolicy.cluster);
//		ConfigSingleton.INSTANCE.setPolitnessDelay(10000);

        ConfigSingleton.INSTANCE.setExtractionType(PageExtractionType.BY_STREAM);
		ConfigSingleton.INSTANCE.setCustomCrawlBehavior(DefaultCrawlBehavior.class);
		
		AbstractCrawlController mCrawlController = new DefaultCrawlController();
		
		
		
//		mCrawlController.setScheduleUrlBehavior(defaultSchedulBehavior.class);
//		mCrawlController.setProcessPageBehavior(defaultProcessPageBehavior.class);
		
		
		mCrawlController.initComponents();
		//mCrawlController.addSeeds("http://deejing.ibaboon.net");
		//mCrawlController.addSeeds("http://www.nba.com");
//		mCrawlController.addSeeds("http://www.lemonde.fr");
//		mCrawlController.addSeeds("http://www.coolthings.com");
//		mCrawlController.addSeeds("http://www.extremetech.com");
//		mCrawlController.addSeeds("http://www.thisiswhyimbroke.com");

        mCrawlController.addSeeds("http://wordpress.org/latest.zip");
		
		mCrawlController.startCrawling();

		
		
	}

}
