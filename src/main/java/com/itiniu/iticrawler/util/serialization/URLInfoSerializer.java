package com.itiniu.iticrawler.util.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.itiniu.iticrawler.httptools.impl.URLInfo;

import java.io.IOException;

/**
 * Created by ericfalk on 28/05/15.
 */
public class URLInfoSerializer  implements StreamSerializer<URLInfo>
{
    public static final int TYPE_ID = 1;

    private ObjectMapper mapper;

    public URLInfoSerializer()
    {
        this.mapper = new ObjectMapper(new SmileFactory());
    }

    @Override
    public void write(ObjectDataOutput objectDataOutput, URLInfo urlInfo) throws IOException {
        if(this.mapper != null) {
            objectDataOutput.writeUTF(mapper.writeValueAsString(urlInfo));
        }
    }

    @Override
    public URLInfo read(ObjectDataInput objectDataInput) throws IOException {
        URLInfo url = null;
        if(this.mapper != null)
        {
            url = this.mapper.readValue(objectDataInput.readUTF(),URLInfo.class);
        }
        return url;
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
