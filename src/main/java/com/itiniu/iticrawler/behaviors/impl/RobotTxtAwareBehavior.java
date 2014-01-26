package com.itiniu.iticrawler.behaviors.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.impl.DefaultRobotTxtDirective;
import com.itiniu.iticrawler.crawler.impl.RobotTxtNotFoundDirective;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.livedatastorage.inte.IRobotTxtStore;

public class RobotTxtAwareBehavior implements IRobotTxtBehavior 
{
	
	private String USER_AGENT_PATTERN = "[Uu]ser-[Aa]gent.*";
	private String DISALLOW_PATTERN = "[dD]isallow.*";
	private String ALLOW_PATTERN = "[Aa]llow.*";
	

	@Override
	public void fetchRobotTxt(URLWrapper url, HttpClient httpClient,
			IRobotTxtStore robotTxtData)
	{
//		String hostUrl = url.getUrl().getProtocol() + url.getUrl().getHost()
//				+ url.getUrl().getPort();
		
		String hostUrl = url.toString();

		HttpGet request = new HttpGet(hostUrl + "/robots.txt");

		try
		{
			HttpResponse response = httpClient.execute(request);

			int pageStatus = response.getStatusLine().getStatusCode();

			if (pageStatus == HttpStatus.SC_OK)
			{
				// Getting the content
				HttpEntity entity = response.getEntity();

				// Get the charset of the page
				Charset charset = ContentType.getOrDefault(entity).getCharset();

				// Load the page Content into a String
				String pageContent = EntityUtils.toString(entity, charset);

				this.parse(url, pageContent, robotTxtData);
				
				
			}
			else
			{
				robotTxtData.insertRule(url, new RobotTxtNotFoundDirective());
				// TODO: Log

			}
			
			request.abort();

		}
		catch (ClientProtocolException e)
		{
			robotTxtData.insertRule(url, new RobotTxtNotFoundDirective());
			
			//TODO: Log
			
			e.printStackTrace();
		}
		catch (IOException e)
		{
			robotTxtData.insertRule(url, new RobotTxtNotFoundDirective());
			
			//TODO: Log
			
			e.printStackTrace();
		}

	}

	private void parse(URLWrapper url, String robotTxt, IRobotTxtStore robotTxtData)
	{

		StringTokenizer cTokenizer = new StringTokenizer(robotTxt, "\n");
		String cString = null;
		IRobotTxtDirective directive = new DefaultRobotTxtDirective();
		int commentIndex = -1;

		boolean isRelevant = false;

		while (cTokenizer.hasMoreElements())
		{
			cString = cTokenizer.nextToken();

			cString = cString.trim();
			cString = cString.toLowerCase();

			if (cString.matches(this.USER_AGENT_PATTERN))
			{
				cString = cString.substring(cString.indexOf(":") + 1);

				cString = cString.trim();

				commentIndex = cString.indexOf("#");

				if (commentIndex != -1)
				{
					cString = cString.substring(0, cString.indexOf("#"));
					cString = cString.trim();
				}

				if (cString.contains("*") || cString.contains(ConfigSingleton.INSTANCE.getUserAgent()))
				{
					isRelevant = true;
				}
			}
			else if (cString.matches(this.DISALLOW_PATTERN))
			{
				if (isRelevant)
				{
					cString = cString.substring(cString.indexOf(":") + 1);

					cString = cString.trim();

					commentIndex = cString.indexOf("#");

					if (commentIndex != -1)
					{
						cString = cString.substring(0, cString.indexOf("#"));
						cString = cString.trim();
					}

					directive.addDisallowEntry(cString);
				}
			}
			else if (cString.matches(this.ALLOW_PATTERN))
			{
				if (isRelevant)
				{
					cString = cString.substring(cString.indexOf(":") + 1);

					cString = cString.trim();

					commentIndex = cString.indexOf("#");

					if (commentIndex != -1)
					{
						cString = cString.substring(0, cString.indexOf("#"));
						cString = cString.trim();
					}

					directive.addAllowEntry(cString);
				}
			}
			else
			{
				isRelevant = false;
			}
		}
		
		robotTxtData.insertRule(url, directive);

	}
}
