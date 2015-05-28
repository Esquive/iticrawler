package com.itiniu.iticrawler.util.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.BaseRobotRules;
import com.itiniu.iticrawler.crawler.rotottxt.crawlercommons.SimpleRobotRules;

import java.io.IOException;

/**
 * Created by ericfalk on 28/05/15.
 */
public class SimpleRobotRulesSerializer implements StreamSerializer<BaseRobotRules> {

    public static final int TYPE_ID = 2;

    private ObjectMapper mapper;

    public SimpleRobotRulesSerializer() {
        mapper = new ObjectMapper(new SmileFactory());
    }

    @Override
    public void write(ObjectDataOutput objectDataOutput, BaseRobotRules baseRobotRules) throws IOException {
        if(mapper != null)
        {
            objectDataOutput.writeUTF(mapper.writeValueAsString(baseRobotRules));
        }
    }

    @Override
    public BaseRobotRules read(ObjectDataInput objectDataInput) throws IOException {
        SimpleRobotRules rules = null;
        if(mapper != null)
        {
            rules = mapper.readValue(objectDataInput.readUTF(),SimpleRobotRules.class);
        }
        return rules;
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void destroy() {
        this.mapper = null;
    }
}
