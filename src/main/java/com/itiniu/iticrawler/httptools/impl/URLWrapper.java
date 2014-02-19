package com.itiniu.iticrawler.httptools.impl;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import com.itiniu.iticrawler.httptools.inte.URLExtensionInterface;

public class URLWrapper implements URLExtensionInterface, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1960291945681398107L;

	private URL url = null;
	private String fullLink;
	private String title;
	private String text;
	private String rel;
	protected long lastCrawlTime;
	protected int urlDepth;
	protected URLWrapper parentURL;
	protected boolean isImage;
	protected boolean isAnchor;

	protected URLWrapper() {

	}

	public URLWrapper(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public String toString() {
		return this.url.toString();
	}

	@Override
	public String getRedirectURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDomain() {
		return this.getDomain(this.url.getHost());
	}

	public String getDomain(String domain) {
		String toReturn = null;

		String[] tokens = domain.split("\\.");

		// TODO: Stabilize the function to avoid the index out of bounce
		// execption
		toReturn = tokens[tokens.length - 2] + "." + tokens[tokens.length - 1];

		return toReturn;
	}

	@Override
	public String getSubDomain() {
		return this.getSubdomain(this.url.getHost());
	}

	public String getSubdomain(String host) {

		String toReturn = "";

		int toplevel = host.lastIndexOf(".");

		int subDomainEndingIndex = host.lastIndexOf(".", toplevel - 1);

		if (subDomainEndingIndex != -1) {
			toReturn = host.substring(0, subDomainEndingIndex);
		}

		return toReturn;
	}

	@Override
	public String getAnchor() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLastCrawlTime() {
		return this.lastCrawlTime;
	}

	public int getUrlDepth() {
		return this.urlDepth;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setLastCrawlTime(long lastCrawlTime) {
		this.lastCrawlTime = lastCrawlTime;
	}

	public void setUrlDepth(int urlDepth) {
		this.urlDepth = urlDepth;
	}

	public URLWrapper getParentURL() {
		return parentURL;
	}

	public void setParentURL(URLWrapper parentURL) {
		this.parentURL = parentURL;
	}

	@Override
	public String getProtocol() {

		return this.url.getProtocol();
	}

	@Override
	public int getPort() {
		return this.url.getPort();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}
	
	public boolean isAnchor() {
		return isAnchor;
	}

	public void setAnchor(boolean isAnchor) {
		this.isAnchor = isImage;
	}

	public URL getUrl() {
		return url;
	}
	
	public void setFullLink(String fullLink)
	{
		this.fullLink = fullLink;
	}
	
	public String getFullLink()
	{
		return this.fullLink;
	}

	private URLWrapper(Builder builder) {

	this.url = builder.url;
	this.title = builder.title;
	this.text = builder.text;
	this.rel = builder.rel;
	this.lastCrawlTime = builder.lastCrawlTime;
	this.urlDepth = builder.urlDepth;
	this.parentURL = builder.parentURL;
	this.isImage = builder.isImage;
	this.isAnchor = builder.isAnchor;
	this.fullLink = builder.fullLink;
	}
	
	public static class Builder {
	private URL url;
	private String title;
	private String text;
	private String rel;
	private long lastCrawlTime;
	private int urlDepth;
	private URLWrapper parentURL;
	private boolean isImage;
	private boolean isAnchor;
	private String fullLink;
	

	public Builder url(URL url) {
	this.url = url;
	return this;
	}
	public Builder title(String title) {
	this.title = title;
	return this;
	}
	public Builder text(String text) {
	this.text = text;
	return this;
	}
	public Builder rel(String rel) {
	this.rel = rel;
	return this;
	}
	public Builder lastCrawlTime(long lastCrawlTime) {
	this.lastCrawlTime = lastCrawlTime;
	return this;
	}
	public Builder urlDepth(int urlDepth) {
	this.urlDepth = urlDepth;
	return this;
	}
	public Builder parentURL(URLWrapper parentURL) {
	this.parentURL = parentURL;
	return this;
	}
	public Builder isImage(boolean isImage) {
	this.isImage = isImage;
	return this;
	}
	
	public Builder isAnchor(boolean isAnchor)
	{
		this.isAnchor = isAnchor;
		return this;
	}
	
	public Builder fullLink(String fullLink)
	{
		this.fullLink = fullLink;
		return this;
	}
	
	public URLWrapper build() {
	return new URLWrapper(this);
	}
	}
	
	
}
