package com.itiniu.iticrawler.livedatastorage.inte;

import java.util.Collection;

import com.itiniu.iticrawler.httptools.impl.URLWrapper;

public interface IScheduledURLStore {
	
public void scheduleURL(URLWrapper inURL);
	
	public void scheduleUniqueUrl(URLWrapper inUrl);
	
	public URLWrapper getNextURL();
	
	public Collection<URLWrapper> getNextURLRange(int number);
	
	public boolean isEmpty();
	

}
