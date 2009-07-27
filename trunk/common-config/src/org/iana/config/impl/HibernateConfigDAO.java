package org.iana.config.impl;

import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
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
            String query = "from AbstractParameter ap where ap.owner = ? and ap.name = ? and " +
                    "(( ? between ap.fromDate and ap.toDate ) or ( ap.fromDate <= ? and ap.toDate is null ))";
            long time = System.currentTimeMillis();
            Object[] queryParams = {owner, name, time, time};
            List<Parameter> ret = getHibernateTemplate().find(query, queryParams);
            return (ret != null && !ret.isEmpty()) ? ret.get(0) : null;
        } catch (DataAccessException e) {
            throw new ConfigException(e);
        }
    }

    public List<Parameter> getParameters() throws ConfigException {
        try {
            String query = "from AbstractParameter ap where " +
                    " (( ? between ap.fromDate and ap.toDate ) or ( ap.fromDate <= ? and ap.toDate is null ))" +
                    " order by ap.name";
            long time = System.currentTimeMillis();
            Object[] queryParams = {time, time};
            List<Parameter> ret = getHibernateTemplate().find(query, queryParams);
            return ret == null ? new ArrayList<Parameter>() : ret;
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


    public void updateParameter(Parameter parameter) throws ConfigException {
    try {
            getHibernateTemplate().update(parameter);
        } catch (DataAccessException e) {
            throw new ConfigException(e);
        }
    }

    public Set<String> getParameterNames(String owner, String name) throws ConfigException {
        try {
            String query = "from AbstractParameter ap where ap.owner = ? and ap.name like ? and " +
                    "(( ? between ap.fromDate and ap.toDate ) or ( ap.fromDate <= ? and ap.toDate is null ))";
            long time = System.currentTimeMillis();
            Object[] queryParams = {owner, name + "%", time, time};
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
