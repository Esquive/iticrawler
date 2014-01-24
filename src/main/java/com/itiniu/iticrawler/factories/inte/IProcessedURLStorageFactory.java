package com.itiniu.iticrawler.factories.inte;

import com.itiniu.iticrawler.livedatastorage.inte.IProcessedURLStore;


public interface IProcessedURLStorageFactory
{
	public IProcessedURLStore getProcessedUrlStorage();
}
