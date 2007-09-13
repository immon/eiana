package org.iana.config;

import java.util.List;
import java.util.Set;

/**
 * The implementation of Config interface that has an owner. Owner uniquely identifies
 * a config across all stored configs. 
 *
 * @author Patrycja Wegrzynowicz
 */
public class OwnedConfig extends AbstractConfig {

    /**
     * The name of the owner of this config. Uniquely identifies a config across
     * all stored configs. Cannot be null. 
     */
    private String owner;

    private ConfigDAO dao;

    public OwnedConfig(String owner, ConfigDAO dao) {
        this.owner = owner;
        this.dao = dao;
    }

    public String getParameter(String name) {
        return null;
    }

    public List<String> getParameterList(String name) {
        return null;
    }

    public Set<String> getParameterSet(String name) {
        return null;
    }

    public Config getConfig(String name) {
        return null;
    }

    public Set<String> getParameterNames() {
        return null;
    }
}
