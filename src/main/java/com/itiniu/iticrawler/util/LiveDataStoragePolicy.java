package com.itiniu.iticrawler.util;

/**
 * Possible configuration for the frontier storage and the robots.txt data storage.
 * 
 * @author Eric Falk <erfalk at gmail dot com>
 *
 */
public enum LiveDataStoragePolicy
{
	MEMORY,
	MEMORYCLUSTER,
	FILE,
	MEMORY_FILE_SWAP;
}
