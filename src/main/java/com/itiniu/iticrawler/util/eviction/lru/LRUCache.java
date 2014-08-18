package com.itiniu.iticrawler.util.eviction.lru;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> implements Map<K, V>
{
	
	private LinkedHashMap<K, V> storage = null;
	private int maxSize = 0;
	
	private ReentrantReadWriteLock lock = null;

	public LRUCache(int maxSize)
	{
		int hashSize = (int) (maxSize / 0.75);
		
		this.storage = new LinkedHashMap<>(hashSize,0.75f,true);
		this.maxSize = maxSize;
		this.lock = new ReentrantReadWriteLock();
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
		return this.storage.get(key);
	}

	@Override
	public V put(K key, V value)
	{
		if(this.maxSize == this.storage.size())
		{
			Iterator<Entry<K,V>> it = this.storage.entrySet().iterator();
			Entry<K,V> ent = null;
			if(it.hasNext())
			{
				ent = it.next();
				this.storage.remove(ent.getKey());
			}
		}
		
		return this.storage.put(key, value);
	}

	@Override
	public V remove(Object key)
	{
		return this.storage.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		this.storage.putAll(m);
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
		return this.storage.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		return this.storage.entrySet();
	}

}
