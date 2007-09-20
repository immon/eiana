package org.iana.config.impl;

import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
@SuppressWarnings("unchecked")
public class HibernateConfigDAO extends HibernateDaoSupport implements ConfigDAO {

    public Parameter getParameter(String owner, String name) throws ConfigException {
        try {
            String query = "from AbstractParameter where owner = ? and name = ? and ? between fromDate and toDate";
            Object[] queryParams = {owner, name, System.currentTimeMillis()};
            List<Parameter> ret = getHibernateTemplate().find(query, queryParams);
            return (ret != null && !ret.isEmpty()) ? ret.get(0) : null;
        } catch (DataAccessException e) {
            throw new ConfigException(e);
        }
    }

    public void addParameter(Parameter parameter) throws ConfigException {
        try {
            getHibernateTemplate().save(parameter);
        } catch (DataAccessException e) {
            throw new ConfigException(e);
        }
    }

    public void removeParameter(Parameter parameter) throws ConfigException {
        try {
            getHibernateTemplate().delete(parameter);
        } catch (DataAccessException e) {
            throw new ConfigException(e);
        }
    }

    public void removeParameter(String owner, String name) throws ConfigException {
        removeParameter(getParameter(owner, name));
    }

    public Set<String> getParameterNames(String owner, String name) throws ConfigException {
        try {
            String query = "from AbstractParameter where owner = ? and name like ? and ? between fromDate and toDate";
            Object[] queryParams = {owner, name + "%", System.currentTimeMillis()};
            List<Parameter> ret = getHibernateTemplate().find(query, queryParams);
            if (ret == null || ret.isEmpty()) return null;
            Set<String> retSet = new HashSet<String>();
            for (Parameter param : ret)
                retSet.add(param.getName());
            return retSet;
        } catch (DataAccessException e) {
            throw new ConfigException(e);
        }
    }

    public Set<String> getSubConfigNames(String owner, String name) throws ConfigException {
        return getParameterNames(owner, name);
    }
}
