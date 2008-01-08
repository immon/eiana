package org.iana.config.impl;

import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
import org.iana.config.ParameterManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ParameterManagerImpl implements ParameterManager {

    ConfigDAO dao;

    public ParameterManagerImpl(ConfigDAO dao) {
        if (dao == null) throw new IllegalArgumentException("config DAO cannot be null");
        this.dao = dao;
    }

    public String getParameter(String owner, String name) throws ConfigException {
        Parameter param = dao.getParameter(owner, name);
        return (param == null) ? null : param.getParameter();
    }

    public List<String> getParameterList(String owner, String name) throws ConfigException {
        Parameter param = dao.getParameter(owner, name);
        //lazy clone
        return (param == null) ? null : new ArrayList<String>(param.getParameterList());
    }

    public Set<String> getParameterSet(String owner, String name) throws ConfigException {
        Parameter param = dao.getParameter(owner, name);
        //lazy clone
        return (param == null) ? null : new HashSet<String>(param.getParameterSet());
    }

    public Set<String> getParameterNames(String owner, String name) throws ConfigException {
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

    public Set<String> getSubConfigNames(String owner, String name) throws ConfigException {
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
