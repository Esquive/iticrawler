package com.itiniu.iticrawler.util.lfu;

import java.util.HashSet;
import java.util.Set;

public class LFUHeap
{
	private int size = 0;
	private FrequencyNode head;
	private FrequencyNode tail;
	private Set<FrequencyNode> children;
	
	public LFUHeap()
	{
		this.children = new HashSet<FrequencyNode>();
	} 
	
	public void addNode(FrequencyNode node)
	{
		if(this.size == 0)
		{
			this.head = node;
			this.children.add(node);
			this.size++;
		}
	}
	
	public void removeFrequencyNode(FrequencyNode node)
	{
		
	}
	
	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public FrequencyNode getHead()
	{
		return head;
	}

	public void setHead(FrequencyNode head)
	{
		this.head = head;
	}

	public FrequencyNode getTail()
	{
		return tail;
	}

	public void setTail(FrequencyNode tail)
	{
		this.tail = tail;
	}

}