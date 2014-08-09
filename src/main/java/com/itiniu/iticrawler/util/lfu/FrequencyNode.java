package com.itiniu.iticrawler.util.lfu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.formula.functions.T;

public class FrequencyNode implements Comparable<FrequencyNode>
{
	private int frequency;
	private FrequencyNode next;
	private FrequencyNode previous;
	
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

	public FrequencyNode getNext()
	{
		return next;
	}

	public void setNext(FrequencyNode next)
	{
		this.next = next;
	}

	public FrequencyNode getPrevious()
	{
		return previous;
	}

	public void setPrevious(FrequencyNode previous)
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
	public int compareTo(FrequencyNode o)
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
