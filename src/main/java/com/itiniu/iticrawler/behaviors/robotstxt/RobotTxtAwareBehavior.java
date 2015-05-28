package com.itiniu.iticrawler.behaviors.robotstxt;

import java.io.IOException;
import java.nio.charset.Charset;

import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotsParser;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.SimpleRobotRulesParser;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
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

import com.itiniu.iticrawler.config.ConfigSingleton;
import com.itiniu.iticrawler.crawler.rotottxt.RobotsTxtCache;

/**
 * Implementation of the {@link RobotsTxtBehavior} interface.
 * This behavior is used should robots.txt be enabled in the configuration.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class RobotTxtAwareBehavior implements RobotsTxtBehavior
{
	private static final Logger LOG = LogManager.getLogger(RobotTxtAwareBehavior.class);

	@Override
	public void fetchRobotTxt(URLInfo url, HttpClient httpClient,
			RobotsTxtCache robotTxtData)
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

				//this.parse(url, pageContent, robotTxtData);
				this.parse(url,pageContent,entity.getContentType().getValue(),robotTxtData);
			}
			else
			{
				//todo
				//robotTxtData.insertRule(url, new RobotTxtNotFoundDirective());

				LOG.info("No robots.txt found for Host: " + url.getDomain());
			}
		}
		catch (IOException e)
		{
			//todo
			//robotTxtData.insertRule(url, new RobotTxtNotFoundDirective());
			LOG.error("Error occured while fetching the robots.txt for page: " + url.toString(), e);
		}
		finally
		{
			try
			{
				if(response != null) response.close();
			}
			catch (IOException e)
			{
				//Close Silently still log the incident
				LOG.warn("An error occured while closing the HTTP request: " + request.getURI(), e);
			}
		}

	}

	private void parse(URLInfo url, String robotsTxt, String contentType, RobotsTxtCache robotTxtStore)
	{
		BaseRobotsParser parser = new SimpleRobotRulesParser();
		BaseRobotRules rules = parser.parseContent(url.toString(), robotsTxt.getBytes(), contentType, ConfigSingleton.INSTANCE.getUserAgent());
		robotTxtStore.insertRule(url, rules);
	}


}
