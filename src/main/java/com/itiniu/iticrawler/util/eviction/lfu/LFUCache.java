package com.itiniu.iticrawler.util.eviction.lfu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

//TODO: Maybe Create another interface to implement 
/**
 * LFUCache: implements the {@link java.util.Map<K,V>} Interface. </br> The
 * cache makes use of a Hashmap for the storage and uses: {@link LFUHeap} for
 * the LFU Eviction. </br></br> The cache IS NOT THREAD-SAFE
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> implements Map<K, V>
{

	private HashMap<K, LFUEntry<K, V>> storage = null;
	private LFUHeap<K, V> heap = null;

	private int maxSize = 0;

	public LFUCache(int maxSize)
	{
		// We create a hashsize bigger than the maxsize to avoid the resize of
		// the structure.
		int hashSize = (int) (maxSize / 0.75) + 1;

		this.storage = new HashMap<>(hashSize, 0.75f);
		this.heap = new LFUHeap<>();
		this.maxSize = maxSize;
	}

	@Override
	public int size()
	{
		return this.storage.size();
	}

	@Override
	public boolean isEmpty()
	{
		return this.storage.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return this.storage.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return this.storage.containsValue(value);
	}

	@Override
	public V get(Object key)
	{
		LFUEntry<K, V> entry = this.storage.get(key);
		if (entry != null)
		{
			this.heap.incrementFrequency(entry);
			return entry.getValue();
		}

		return null;
	}

	@Override
	public V put(K key, V value)
	{
		// First do we need to evict?
		if (this.storage.size() == this.maxSize)
		{
			Collection<LFUEntry<K, V>> toEvict = this.heap.getNodesToEvict();
			for (LFUEntry<K, V> entry : toEvict)
			{
				this.storage.remove(entry.getKey());
			}
		}

		// Put the value in the cache
		LFUEntry<K, V> toReturn = this.storage.put(key, new LFUEntry<K, V>(key, value));
		this.heap.addNode(this.storage.get(key));

		return (toReturn != null) ? toReturn.getValue() : null;
	}

	@Override
	public V remove(Object key)
	{
		// Get the entry
		LFUEntry<K, V> entry = this.storage.get(key);
		this.heap.removeNode(entry);

		entry = this.storage.remove(key);

		return (entry != null) ? entry.getValue() : null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		throw new NotImplementedException("The method putAll of the Map inteface is of no use here.");
	}

	@Override
	public void clear()
	{
		this.storage.clear();
	}

	@Override
	public Set<K> keySet()
	{
		return this.storage.keySet();
	}

	@Override
	public Collection<V> values()
	{
		Collection<LFUEntry<K, V>> values = this.storage.values();
		Collection<V> toReturn = new ArrayList<>(values.size());
		for (LFUEntry<K, V> ent : values)
		{
			toReturn.add(ent.getValue());
		}
		return toReturn;
	}

	@Override
	public Set<Entry<K, V>> entrySet()
	{
		Set<Entry<K, V>> toReturn = new HashSet<>();
		toReturn.addAll(this.storage.values());

		return toReturn;
	}

}
