package org.iana.config.impl;

import org.iana.config.Config;
import org.iana.config.ParameterManager;

import java.util.List;
import java.util.Set;

/**
 * The implementation of Config interface that has an owner. Owner uniquely identifies
 * a config across all stored configs.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class OwnedConfig extends AbstractConfig {

    /**
     * The name of the owner of this config. Uniquely identifies a config across
     * all stored configs. Cannot be null.
     */

    private String owner;

    private ParameterManager manager;

    public OwnedConfig(ParameterManager manager) {
        this(DEFAULT_OWNER, manager);
    }

    public OwnedConfig(String owner, ParameterManager manager) {
        if (owner == null || owner.trim().length() == 0)
            throw new IllegalArgumentException("owner cannot be null or empty");
        if (manager == null) throw new IllegalArgumentException("parameterManager cannot be null");
        this.owner = owner;
        this.manager = manager;
    }

    public String getParameter(String name) throws ConfigException {
        return manager.getParameter(owner, name);
    }

    public List<String> getParameterList(String name) throws ConfigException {
        return manager.getParameterList(owner, name);
    }

    public Set<String> getParameterSet(String name) throws ConfigException {
        return manager.getParameterSet(owner, name);
    }

    public Config getSubConfig(String name) {
        return new SubConfig(name, this);
    }

    public Set<String> getSubConfigNames() throws ConfigException {
        return manager.getSubConfigNames(owner, "");
    }

    public Set<String> getParameterNames() throws ConfigException {
        return manager.getParameterNames(owner, "");
    }

    protected Set<String> getParameterNames(String name) throws ConfigException {
        return manager.getParameterNames(owner, name);
    }

    protected Set<String> getSubConfigNames(String name) throws ConfigException {
        return manager.getSubConfigNames(owner, name);
    }
}
