package org.iana.config.impl;

import org.iana.config.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides a template implementation of methods for fetching Boolean, Integer or Long values.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
abstract class AbstractConfig implements Config {

    public Boolean getBooleanParameter(String name) {
        String value = getParameter(name);
        return (value == null) ? null : Boolean.parseBoolean(value);
    }

    public Integer getIntegerParameter(String name) {
        String value = getParameter(name);
        return (value == null) ? null : Integer.parseInt(value);
    }

    public Long getLongParameter(String name) {
        String value = getParameter(name);
        return (value == null) ? null : Long.parseLong(value);
    }

    public List<Boolean> getBooleanParameterList(String name) {
        List<Boolean> ret = new ArrayList<Boolean>();
        for (String value : getParameterList(name))
            if (value != null) ret.add(Boolean.parseBoolean(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public List<Integer> getIntegerParameterList(String name) {
        List<Integer> ret = new ArrayList<Integer>();
        for (String value : getParameterList(name))
            if (value != null) ret.add(Integer.parseInt(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public List<Long> getLongParameterList(String name) {
        List<Long> ret = new ArrayList<Long>();
        for (String value : getParameterList(name))
            if (value != null) ret.add(Long.parseLong(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public Set<Boolean> getBooleanParameterSet(String name) {
        Set<Boolean> ret = new HashSet<Boolean>();
        for (String value : getParameterSet(name))
            if (value != null) ret.add(Boolean.parseBoolean(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public Set<Integer> getIntegerParameterSet(String name) {
        Set<Integer> ret = new HashSet<Integer>();
        for (String value : getParameterSet(name))
            if (value != null) ret.add(Integer.parseInt(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public Set<Long> getLongParameterSet(String name) {
        Set<Long> ret = new HashSet<Long>();
        for (String value : getParameterSet(name))
            if (value != null) ret.add(Long.parseLong(value));

        return (ret.isEmpty()) ? null : ret;
    }

    protected abstract Set<String> getParameterNames(String name);

    protected abstract Set<String> getSubConfigNames(String name);
}
