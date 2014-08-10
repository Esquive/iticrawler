package com.itiniu.iticrawler.util.lfu;

/**
 * Node carrying the data of the LFU Cache.
 * @author Eric Falk <erfalk at gmail dot com>
 *
 * @param <T>
 */
public class ContentNode<T>
{
	private T nodeContent;
	private FrequencyNode<T> frequencyNode;
	
	public ContentNode(T nodeContent)
	{
		this.nodeContent = nodeContent;
	}
	
	public T getContent()
	{
		return this.nodeContent;
	}

	public FrequencyNode<T> getFrequencyNode()
	{
		return frequencyNode;
	}

	public void setFrequencyNode(FrequencyNode<T> frequencyNode)
	{
		this.frequencyNode = frequencyNode;
	}
	
	@Override
	public int hashCode()
	{
		return nodeContent.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this.nodeContent.equals(o);
	}
}
