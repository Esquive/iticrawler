package com.itiniu.iticrawler.util.eviction.lfu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FrequencyNode<K,V> implements Comparable<FrequencyNode<K,V>>
{
	private int frequency;
	private FrequencyNode<K,V> next;
	private FrequencyNode<K,V> previous;
	
	private Set<LFUEntry<K,V>> children;
	
	public FrequencyNode(int frequency)
	{
		this.children = new HashSet<>();
		this.frequency = frequency;
	}
	
	public void addChild(LFUEntry<K,V> node)
	{
		this.children.add(node);
	}
	
	public void removeChild(LFUEntry<K,V> node)
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

	public FrequencyNode<K,V> getNext()
	{
		return next;
	}

	public void setNext(FrequencyNode<K,V> next)
	{
		this.next = next;
	}

	public FrequencyNode<K,V> getPrevious()
	{
		return previous;
	}

	public void setPrevious(FrequencyNode<K,V> previous)
	{
		this.previous = previous;
	}

	public int getChildCount()
	{
		return this.children.size();
	}

	public Collection<LFUEntry<K,V>> getChildren()
	{
		return this.children;
	}

	@Override
	public int compareTo(FrequencyNode<K,V> o)
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
