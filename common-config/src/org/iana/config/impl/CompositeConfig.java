package org.iana.config.impl;

import org.iana.config.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The config composed of a list of other configs. The configs are searched
 * in a list order, the first value found is returned.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */

public class CompositeConfig extends AbstractConfig {

    private List<Config> configs = new ArrayList<Config>();

    public CompositeConfig(List<Config> configs) {
        if (configs == null || configs.isEmpty())
            throw new IllegalArgumentException("configs list cannot be null or empty");
        this.configs = configs;
    }

    public void addConfig(Config config) {
        if (config == null) throw new IllegalArgumentException("config cannot be null");
        configs.add(config);
    }

    public String getParameter(String name) {
        for (Config config : configs) {
            String value = config.getParameter(name);
            if (value != null) return value;
        }
        return null;
    }

    public List<String> getParameterList(String name) {
        for (Config config : configs) {
            List<String> ret = config.getParameterList(name);
            if (ret != null && !ret.isEmpty()) return ret;
        }
        return null;
    }

    public Set<String> getParameterSet(String name) {
        for (Config config : configs) {
            Set<String> ret = config.getParameterSet(name);
            if (ret != null && !ret.isEmpty()) return ret;
        }
        return null;
    }

    public Config getSubConfig(String name) {
        for (Config config : configs) {
            Config conf = config.getSubConfig(name);
            if (conf != null) return conf;
        }
        return null;
    }

    public Set<String> getSubConfigNames() {
        Set<String> ret = new HashSet<String>();
        for (Config config : configs) {
            Set<String> subConfigNames = config.getSubConfigNames();
            if (subConfigNames != null) ret.addAll(subConfigNames);
        }
        return (ret.isEmpty()) ? null : ret;
    }

    public Set<String> getParameterNames() {
        Set<String> ret = new HashSet<String>();
        for (Config config : configs) {
            Set<String> paramNames = config.getParameterNames();
            if (paramNames != null) ret.addAll(paramNames);
        }
        return (ret.isEmpty()) ? null : ret;
    }

    protected Set<String> getParameterNames(String name) {
        throw new UnsupportedOperationException();
    }


    protected Set<String> getSubConfigNames(String name) {
        throw new UnsupportedOperationException();
    }
}
