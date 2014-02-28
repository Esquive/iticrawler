package com.itiniu.iticrawler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.io.IOUtils;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.crawler.inte.AbstractCrawler;
import com.itiniu.iticrawler.crawler.inte.AbstractPage;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public class DefaultCrawlBehavior implements ICrawlBehavior
{
	//Getting the logger
	protected static final Logger logger = LogManager.getLogger(DefaultCrawlBehavior.class.getName());
	



    @Override
    public boolean shouldScheduleURL(AbstractPage page, URLWrapper url) {
        return true;
    }

    @Override
	public void processPage(AbstractPage page)
	{
		logger.info("Crawling: " + page.getUrl().toString());
		
		FileOutputStream out = null;
		BufferedOutputStream bout= null;
		File pageFile = null;
		try {
			pageFile = new File(UUID.randomUUID() + ".html");
			if(!pageFile.exists()) pageFile.createNewFile();
			out = new FileOutputStream(pageFile);
			bout = new BufferedOutputStream(out);
			org.apache.commons.io.IOUtils.copy(page.getStream(), bout);
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
