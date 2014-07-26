package com.itiniu.iticrawler.behaviors.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itiniu.iticrawler.behaviors.inte.IRobotTxtBehavior;
import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.impl.DefaultRobotTxtDirective;
import com.itiniu.iticrawler.crawler.impl.RobotTxtNotFoundDirective;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.rotottxtdata.inte.IRobotTxtStore;

/**
 * Implementation of the {@link IRobotTxtBehavior} interface.
 * This behavior is used should robots.txt be enabled in the configuration.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class RobotTxtAwareBehavior implements IRobotTxtBehavior 
{
	private static final Logger LOG = LogManager.getLogger(RobotTxtAwareBehavior.class);
	
	private String USER_AGENT_PATTERN = "[Uu]ser-[Aa]gent.*";
	private String DISALLOW_PATTERN = "[dD]isallow.*";
	private String ALLOW_PATTERN = "[Aa]llow.*";
	private String CRAWL_DELAY_PATTERN = "[Cc]rawl-[Dd]elay.*";
	

	@Override
	public void fetchRobotTxt(URLWrapper url, HttpClient httpClient,
			IRobotTxtStore robotTxtData)
	{
		HttpGet request = null;
		CloseableHttpResponse response = null;

		try
		{
			request = new HttpGet(url.getProtocol() + "://" + url.getDomain() + "/robots.txt");
			request.setProtocolVersion(HttpVersion.HTTP_1_1);
			
		    response = (CloseableHttpResponse)httpClient.execute(request);
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
				LOG.info("No robots.txt found for page: " + url.toString());
			}
		}
		catch (IOException e)
		{
			robotTxtData.insertRule(url, new RobotTxtNotFoundDirective());
			LOG.error("Error occured while fetching the robots.txt for page: " + url.toString());
		}
		finally
		{
			try
			{
				if(response != null) response.close();
			}
			catch (IOException e)
			{
				//Close Silently
			}
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
			else if(cString.matches(this.CRAWL_DELAY_PATTERN))
			{
				cString = cString.substring(cString.indexOf(":") + 1);
				cString = cString.trim();
				Scanner scanner = new Scanner(cString);
				int delay = scanner.nextInt();
				scanner.close();
				
				directive.addDelay(delay);
			}
			else
			{
				isRelevant = false;
			}
		}
		
		robotTxtData.insertRule(url, directive);

	}
}
