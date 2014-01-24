package com.itiniu.iticrawler.crawler.inte;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.itiniu.iticrawler.httptools.impl.NormalizedURLWrapper;
import com.itiniu.iticrawler.httptools.impl.URLWrapper;
import com.itiniu.iticrawler.httptools.impl.UrlNormalizer;
import com.itiniu.iticrawler.httptools.inte.URLExtensionInterface;


public abstract class AbstractPage
{
	protected URLWrapper url = null;
	protected String html = null;
	protected List<URLWrapper> outgoingURLs = null;
	
	protected int statusCode = -1;
	
	
	
	public URLWrapper getUrl()
	{
		return url;
	}
	
	public void setUrl(URLWrapper curURL)
	{
		this.url = curURL;
	}
	
	public String getHtml()
	{
		return html;
	}
	
	public void setHtml(String html)
	{
		this.html = html;
	}
		
	public List<URLWrapper> getOutgoingURLs()
	{
		if(this.outgoingURLs != null)
		{
			return this.outgoingURLs;
		}
		else
		{
			this.outgoingURLs = extractUrls();
			return this.outgoingURLs;
		}
	}
	
	public int getStatusCode()
	{
		return this.statusCode;
	}
	
	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}
	
	
	protected List<URLWrapper> extractUrls()
	{
		List<URLWrapper> toReturn = new ArrayList<>();

		Document doc = Jsoup.parse(this.html, this.url.toString());

		Iterator<Element> cElemIt = doc.select("a, link, area").iterator();

		Element cElem = null;
		String url = null;
		URLWrapper toAdd = null;

		while (cElemIt.hasNext())
		{
			cElem = cElemIt.next();
			url = cElem.attr("abs:href");

			if (url.length() > 0)
			{
//				try
//				{
//					url = UrlNormalizer.normalize(url);
//				}
//				catch (MalformedURLException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				try
				{
//					toAdd = ;
//					toAdd.setParentURL(this.url);
//					toAdd.setUrlDepth(this.url.getUrlDepth() + 1);
					toReturn.add(new NormalizedURLWrapper(url));
				}
				catch (MalformedURLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return toReturn;
	}
	
}
