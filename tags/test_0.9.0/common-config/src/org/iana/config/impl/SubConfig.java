package org.iana.config.impl;

import org.iana.config.Config;

import java.util.List;
import java.util.Set;

/**
 * Subconfig of a given subsettedConfig. It represents a subset of the subsettedConfig
 * parameters. All parameters of subsetted prefixed "name." belong to this subconfig.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */

class SubConfig extends AbstractConfig {

    /**
     * The name of this subconfig. Cannot be null or empty string.
     */
    private String subName;

    private AbstractConfig subsettedConfig;

    SubConfig(String subName, AbstractConfig config) {
        if (subName == null || subName.trim().length() == 0)
            throw new IllegalArgumentException("subconfig name cannot be null or empty");
        if (subName.lastIndexOf(".") == subName.length() - 1)
            throw new IllegalArgumentException("subconfig name cannot ends with \".\"");
        this.subName = subName;
        this.subsettedConfig = config;
    }

    public String getParameter(String name) throws ConfigException {
        return subsettedConfig.getParameter(updateSubName(subName, name));
    }

    public List<String> getParameterList(String name) throws ConfigException {
        return subsettedConfig.getParameterList(updateSubName(subName, name));
    }

    public Set<String> getParameterSet(String name) throws ConfigException {
        return subsettedConfig.getParameterSet(updateSubName(subName, name));
    }

    public Config getSubConfig(String name) throws ConfigException {
        return subsettedConfig.getSubConfig(updateSubName(subName, name));
    }

    public Set<String> getSubConfigNames() throws ConfigException {
        return subsettedConfig.getSubConfigNames(subName);
    }

    protected Set<String> getSubConfigNames(String name) throws ConfigException {
        return subsettedConfig.getSubConfigNames(name);
    }

    public Set<String> getParameterNames() throws ConfigException {
        return subsettedConfig.getParameterNames(subName);
    }

    protected Set<String> getParameterNames(String name) throws ConfigException {
        return subsettedConfig.getParameterNames(name);
    }

    public Boolean getBooleanParameter(String name) throws ConfigException {
        return subsettedConfig.getBooleanParameter(updateSubName(subName, name));
    }

    public Integer getIntegerParameter(String name) throws ConfigException {
        return subsettedConfig.getIntegerParameter(updateSubName(subName, name));
    }

    public Long getLongParameter(String name) throws ConfigException {
        return subsettedConfig.getLongParameter(updateSubName(subName, name));
    }

    public List<Boolean> getBooleanParameterList(String name) throws ConfigException {
        return subsettedConfig.getBooleanParameterList(updateSubName(subName, name));
    }

    public List<Integer> getIntegerParameterList(String name) throws ConfigException {
        return subsettedConfig.getIntegerParameterList(updateSubName(subName, name));
    }

    public List<Long> getLongParameterList(String name) throws ConfigException {
        return subsettedConfig.getLongParameterList(updateSubName(subName, name));
    }

    public Set<Boolean> getBooleanParameterSet(String name) throws ConfigException {
        return subsettedConfig.getBooleanParameterSet(updateSubName(subName, name));
    }

    public Set<Integer> getIntegerParameterSet(String name) throws ConfigException {
        return subsettedConfig.getIntegerParameterSet(updateSubName(subName, name));
    }

    public Set<Long> getLongParameterSet(String name) throws ConfigException {
        return subsettedConfig.getLongParameterSet(updateSubName(subName, name));
    }

    private String updateSubName(String subName, String paramName) {
        StringBuffer buff = new StringBuffer();
        buff.append(subName).append(".");
        buff.append(paramName);
        return buff.toString();
    }
}
