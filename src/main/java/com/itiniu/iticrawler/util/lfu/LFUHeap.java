package com.itiniu.iticrawler.util.lfu;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.formula.functions.T;

/**
 * Class to manage the LFU information for cache eviction
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public class LFUHeap
{
	private AtomicInteger size = new AtomicInteger(0);
	private FrequencyNode head = new FrequencyNode(0);

	public void addNode(ContentNode<T> node)
	{
		FrequencyNode first = this.head.getNext();
		if (first == null)
		{
			this.head.setNext(new FrequencyNode(1));
			this.head.getNext().addChild(node);
			this.head.getNext().setPrevious(this.head);
			node.setFrequencyNode(this.head.getNext());
			this.size.incrementAndGet();
		}
		else
		{
			if (first.getFrequency() == 1)
			{
				first.addChild(node);
				node.setFrequencyNode(first);
			}
			else
			{
				FrequencyNode newNode = new FrequencyNode(1);
				newNode.addChild(node);

				this.head.setNext(newNode);
				newNode.setNext(first);
				first.setPrevious(newNode);
				newNode.setPrevious(this.head);
				
				node.setFrequencyNode(newNode);
			}
		}

	}

	public void incrementFrequency(ContentNode<T> node)
	{
		FrequencyNode current, previous, next, newNode;
		current = node.getFrequencyNode();
		previous = current.getPrevious();
		next = current.getNext();

		int nextFreq = current.getFrequency() + 1;
		current.removeChild(node);

		if (next == null)
		{
			newNode = new FrequencyNode(nextFreq);
			newNode.addChild(node);
			node.setFrequencyNode(newNode);
			if (current.getChildCount() == 0)
			{
				newNode.setPrevious(previous);
				newNode.setNext(newNode);
			}
			else
			{
				newNode.setPrevious(current);
				current.setNext(newNode);
				this.size.incrementAndGet();
			}
		}
		else if (next.getFrequency() == nextFreq)
		{
			next.addChild(node);
			if (current.getChildCount() == 0)
			{
				previous.setNext(next);
				next.setPrevious(previous);
				node.setFrequencyNode(next);
			}
		}
		else if (next.getFrequency() > nextFreq)
		{
			newNode = new FrequencyNode(nextFreq);
			newNode.addChild(node);
			newNode.setNext(next);
			next.setPrevious(newNode);
			node.setFrequencyNode(newNode);
			this.size.incrementAndGet();
			if(current.getChildCount() == 0)
			{
				newNode.setPrevious(previous);
				previous.setNext(newNode);
			}
			else
			{
				current.setNext(newNode);
				newNode.setPrevious(current);
			}
		}
	}

	public Collection<ContentNode<T>> getNodesToEvict()
	{
		FrequencyNode toEvict = this.head.getNext();
		FrequencyNode next = toEvict.getNext();
		this.head.setNext(next);
		next.setPrevious(this.head);
		return toEvict.getChildren();
	}

	public int getSize()
	{
		return size.get();
	}

}