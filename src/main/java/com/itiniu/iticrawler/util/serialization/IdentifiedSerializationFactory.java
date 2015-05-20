package com.itiniu.iticrawler.util.serialization;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.itiniu.iticrawler.crawler.rotottxt.impl.DefaultRobotTxtDirective;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

/**
 * Created by ericfalk on 21/05/15.
 */
public class IdentifiedSerializationFactory implements DataSerializableFactory{


    public static final int FACTORY_ID = 1;
    public static final int URLINFO_TYPE = 1;
    public static final int ROBOT_DIRECTIVE_TYPE = 2;


    @Override
    public IdentifiedDataSerializable create(int typeId) {

        switch (typeId)
        {
            case URLINFO_TYPE:
                return new URLInfo();
            case ROBOT_DIRECTIVE_TYPE:
                return new DefaultRobotTxtDirective();

            default:
                return null;
        }
    }


}
