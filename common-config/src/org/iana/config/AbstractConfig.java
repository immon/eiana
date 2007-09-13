package org.iana.config;

import java.util.List;
import java.util.Set;

/**
 * Provides a template implementation of methods for fetching Boolean, Integer or Long values.
 *
 * @author Patrycja Wegrzynowicz
 */
public abstract class AbstractConfig implements Config {

    public Boolean getBooleanParameter(String name) {
        return null;
    }

    public Integer getIntegerParameter(String name) {
        return null;
    }

    public Long getLongParameter(String name) {
        return null;
    }

    public List<Boolean> getBooleanParameterList(String name) {
        return null;
    }

    public List<Integer> getIntegerParameterList(String name) {
        return null;
    }

    public List<Long> getLongParameterList(String name) {
        return null;
    }

    public Set<Boolean> getBooleanParameterSet(String name) {
        return null;
    }

    public Set<Integer> getIntegerParameterSet(String name) {
        return null;
    }

    public Set<Long> getLongParameterSet(String name) {
        return null;
    }
}
