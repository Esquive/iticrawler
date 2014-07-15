package com.itiniu.iticrawler.livedatastorage.inte;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface IScheduledURLStore {
	
public void scheduleURL(URLWrapper inURL);
	
	public void scheduleUniqueUrl(URLWrapper inUrl);
	
	public URLWrapper getNextURL();
	
	public boolean isEmpty();

}
