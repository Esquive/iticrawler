package com.itiniu.iticrawler.crawler.inte;

public interface IRobotTxtDirective
{
	public void addAllowEntry(String entry);
	
	public void addDisallowEntry(String entry);
	
	public boolean allows(String path);
	
}
