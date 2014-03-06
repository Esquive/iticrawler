package com.itiniu.iticrawler;

import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.crawler.inte.AbstractPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class DefaultCrawlBehavior implements ICrawlBehavior
{
	//Getting the logger
	protected static final Logger logger = LogManager.getLogger(DefaultCrawlBehavior.class.getName());
	



    @Override
    public boolean shouldScheduleURL(AbstractPage page, URLWrapper url) {
        return false;
    }

    @Override
	public void processPage(AbstractPage page)
	{
		logger.info("Crawling: " + page.getUrl().toString());
		
		FileOutputStream out = null;
		BufferedOutputStream bout= null;

        String fileName = page.getUrl().getUrl().getPath();
        fileName = fileName.substring(1);

		File pageFile = null;
		try {
			pageFile = new File(fileName); //new File(UUID.randomUUID() + ".html");
			if(!pageFile.exists()) pageFile.createNewFile();
			out = new FileOutputStream(pageFile);
			bout = new BufferedOutputStream(out);
			page.writeToOutputStream(bout);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				bout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

				
		
	}

    @Override
    public void handleStatuScode(AbstractPage page) {

    }



}
