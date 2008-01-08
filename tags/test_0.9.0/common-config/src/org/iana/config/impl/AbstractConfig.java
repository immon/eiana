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

    public Boolean getBooleanParameter(String name) throws ConfigException {
        String value = getParameter(name);
        return (value == null) ? null : Boolean.parseBoolean(value);
    }

    public Integer getIntegerParameter(String name) throws ConfigException {
        String value = getParameter(name);
        return (value == null) ? null : Integer.parseInt(value);
    }

    public Long getLongParameter(String name) throws ConfigException {
        String value = getParameter(name);
        return (value == null) ? null : Long.parseLong(value);
    }

    public List<Boolean> getBooleanParameterList(String name) throws ConfigException {
        List<Boolean> ret = new ArrayList<Boolean>();
        List<String> params = getParameterList(name);
        if (params == null || params.isEmpty()) return null;
        for (String value : params)
            if (value != null) ret.add(Boolean.parseBoolean(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public List<Integer> getIntegerParameterList(String name) throws ConfigException {
        List<Integer> ret = new ArrayList<Integer>();
        List<String> params = getParameterList(name);
        if (params == null || params.isEmpty()) return null;
        for (String value : params)
            if (value != null) ret.add(Integer.parseInt(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public List<Long> getLongParameterList(String name) throws ConfigException {
        List<Long> ret = new ArrayList<Long>();
        List<String> params = getParameterList(name);
        if (params == null || params.isEmpty()) return null;
        for (String value : params)
            if (value != null) ret.add(Long.parseLong(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public Set<Boolean> getBooleanParameterSet(String name) throws ConfigException {
        Set<Boolean> ret = new HashSet<Boolean>();
        Set<String> params = getParameterSet(name);
        if (params == null || params.isEmpty()) return null;
        for (String value : params)
            if (value != null) ret.add(Boolean.parseBoolean(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public Set<Integer> getIntegerParameterSet(String name) throws ConfigException {
        Set<Integer> ret = new HashSet<Integer>();
        Set<String> params = getParameterSet(name);
        if (params == null || params.isEmpty()) return null;
        for (String value : params)
            if (value != null) ret.add(Integer.parseInt(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public Set<Long> getLongParameterSet(String name) throws ConfigException {
        Set<Long> ret = new HashSet<Long>();
        Set<String> params = getParameterSet(name);
        if (params == null || params.isEmpty()) return null;
        for (String value : params)
            if (value != null) ret.add(Long.parseLong(value));

        return (ret.isEmpty()) ? null : ret;
    }

    public String getParameter(String name, String defaultValue) throws ConfigException {
        String ret = getParameter(name);
        return ret == null ? defaultValue : ret;
    }

    public List<String> getParameterList(String name, List<String> defaultValue) throws ConfigException {
        List<String> ret = getParameterList(name);
        return ret == null ? defaultValue : ret;
    }

    public Set<String> getParameterSet(String name, Set<String> defaultValue) throws ConfigException {
        Set<String> ret = getParameterSet(name);
        return ret == null ? defaultValue : ret;
    }

    public Boolean getBooleanParameter(String name, Boolean defaultValue) throws ConfigException {
        Boolean ret = getBooleanParameter(name);
        return ret == null ? defaultValue : ret;
    }

    public Long getLongParameter(String name, Long defaultValue) throws ConfigException {
        Long ret = getLongParameter(name);
        return ret == null ? defaultValue : ret;
    }

    public Integer getIntegerParameter(String name, Integer defaultValue) throws ConfigException {
        Integer ret = getIntegerParameter(name);
        return ret == null ? defaultValue : ret;
    }

    public List<Boolean> getBooleanParameterList(String name, List<Boolean> defaultValue) throws ConfigException {
        List<Boolean> ret = getBooleanParameterList(name);
        return ret == null ? defaultValue : ret;
    }

    public List<Integer> getIntegerParameterList(String name, List<Integer> defaultValue) throws ConfigException {
        List<Integer> ret = getIntegerParameterList(name);
        return ret == null ? defaultValue : ret;
    }

    public List<Long> getLongParameterList(String name, List<Long> defaultValue) throws ConfigException {
        List<Long> ret = getLongParameterList(name);
        return ret == null ? defaultValue : ret;
    }

    public Set<Boolean> getBooleanParameterSet(String name, Set<Boolean> defaultValue) throws ConfigException {
        Set<Boolean> ret = getBooleanParameterSet(name);
        return ret == null ? defaultValue : ret;
    }

    public Set<Integer> getIntegerParameterSet(String name, Set<Integer> defaultValue) throws ConfigException {
        Set<Integer> ret = getIntegerParameterSet(name);
        return ret == null ? defaultValue : ret;
    }

    public Set<Long> getLongParameterSet(String name, Set<Long> defaultValue) throws ConfigException {
        Set<Long> ret = getLongParameterSet(name);
        return ret == null ? defaultValue : ret;
    }

    protected abstract Set<String> getParameterNames(String name) throws ConfigException;

    protected abstract Set<String> getSubConfigNames(String name) throws ConfigException;
}
