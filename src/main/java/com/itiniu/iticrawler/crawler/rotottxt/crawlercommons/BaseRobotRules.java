/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itiniu.iticrawler.crawler.rotottxt.crawlercommons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result from parsing a single robots.txt file - which means we get a set of
 * rules, and a crawl-delay.
 */

@SuppressWarnings("serial")
public abstract class BaseRobotRules implements Serializable {

    public static final long UNSET_CRAWL_DELAY = Long.MIN_VALUE;

    public abstract boolean isAllowed(String url);

    public abstract boolean isAllowAll();

    public abstract boolean isAllowNone();

    private long _crawlDelay = UNSET_CRAWL_DELAY;
    private boolean _deferVisits = false;
    private List<String> _sitemaps;

    public BaseRobotRules() {
        _sitemaps = new ArrayList<String>();
    }

    public long getCrawlDelay() {
        return _crawlDelay;
    }

    public void setCrawlDelay(long crawlDelay) {
        _crawlDelay = crawlDelay;
    }

    public boolean isDeferVisits() {
        return _deferVisits;
    }

    public void setDeferVisits(boolean deferVisits) {
        _deferVisits = deferVisits;
    }

    public void addSitemap(String sitemap) {
        _sitemaps.add(sitemap);
    }

    public List<String> getSitemaps() {
        return _sitemaps;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (_crawlDelay ^ (_crawlDelay >>> 32));
        result = prime * result + (_deferVisits ? 1231 : 1237);
        result = prime * result + ((_sitemaps == null) ? 0 : _sitemaps.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseRobotRules other = (BaseRobotRules) obj;
        if (_crawlDelay != other._crawlDelay)
            return false;
        if (_deferVisits != other._deferVisits)
            return false;
        if (_sitemaps == null) {
            if (other._sitemaps != null)
                return false;
        } else if (!_sitemaps.equals(other._sitemaps))
            return false;
        return true;
    }

    public long get_crawlDelay() {
        return _crawlDelay;
    }

    public void set_crawlDelay(long _crawlDelay) {
        this._crawlDelay = _crawlDelay;
    }

    public boolean is_deferVisits() {
        return _deferVisits;
    }

    public void set_deferVisits(boolean _deferVisits) {
        this._deferVisits = _deferVisits;
    }

    public List<String> get_sitemaps() {
        return _sitemaps;
    }

    public void set_sitemaps(List<String> _sitemaps) {
        this._sitemaps = _sitemaps;
    }
}
