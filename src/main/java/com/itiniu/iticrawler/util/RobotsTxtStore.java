package com.itiniu.iticrawler.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.core.MapStore;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.SimpleRobotRules;
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
 * Created by ericfalk on 29/05/15.
 */
public class RobotsTxtStore implements MapStore<String, BaseRobotRules> {

    private static final String VALUE_COLUMN = "VALUE";

    StorageCluster cluster;
    Keyspace keyspace;
    ObjectMapper mapper;

    public RobotsTxtStore(StorageCluster storageCluster) {
        this.cluster = storageCluster;
        this.keyspace = storageCluster.getKeyspace();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void store(String s, BaseRobotRules baseRobotRules) {
        try {
            String value = mapper.writeValueAsString(baseRobotRules);
            keyspace.prepareColumnMutation(cluster.getRobotsTxtColumn(), s, VALUE_COLUMN)
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
    public void storeAll(Map<String, BaseRobotRules> map) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void deleteAll(Collection<String> collection) {

    }

    @Override
    public BaseRobotRules load(String s) {
        Column<String> result = null;
        BaseRobotRules value = null;
        try {
            result = keyspace.prepareQuery(cluster.getRobotsTxtColumn())
                    .getKey(s)
                    .getColumn(VALUE_COLUMN)
                    .execute().getResult();

            value = this.mapper.readValue(result.getStringValue(), SimpleRobotRules.class);

            //TODO If an entry is not a in cassandra an Exception is thrown!!!
        } catch (NotFoundException e) {//Do nothing
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
    public Map<String, BaseRobotRules> loadAll(Collection<String> collection) {
        return null;
    }

    @Override
    public Set<String> loadAllKeys() {
        return null;
    }
}
