package com.itiniu.iticrawler.test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.junit.Test;

import ch.sentric.URL;

public class URLNormalizationTest
{

	@Test
	public void test() throws MalformedURLException
	{
		URL cUrl = new URL("http://www.nba.com");
		
		String normUrl = cUrl.getNormalizedUrl();
		
	
		fail("Not yet implemented");
	}

}
