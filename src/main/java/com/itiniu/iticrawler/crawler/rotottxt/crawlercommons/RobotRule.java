package com.itiniu.iticrawler.crawler.rotottxt.crawlercommons;

import java.io.Serializable;

/**
 * Single rule that maps from a path prefix to an allow flag.
 */
public class RobotRule implements Comparable<RobotRule>, Serializable {
    String _prefix;
    boolean _allow;

    public RobotRule()
    {}

    public RobotRule(String prefix, boolean allow) {
        _prefix = prefix;
        _allow = allow;
    }

    // Sort from longest to shortest rules.
    @Override
    public int compareTo(RobotRule o) {
        if (_prefix.length() < o._prefix.length()) {
            return 1;
        } else if (_prefix.length() > o._prefix.length()) {
            return -1;
        } else if (_allow == o._allow) {
            return 0;
        } else if (_allow) {
            // Allow comes before disallow
            return -1;
        } else {
            return 1;
        }
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
        result = prime * result + (_allow ? 1231 : 1237);
        result = prime * result + ((_prefix == null) ? 0 : _prefix.hashCode());
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
        RobotRule other = (RobotRule) obj;
        if (_allow != other._allow)
            return false;
        if (_prefix == null) {
            if (other._prefix != null)
                return false;
        } else if (!_prefix.equals(other._prefix))
            return false;
        return true;
    }

    public String get_prefix() {
        return _prefix;
    }

    public void set_prefix(String _prefix) {
        this._prefix = _prefix;
    }

    public boolean is_allow() {
        return _allow;
    }

    public void set_allow(boolean _allow) {
        this._allow = _allow;
    }
}
