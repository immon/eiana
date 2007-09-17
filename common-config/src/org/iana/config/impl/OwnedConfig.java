package org.iana.config.impl;

import org.iana.config.Config;
import org.iana.config.ConfigDAO;

import java.util.HashSet;
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
        return dao.getParameter(owner, name).getParameter();
    }

    public List<String> getParameterList(String name) {
        return dao.getParameter(owner, name).getParameterList();
    }

    public Set<String> getParameterSet(String name) {
        return dao.getParameter(owner, name).getParameterSet();
    }

    public Config getSubConfig(String name) {
        return new SubConfig(name, this);
    }

    public Set<String> getSubConfigNames() {
        return getSubConfigNames("");
    }

    public Set<String> getParameterNames() {
        return getParameterNames("");
    }

    protected Set<String> getParameterNames(String name) {
        Set<String> ret = new HashSet<String>();
        Set<String> names = dao.getParameterNames(owner, name);
        if (names == null || names.isEmpty()) return null;
        for (String n : names) {
            if (!name.contains(".") && (!n.contains("."))) {
                ret.add(n);
            } else {
                int dotIndex = n.lastIndexOf(".");
                if (dotIndex > 0 && n.substring(0, dotIndex).equals(name))
                    ret.add(n.substring(dotIndex + 1));
            }
        }
        return (ret.isEmpty()) ? null : ret;
    }

    protected Set<String> getSubConfigNames(String name) {
        Set<String> ret = new HashSet<String>();
        Set<String> names = dao.getParameterNames(owner, name);
        if (names == null || names.isEmpty()) return null;
        for (String n : names) {
            if ("".equals(name) && (n.contains(".")))
                ret.add(n.substring(0, n.indexOf(".")));
            else if (n.startsWith(name)) {
                String subString = n.substring(name.length() + 1);
                if (subString.contains("."))
                    ret.add(subString.substring(0, subString.indexOf(".")));
            }
        }
        return (ret.isEmpty()) ? null : ret;
    }
}
