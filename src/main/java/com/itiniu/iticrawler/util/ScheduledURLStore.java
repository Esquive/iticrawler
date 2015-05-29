package com.itiniu.iticrawler.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.QueueStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by ericfalk on 28/05/15.
 */
public class ScheduledURLStore implements QueueStore<URLInfo> {

    private static final Logger LOG = LogManager.getLogger(ScheduledURLStore.class);

    private static final String VALUE_COLUMN = "VALUE";

    StorageCluster cluster;
    Keyspace keyspace;
    ObjectMapper mapper;

    public ScheduledURLStore(StorageCluster storageCluster) {
        this.cluster = storageCluster;
        this.keyspace = storageCluster.getKeyspace();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void store(Long aLong, URLInfo urlInfo) {
        try {
            String value = this.mapper.writeValueAsString(urlInfo);
            keyspace.prepareColumnMutation(cluster.getScheduledUrlColumn(), aLong, VALUE_COLUMN)
                    .putValue(value, null)
                    .execute();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeAll(Map<Long, URLInfo> map) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void deleteAll(Collection<Long> collection) {

    }

    @Override
    public URLInfo load(Long aLong) {
        Column<String> result;
        URLInfo value = null;
        try {
            result = keyspace.prepareQuery(cluster.getScheduledUrlColumn())
                    .getKey(aLong)
                    .getColumn(VALUE_COLUMN)
                    .execute().getResult();

            value = this.mapper.readValue(result.getStringValue(), URLInfo.class);

            //TODO If an entry is not a in cassandra an Exception is thrown!!!
        } catch (NotFoundException e) {
            //Do nothing
        } catch (ConnectionException e) {
            //todo proper logging
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public Map<Long, URLInfo> loadAll(Collection<Long> collection) {
        return null;
    }

    @Override
    public Set<Long> loadAllKeys() {
        return null;
    }
}
