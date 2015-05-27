package com.itiniu.iticrawler.util;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by ericfalk on 24/05/15.
 */
public class CrawledURLStore implements MapStore<Integer, URLInfo> {

    StorageCluster cluster;
    Keyspace keyspace;

    public CrawledURLStore(StorageCluster storageCluster)
    {
        this.cluster = storageCluster;
       this.keyspace = storageCluster.getKeyspace();
    }

    @Override
    public void store(Integer integer, URLInfo urlInfo) {
        try {
            keyspace.prepareColumnMutation(cluster.getCrawledUrlColumn(),integer,"Column1")
            .putValue(urlInfo.toString(), null)
                    .execute();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeAll(Map<Integer, URLInfo> map) {

    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public void deleteAll(Collection<Integer> collection) {

    }

    @Override
    public URLInfo load(Integer integer) {
        Column<String> result = null;
        URLInfo value = null;
        try {
            result = keyspace.prepareQuery(cluster.getCrawledUrlColumn())
                    .getKey(integer)
                    .getColumn("Column1")
                    .execute().getResult();

            value = new URLInfo(result.getStringValue());

            //TODO If an entry is not a in cassandra an Exception is thrown!!!
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return value;
    }

    @Override
    public Map<Integer, URLInfo> loadAll(Collection<Integer> collection) {
        return null;
    }

    @Override
    public Set<Integer> loadAllKeys() {
        return null;
    }
}
