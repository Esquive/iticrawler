package com.itiniu.iticrawler.util.lfu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FrequencyNode<T> implements Comparable<FrequencyNode<T>>
{
	private int frequency;
	private FrequencyNode<T> next;
	private FrequencyNode<T> previous;
	
	private Set<ContentNode<T>> children;
	
	public FrequencyNode(int frequency)
	{
		this.children = new HashSet<>();
		this.frequency = frequency;
	}
	
	public void addChild(ContentNode<T> node)
	{
		this.children.add(node);
	}
	
	public void removeChild(ContentNode<T> node)
	{
		this.children.remove(node);
	}
	
	public int getFrequency()
	{
		return frequency;
	}

	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
	}

	public FrequencyNode<T> getNext()
	{
		return next;
	}

	public void setNext(FrequencyNode<T> next)
	{
		this.next = next;
	}

	public FrequencyNode<T> getPrevious()
	{
		return previous;
	}

	public void setPrevious(FrequencyNode<T> previous)
	{
		this.previous = previous;
	}

	public int getChildCount()
	{
		return this.children.size();
	}

	public Collection<ContentNode<T>> getChildren()
	{
		return this.children;
	}

	@Override
	public int compareTo(FrequencyNode<T> o)
	{
		if(this.frequency == o.frequency)
		{
			return 0;
		}
		else if(this.frequency < o.frequency)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
	
}
