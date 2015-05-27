package com.itiniu.iticrawler.crawler.rotottxt.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.itiniu.iticrawler.crawler.rotottxt.inte.IRobotTxtDirective;
import com.itiniu.iticrawler.util.serialization.IdentifiedSerializationFactory;

/**
 * Default implementation of the {@link IRobotTxtDirective} interface
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 * 
 */
public class DefaultRobotTxtDirective implements IRobotTxtDirective, IdentifiedDataSerializable
{
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
		if (!this.containsDisallowWildcard && entry.equals("/"))
		{
			this.containsDisallowWildcard = true;
		}
		else if (!this.containsAllowWildcard && entry.equals("") && this.disallowed.size() == 0)
		{
			this.containsAllowWildcard = true;
		}
		else
		{
			this.containsAllowWildcard = false;
			this.disallowed.add(entry);
		}
	}

	/**
	 * Call this method to determine if an URL can be crawled.
	 */
	@Override
	public boolean allows(String url)
	{
		boolean toReturn = true;
		String path = null;
		try
		{
			path = this.getPath(url);
		}
		catch(MalformedURLException e)
		{
			//Is not supposed to happen
			return false;
		}

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
	
	private String getPath(String url) throws MalformedURLException
	{
			return new URL(url).getPath();
	}
	

	/**
	 * Internal Method called by {@link IRobotTxtDirective#allows(String)}
	 * 
	 * @param path
	 * @return
	 */
	private boolean isDisallowed(String path)
	{
		boolean toReturn = false;

		//TODO: correct robots.txt behavior
		if (this.containsDisallowWildcard || this.disallowed.contains(path))
		{
			toReturn = true;
		}

		return toReturn;
	}

	/**
	 * Internal Method called by {@link IRobotTxtDirective#allows(String)}
	 * 
	 * @param path
	 * @return
	 */
	private boolean isAllowed(String path)
	{
		return this.allowed.contains(path);
	}

	/**
	 * Method to add the delay if specified by robots.txt.
	 */
	@Override
	public void addDelay(int delay)
	{
		this.delay = delay;
	}

	/**
	 * Method to fetch the delay for the crawl process.
	 */
	@Override
	public int getDelay()
	{
		return this.delay;
	}

	// TODO: Add identifiable serializable methods.
	@Override
	public void writeData(ObjectDataOutput out) throws IOException
	{
		out.writeBoolean(containsAllowWildcard);
		out.writeBoolean(containsDisallowWildcard);
		out.writeInt(delay);
		out.writeInt(disallowed.size());
		out.writeInt(allowed.size());
		for(String dis : disallowed)
		{
			out.writeUTF(dis);
		}
		for(String all : allowed)
		{
			out.writeUTF(all);
		}
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException
	{
		this.containsAllowWildcard = in.readBoolean();
		this.containsDisallowWildcard = in.readBoolean();
		this.delay = in.readInt();
		int disSize = in.readInt();
		int allSize = in.readInt();
		this.disallowed = new HashSet<>();
		this.allowed = new HashSet<>();
		for(int i = 0; i<disSize; i++)
		{
			this.disallowed.add(in.readUTF());
		}for(int i = 0; i<allSize; i++)
		{
			this.allowed.add(in.readUTF());
		}
	}

	@Override
	public int getFactoryId()
	{
		return IdentifiedSerializationFactory.FACTORY_ID;
	}

	@Override
	public int getId()
	{
		return IdentifiedSerializationFactory.ROBOT_DIRECTIVE_TYPE;
	}

}
