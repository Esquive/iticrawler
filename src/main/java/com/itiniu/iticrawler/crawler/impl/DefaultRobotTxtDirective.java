package com.itiniu.iticrawler.crawler.impl;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.itiniu.iticrawler.crawler.inte.IRobotTxtDirective;


public class DefaultRobotTxtDirective implements IRobotTxtDirective, Serializable, IdentifiedDataSerializable
{
	private static final long serialVersionUID = -6746911164640866605L;
	
	private Set<String> disallowed = null;
	private Set<String> allowed = null;
	
	private int delay = -1;
	
	private boolean containsDisallowWildcard = false;
	private boolean containsAllowWildcard = false;
	
	public DefaultRobotTxtDirective()
	{
		this.disallowed = new HashSet<>();
		this.allowed = new HashSet<>();
	}
	
	@Override
	public void addAllowEntry(String entry)
	{
		this.allowed.add(entry);
	}
	
	@Override
	public void addDisallowEntry(String entry)
	{
		if(!this.containsDisallowWildcard && entry.equals("/"))
		{
			this.containsDisallowWildcard = true;
		}
		else if(!this.containsAllowWildcard && entry.equals("") && this.disallowed.size() == 0)
		{
			this.containsAllowWildcard = true;
		}
		else
		{
			this.containsAllowWildcard = false;
			this.disallowed.add(entry);
		}
	}
	
	@Override
	public boolean allows(String path)
	{
		boolean toReturn = true;
		
		if (!this.containsAllowWildcard)
		{
			String pathBuilder = "/";
			String curToken = null;
			int tokenCount = -1;
			int it = 0;

			boolean cIsDisallowed = false;

			StringTokenizer cToken = new StringTokenizer(path, "/");
			tokenCount = cToken.countTokens();

			while (cToken.hasMoreElements())
			{
				it++;

				curToken = cToken.nextToken();

				pathBuilder += curToken;

				if (it == tokenCount)
				{
					if (curToken.lastIndexOf(".") == -1)
					{
						pathBuilder += "/";
					}
				}
				else
				{
					pathBuilder += "/";
				}

				if (!cIsDisallowed)
				{
					if (this.isDisallowed(pathBuilder))
					{
						cIsDisallowed = true;
						toReturn = false;

					}
				}
				else if (cIsDisallowed)
				{
					if (this.allowed.isEmpty())
					{
						toReturn = false;
						break;
					}
					else if (this.isAllowed(pathBuilder))
					{
						toReturn = true;
						break;
					}
				}
			}
		}

		return toReturn;
	}
	
	private boolean isDisallowed(String path)
	{	
		boolean toReturn = false;
		
		if(this.containsDisallowWildcard || this.disallowed.contains(path))
		{
			toReturn = true;	
		}

		return toReturn;
	}
	
	private boolean isAllowed(String path)
	{
		return this.allowed.contains(path);
	}

	@Override
	public void addDelay(int delay)
	{
		this.delay = delay;
	}

	@Override
	public int getDelay()
	{
		return this.delay;
	}
	
	@Override
	public void writeData(ObjectDataOutput out) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFactoryId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getId()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
