package org.iana.config;

import java.util.List;
import java.util.Set;

/**
 * Subconfig of a given subsettedConfig. It represents a subset of the subsettedConfig
 * parameters. All parameters of subsetted prefixed "name." belong to this subconfig.
 *
 * @author Patrycja Wegrzynowicz
 */
public class Subconfig implements Config {

    /**
     * The name of this subconfig. Cannot be null or empty string.
     */
    private String name;

    private Config subsettedConfig;

    public Subconfig(String name, Config config) {
        this.name = name;
        this.subsettedConfig = config;
    }

    public String getParameter(String name) {
        return subsettedConfig.getParameter(name);
    }

    public List<String> getParameterList(String name) {
        return subsettedConfig.getParameterList(name);
    }

    public Set<String> getParameterSet(String name) {
        return subsettedConfig.getParameterSet(name);
    }

    public Config getConfig(String name) {
        return subsettedConfig.getConfig(name);
    }

    public Set<String> getParameterNames() {
        return subsettedConfig.getParameterNames();
    }

    public Boolean getBooleanParameter(String name) {
        return subsettedConfig.getBooleanParameter(name);
    }

    public Integer getIntegerParameter(String name) {
        return subsettedConfig.getIntegerParameter(name);
    }

    public Long getLongParameter(String name) {
        return subsettedConfig.getLongParameter(name);
    }

    public List<Boolean> getBooleanParameterList(String name) {
        return subsettedConfig.getBooleanParameterList(name);
    }

    public List<Integer> getIntegerParameterList(String name) {
        return subsettedConfig.getIntegerParameterList(name);
    }

    public List<Long> getLongParameterList(String name) {
        return subsettedConfig.getLongParameterList(name);
    }

    public Set<Boolean> getBooleanParameterSet(String name) {
        return subsettedConfig.getBooleanParameterSet(name);
    }

    public Set<Integer> getIntegerParameterSet(String name) {
        return subsettedConfig.getIntegerParameterSet(name);
    }

    public Set<Long> getLongParameterSet(String name) {
        return subsettedConfig.getLongParameterSet(name);
    }
}
