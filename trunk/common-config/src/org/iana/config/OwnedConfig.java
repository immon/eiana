package org.iana.config;

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

    private ConfigDAO dao;

    public OwnedConfig(String owner, ConfigDAO dao) {
        if (owner == null || owner.trim().length() == 0)
            throw new IllegalArgumentException("owner cannot be null or empty");
        this.owner = owner;
        this.dao = dao;
    }

    public String getParameter(String name) {
        AbstractParameter param = dao.getParameter(owner, name);
        if (param instanceof SingleParameter) {
            SingleParameter singleParam = (SingleParameter) param;
            return singleParam.getValue();
        }
        return null;
    }

    public List<String> getParameterList(String name) {
        AbstractParameter param = dao.getParameter(owner, name);
        if (param instanceof ListParameter) {
            ListParameter listParam = (ListParameter) param;
            return listParam.getValues();
        }
        return null;
    }

    public Set<String> getParameterSet(String name) {
        AbstractParameter param = dao.getParameter(owner, name);
        if (param instanceof SetParameter) {
            SetParameter listParam = (SetParameter) param;
            return listParam.getValues();
        }
        return null;
    }

    public Config getSubConfig(String name) {
        return new SubConfig(name, this);
    }

    public Set<String> getSubConfigNames() {
        return dao.getSubConfigNames(owner);
    }

    public Set<String> getParameterNames() {
        return dao.getParameterNames(owner);
    }
}
