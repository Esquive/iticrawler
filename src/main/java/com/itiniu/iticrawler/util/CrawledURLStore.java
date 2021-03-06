package com.itiniu.iticrawler.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;
import com.itiniu.iticrawler.httptools.impl.URLInfo;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by ericfalk on 24/05/15.
 */
public class CrawledURLStore implements MapStore<Integer, URLInfo> {

    private static final String VALUE_COLUMN = "VALUE";

    StorageCluster cluster;
    Keyspace keyspace;
    ObjectMapper mapper;

    public CrawledURLStore(StorageCluster storageCluster) {
        this.cluster = storageCluster;
        this.keyspace = storageCluster.getKeyspace();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void store(Integer integer, URLInfo urlInfo) {
        try {
            String value = mapper.writeValueAsString(urlInfo);
            keyspace.prepareColumnMutation(cluster.getCrawledUrlColumn(), integer, VALUE_COLUMN)
                    .putValue(value, null)
                    .execute();
        } catch (ConnectionException e) {
            //todo proper logging at this stage
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeAll(Map<Integer, URLInfo> map) {
        //do nothing
    }

    @Override
    public void delete(Integer integer) {
        //do nothing
    }

    @Override
    public void deleteAll(Collection<Integer> collection) {
        //do nothing
    }

    @Override
    public URLInfo load(Integer integer) {
        Column<String> result = null;
        URLInfo value = null;
        try {
            result = keyspace.prepareQuery(cluster.getCrawledUrlColumn())
                    .getKey(integer)
                    .getColumn(VALUE_COLUMN)
                    .execute().getResult();

            value = this.mapper.readValue(result.getStringValue(), URLInfo.class);

            //TODO If an entry is not a in cassandra an Exception is thrown!!!
        }catch(NotFoundException e)
        {
            //Do nothing
        }
        catch (ConnectionException e) {
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
    public Map<Integer, URLInfo> loadAll(Collection<Integer> collection) {
        return null;
    }

    @Override
    public Set<Integer> loadAllKeys() {
        return null;
    }
}
