package com.itiniu.iticrawler.config;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public enum FileStorageConfig
{
	INSTANCE;

	private final Logger LOG = LogManager.getLogger(FileStorageConfig.class);
	private DB fileStorage;

	FileStorageConfig()
	{
		File folder = new File(ConfigSingleton.INSTANCE.getStorageLocation());
		if (folder.exists() && folder.isDirectory())
		{
			if (!folder.canWrite())
			{
				LOG.error("The Storage folder: \"" + folder.getPath() + "\" is not accessible.");
			}
		}
		else
		{
			folder.mkdirs();
		}

		if (folder.exists() && folder.isDirectory() && folder.canWrite())
		{
			this.fileStorage = DBMaker.newFileDB(new File(folder, "frontier.db")).make();
		}

	}

	public DB getStorageProvider()
	{
		return this.fileStorage;
	}
}
