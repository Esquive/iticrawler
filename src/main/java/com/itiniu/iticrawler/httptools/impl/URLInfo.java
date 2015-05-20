package com.itiniu.iticrawler.httptools.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.itiniu.iticrawler.util.serialization.IdentifiedSerializationFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class containing the information gained out of the crawling process.
 *
 * @author Eric Falk <erfalk at gmail dot com>
 */
public class URLInfo implements IdentifiedDataSerializable {


    protected URL url = null;
    protected String fullLink = null;
    protected String title = null;
    protected String text = null;
    protected String rel = null;
    protected String redirectedFrom = null;
    protected int urlDepth;
    protected URLInfo parentURLInfo = null;
    protected boolean isImage;
    protected boolean isAnchor;

    public URLInfo() {

    }

    public URLInfo(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public String toString() {
        return this.url.toString();
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    public String getDomain() {
        return this.url.getHost();
    }

    public int getUrlDepth() {
        return this.urlDepth;
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public void setUrlDepth(int urlDepth) {
        this.urlDepth = urlDepth;
    }

    public URLInfo getParentURLInfo() {
        return parentURLInfo;
    }

    public void setParentURLInfo(URLInfo parentURLInfo) {
        this.parentURLInfo = parentURLInfo;
    }

    public String getProtocol() {
        return this.url.getProtocol();
    }

    public int getPort() {
        return this.url.getPort();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean isImage) {
        this.isImage = isImage;
    }

    public boolean isAnchor() {
        return isAnchor;
    }

    public void setAnchor(boolean isAnchor) {
        this.isAnchor = isImage;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getFullLink() {
        return this.fullLink;
    }

    public String getRedirectedFrom() {
        return redirectedFrom;
    }

    public void setRedirectedFrom(String redirectedFrom) {
        this.redirectedFrom = redirectedFrom;
    }

    public String getPath() {
        return this.url.getPath();
    }

    private URLInfo(Builder builder) throws MalformedURLException {
        this.url = new URL(builder.urlString);
        this.fullLink = builder.fullLink;
        this.title = builder.title;
        this.text = builder.text;
        this.rel = builder.rel;
        this.redirectedFrom = builder.redirectedFrom;
        this.urlDepth = builder.urlDepth;
        this.parentURLInfo = builder.parentURL;
        this.isImage = builder.isImage;
        this.isAnchor = builder.isAnchor;
    }

    @Override
    public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
        objectDataOutput.writeUTF(this.url.toString());
        objectDataOutput.writeUTF(this.fullLink);
        objectDataOutput.writeUTF(this.title);
        objectDataOutput.writeUTF(this.text);
        objectDataOutput.writeUTF(this.rel);
        objectDataOutput.writeUTF(this.redirectedFrom);
        objectDataOutput.writeInt(this.urlDepth);
        objectDataOutput.writeBoolean(this.isImage);
        objectDataOutput.writeBoolean(this.isAnchor);
        this.parentURLInfo.writeData(objectDataOutput);
    }

    @Override
    public void readData(ObjectDataInput objectDataInput) throws IOException {
        this.url = new URL(objectDataInput.readUTF());
        this.fullLink = objectDataInput.readUTF();
        this.title = objectDataInput.readUTF();
        this.text = objectDataInput.readUTF();
        this.rel = objectDataInput.readUTF();
        this.redirectedFrom = objectDataInput.readUTF();
        this.urlDepth = objectDataInput.readInt();
        this.isImage = objectDataInput.readBoolean();
        this.isAnchor = objectDataInput.readBoolean();
        this.parentURLInfo = new URLInfo();
        this.parentURLInfo.readData(objectDataInput);
        //TODO: break the parent chain
    }

    @Override
    public int getFactoryId() {
        return IdentifiedSerializationFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return IdentifiedSerializationFactory.URLINFO_TYPE;
    }



    public static class Builder {
        private String urlString;
        private String fullLink;
        private String title;
        private String text;
        private String rel;
        private String redirectedFrom;
        private int urlDepth;
        private URLInfo parentURL;
        private boolean isImage;
        private boolean isAnchor;


        public Builder fullLink(String fullLink) {
            this.fullLink = fullLink;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder rel(String rel) {
            this.rel = rel;
            return this;
        }

        public Builder redirectedFrom(String redirectedFrom) {
            this.redirectedFrom = redirectedFrom;
            return this;
        }

        public Builder urlDepth(int urlDepth) {
            this.urlDepth = urlDepth;
            return this;
        }

        public Builder parentURL(URLInfo parentURL) {
            this.parentURL = parentURL;
            return this;
        }

        public Builder isImage(boolean isImage) {
            this.isImage = isImage;
            return this;
        }

        public Builder isAnchor(boolean isAnchor) {
            this.isAnchor = isAnchor;
            return this;
        }


        public Builder urlString(String urlString) {
            this.urlString = urlString;
            return this;
        }

        public URLInfo build() throws MalformedURLException {
            return new URLInfo(this);
        }
    }
}
