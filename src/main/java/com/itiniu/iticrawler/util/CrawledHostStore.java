package com.itiniu.iticrawler.util;

import com.hazelcast.core.MapStore;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by ericfalk on 28/05/15.
 */
public class CrawledHostStore implements MapStore<String, Long> {

    private static final String VALUE_COLUMN = "VALUE";

    StorageCluster cluster;
    Keyspace keyspace;

    public CrawledHostStore(StorageCluster storageCluster) {
        this.cluster = storageCluster;
        this.keyspace = storageCluster.getKeyspace();
    }

    @Override
    public void store(String s, Long aLong) {
        try {
            keyspace.prepareColumnMutation(cluster.getHostColumn(), s, VALUE_COLUMN)
                    .putValue(aLong, null)
                    .execute();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeAll(Map<String, Long> map) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void deleteAll(Collection<String> collection) {

    }

    @Override
    public Long load(String s) {
        Column<String> result = null;
        Long value = null;
        try {
            result = keyspace.prepareQuery(cluster.getHostColumn())
                    .getKey(s)
                    .getColumn(VALUE_COLUMN)
                    .execute().getResult();

            value = result.getLongValue();

            //TODO If an entry is not a in cassandra an Exception is thrown!!!
        } catch (NotFoundException e) {
            //Do nothing
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        return value;
    }

    @Override
    public Map<String, Long> loadAll(Collection<String> collection) {
        return null;
    }

    @Override
    public Set<String> loadAllKeys() {
        return null;
    }
}
