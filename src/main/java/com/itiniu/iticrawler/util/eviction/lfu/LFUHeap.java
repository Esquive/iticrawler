package com.itiniu.iticrawler.util.eviction.lfu;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Class to manage the LFU information for cache eviction.
 * </br>
 * </br>
 * Method calls of this class ARE NOT THREAD-SAFE.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 */
public class LFUHeap <K,V>
{
	private AtomicInteger size = new AtomicInteger(0);
	private FrequencyNode<K,V> head = new FrequencyNode<K,V>(0);

	/**
	 * Method to add a new LFUEntry to the structure.
	 * </br> The LFU structure is automatically re-balanced
	 * @param node
	 */
	public void addNode(LFUEntry<K,V> node)
	{
		FrequencyNode<K,V> first = this.head.getNext();
		if (first == null)
		{
			this.head.setNext(new FrequencyNode<K,V>(1));
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
				FrequencyNode<K,V> newNode = new FrequencyNode<K,V>(1);
				newNode.addChild(node);

				this.head.setNext(newNode);
				newNode.setNext(first);
				first.setPrevious(newNode);
				newNode.setPrevious(this.head);
				
				node.setFrequencyNode(newNode);
			}
		}

	}

	/**
	 * Method to increment the use statistic of a node.
	 * </br> The LFU structure is automatically re-balanced
	 * @param node
	 */
	public void incrementFrequency(LFUEntry<K,V> node)
	{
			FrequencyNode<K, V> current, previous, next, newNode;
			current = node.getFrequencyNode();
			previous = current.getPrevious();
			next = current.getNext();
			int nextFreq = current.getFrequency() + 1;
			current.removeChild(node);
			if (next == null)
			{
				newNode = new FrequencyNode<K, V>(nextFreq);
				newNode.addChild(node);
				node.setFrequencyNode(newNode);
				if (current.getChildCount() == 0)
				{
					newNode.setPrevious(previous);
					previous.setNext(newNode);
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
				newNode = new FrequencyNode<K, V>(nextFreq);
				newNode.addChild(node);
				newNode.setNext(next);
				next.setPrevious(newNode);
				node.setFrequencyNode(newNode);
				this.size.incrementAndGet();
				if (current.getChildCount() == 0)
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

	/**
	 * Method to remove a node from the Cache and so from the LFUHeap.
	 * </br> The LFU structure is automatically re-balanced
	 * @param node
	 */
	public void removeNode(LFUEntry<K,V> node)
	{
			FrequencyNode<K, V> freq = node.getFrequencyNode();
			freq.removeChild(node);
			if(freq.getChildCount() == 0)
			{
				FrequencyNode<K, V> next = freq.getNext();
				FrequencyNode<K, V> prev = freq.getPrevious();
				
				prev.setNext(next);
				next.setPrevious(prev);
			}
	}
	
	/**
	 * Method to get the nodes with the smallest use statistic so they can be evicted.
	 * @return
	 */
	public Collection<LFUEntry<K,V>> getNodesToEvict()
	{
			FrequencyNode<K, V> toEvict = this.head.getNext();
			FrequencyNode<K, V> next = toEvict.getNext();
			this.head.setNext(next);
			next.setPrevious(this.head);
			return toEvict.getChildren();
	}

	/**
	 * Returns the size of the LFUHEap
	 * @return
	 */
	public int getSize()
	{
			return size.get();
	}
	
}