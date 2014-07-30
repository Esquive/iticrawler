package com.itiniu.iticrawler.util.lfu;

public class FrequencyNode implements Comparable<FrequencyNode>
{
	private int frequency;
	private FrequencyNode next;
	private FrequencyNode previous;
	
	private boolean isHead;
	private boolean isTail;
	
	private int childCount;

	private FrequencyNode()
	{
		this.frequency = 0;
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

	public boolean isHead()
	{
		return isHead;
	}

	public void setHead()
	{
		this.isTail = false;
		this.isHead = true;
	}

	public boolean isTail()
	{
		return isTail;
	}

	public void setTail()
	{
		this.isHead = false;
		this.isTail = true;
	}

	public int getChildCount()
	{
		return childCount;
	}

	public void setChildCount(int childCount)
	{
		this.childCount = childCount;
	}

	@Override
	public int compareTo(FrequencyNode o)
	{
		if(this.frequency < o.frequency)
		{
			return -1;
		}
		else if(this.frequency == o.frequency)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
	@Override public int hashCode()
	{
		return this.frequency;
	}

	
	
	
}
