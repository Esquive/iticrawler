package com.itiniu.iticrawler;

import java.io.*;
import java.util.UUID;

import com.itiniu.iticrawler.exceptions.OutputStreamPageExtractionException;
import com.itiniu.iticrawler.tools.CloseableByteArrayOutputStream;
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
        return false;
    }

    @Override
	public void processPage(AbstractPage page)
	{
		logger.info("Crawling: " + page.getUrl().toString());
		
//		FileOutputStream out = null;
//		BufferedOutputStream bout= null;
//
//        String fileName = page.getUrl().getUrl().getPath();
//        fileName = fileName.substring(1);
//
//		File pageFile = null;
//		try {
//			pageFile = new File(fileName); //new File(UUID.randomUUID() + ".html");
//			if(!pageFile.exists()) pageFile.createNewFile();
//			out = new FileOutputStream(pageFile);
//			bout = new BufferedOutputStream(out);
//			org.apache.commons.io.IOUtils.copy(page.getInStream(), bout);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally
//		{
//			try {
//				bout.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

        FileOutputStream out = null;
        BufferedOutputStream bout = null;
        CloseableByteArrayOutputStream pageOut = (CloseableByteArrayOutputStream)page.getOutStream();

        String fileName = page.getUrl().getUrl().getPath();
        fileName = fileName.substring(1);

        File pageFile = null;

        try
        {
            pageFile = new File(fileName);
            out = new FileOutputStream(pageFile);
            bout = new BufferedOutputStream(out);

            while(true)
            {
                pageOut.writeTo(bout);

                if(pageOut.isClosed())
                {
                    if(pageOut.getWroteToCount() < pageOut.getWroteCount())
                    {
                        pageOut.writeTo(bout);
                    }
                    break;
                }
            }


        }
            catch (IOException e)
        {

        }
        finally {
            try
            {
            bout.flush();
            bout.close();
            }
            catch(IOException e)
            {

            }
        }

				
		
	}

    @Override
    public void handleStatuScode(AbstractPage page) {

    }



}
