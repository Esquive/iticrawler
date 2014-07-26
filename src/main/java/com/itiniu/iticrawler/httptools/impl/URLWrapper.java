package com.itiniu.iticrawler.httptools.impl;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * Class wrapping the URL type. It contains additional information as the parentURL, the redirecting URL. 
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class URLWrapper implements Serializable
{
	
	private static final long serialVersionUID = 1665293006612443211L;
	
	protected URL url = null;
	protected int hashCode;
	protected String urlString = null;
	protected String fullLink;
	protected String title;
	protected String text;
	protected String rel;
	protected String redirectedFrom;
	protected int urlDepth;
	protected URLWrapper parentURL;
	protected boolean isImage;
	protected boolean isAnchor;

	public URLWrapper()
	{

	}

	public URLWrapper(String url) throws MalformedURLException
	{
		this.url = new URL(url);
		this.urlString = url;
		this.hashCode = this.urlString.hashCode();
	}

	@Override
	public String toString()
	{
		return this.urlString;
	}

	@Override
	public int hashCode()
	{
		return this.hashCode;
	}

	public String getDomain()
	{
		return this.url.getHost();
	}

	public Set<String> getNonPublicURLParts()
	{
		return URLCanonicalizer.filterPublicSuffix(this.url.getHost());
	}

	public int getUrlDepth()
	{
		return this.urlDepth;
	}

	public void setUrl(String url) throws MalformedURLException
	{
		this.url = new URL(url);
		this.urlString = url;
	}

	public void setUrlDepth(int urlDepth)
	{
		this.urlDepth = urlDepth;
	}

	public URLWrapper getParentURL()
	{
		return parentURL;
	}

	public void setParentURL(URLWrapper parentURL)
	{
		this.parentURL = parentURL;
	}

	public String getProtocol()
	{
		return this.url.getProtocol();
	}

	public int getPort()
	{
		return this.url.getPort();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getRel()
	{
		return rel;
	}

	public void setRel(String rel)
	{
		this.rel = rel;
	}

	public boolean isImage()
	{
		return isImage;
	}

	public void setImage(boolean isImage)
	{
		this.isImage = isImage;
	}

	public boolean isAnchor()
	{
		return isAnchor;
	}

	public void setAnchor(boolean isAnchor)
	{
		this.isAnchor = isImage;
	}

	public void setFullLink(String fullLink)
	{
		this.fullLink = fullLink;
	}

	public String getFullLink()
	{
		return this.fullLink;
	}

	public String getRedirectedFrom()
	{
		return redirectedFrom;
	}

	public void setRedirectedFrom(String redirectedFrom)
	{
		this.redirectedFrom = redirectedFrom;
	}

	private URLWrapper(Builder builder) throws MalformedURLException
	{
		this.urlString = builder.urlString;
		this.url = new URL(this.urlString);
		this.hashCode = this.urlString.hashCode();
		this.fullLink = builder.fullLink;
		this.title = builder.title;
		this.text = builder.text;
		this.rel = builder.rel;
		this.redirectedFrom = builder.redirectedFrom;
		this.urlDepth = builder.urlDepth;
		this.parentURL = builder.parentURL;
		this.isImage = builder.isImage;
		this.isAnchor = builder.isAnchor;
	}

	public static class Builder
	{
		private String urlString;
		private String fullLink;
		private String title;
		private String text;
		private String rel;
		private String redirectedFrom;
		private int urlDepth;
		private URLWrapper parentURL;
		private boolean isImage;
		private boolean isAnchor;

		public Builder urlString(String urlString)
		{
			this.urlString = urlString;
			return this;
		}

		public Builder fullLink(String fullLink)
		{
			this.fullLink = fullLink;
			return this;
		}

		public Builder title(String title)
		{
			this.title = title;
			return this;
		}

		public Builder text(String text)
		{
			this.text = text;
			return this;
		}

		public Builder rel(String rel)
		{
			this.rel = rel;
			return this;
		}

		public Builder redirectedFrom(String redirectedFrom)
		{
			this.redirectedFrom = redirectedFrom;
			return this;
		}

		public Builder urlDepth(int urlDepth)
		{
			this.urlDepth = urlDepth;
			return this;
		}

		public Builder parentURL(URLWrapper parentURL)
		{
			this.parentURL = parentURL;
			return this;
		}

		public Builder isImage(boolean isImage)
		{
			this.isImage = isImage;
			return this;
		}

		public Builder isAnchor(boolean isAnchor)
		{
			this.isAnchor = isAnchor;
			return this;
		}

		public URLWrapper build() throws MalformedURLException
		{
			return new URLWrapper(this);
		}
	}
}
