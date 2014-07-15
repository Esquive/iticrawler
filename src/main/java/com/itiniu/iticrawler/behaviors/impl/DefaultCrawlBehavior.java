package com.itiniu.iticrawler.behaviors.impl;

import com.itiniu.iticrawler.behaviors.inte.ICrawlBehavior;
import com.itiniu.iticrawler.crawler.impl.Page;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;

/**
 * Created by falk.e on 28/02/14.
 */
public class DefaultCrawlBehavior implements ICrawlBehavior {

    @Override
    public boolean shouldScheduleURL(Page page, URLWrapper url) {
        return true;
    }

    @Override
    public void processPage(Page page) {
        //Do Nothing
    }

    @Override
    public void handleStatuScode(Page page) {
        //Do Nothing
    }
}
