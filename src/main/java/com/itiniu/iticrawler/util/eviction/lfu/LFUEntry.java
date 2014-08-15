package com.itiniu.iticrawler.util.eviction.lfu;

import java.util.Map;

/**
 * Node carrying the data of the LFU Cache.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 * @param <T>
 */
@SuppressWarnings({"unchecked"})
public class LFUEntry<K, V> implements Map.Entry<K, V>
{
	private V value;
	private K key;

	private FrequencyNode<K, V> frequencyNode;

	public LFUEntry(K key, V value)
	{
		this.key = key;
		this.value = value;
	}
	
	public FrequencyNode<K, V> getFrequencyNode()
	{
		return frequencyNode;
	}

	public void setFrequencyNode(FrequencyNode<K, V> frequencyNode)
	{
		this.frequencyNode = frequencyNode;
	}

	@Override
	public K getKey()
	{
		return this.key;
	}

	@Override
	public V getValue()
	{
		return this.value;
	}

	@Override
	public V setValue(V value)
	{
		return this.value = value;
	}
	
	@Override
	 public final boolean equals(Object o) {
         if (!(o instanceof LFUEntry))
             return false;
		LFUEntry<K,V> e = (LFUEntry<K,V>)o;
         Object k1 = getKey();
         Object k2 = e.getKey();
         if (k1 == k2 || (k1 != null && k1.equals(k2))) {
             Object v1 = getValue();
             Object v2 = e.getValue();
             if (v1 == v2 || (v1 != null && v1.equals(v2)))
                 return true;
         }
         return false;
     }

	@Override
    public final int hashCode() {
         return (key==null   ? 0 : key.hashCode()) ^
                (value==null ? 0 : value.hashCode());
     }
}
