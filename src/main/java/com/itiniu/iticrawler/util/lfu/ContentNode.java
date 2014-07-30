package com.itiniu.iticrawler.util.lfu;

public class ContentNode<T>
{
	private T nodeContent;
	private FrequencyNode frequencyNode;
	
	public ContentNode(T nodeContent)
	{
		this.nodeContent = nodeContent;
	}
	
	public T getContent()
	{
		return this.nodeContent;
	}

	public FrequencyNode getFrequencyNode()
	{
		return frequencyNode;
	}

	public void setFrequencyNode(FrequencyNode frequencyNode)
	{
		this.frequencyNode = frequencyNode;
	}
	
}
