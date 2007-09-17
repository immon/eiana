package org.iana.config.impl;

import org.iana.config.ConfigDAO;
import org.iana.config.Parameter;
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

    public Parameter getParameter(String owner, String name) {
        String query = "from AbstractParameter where owner = ? and name = ? and ? between fromDate and toDate";
        Object[] queryParams = {owner, name, System.currentTimeMillis()};
        List<Parameter> ret = getHibernateTemplate().find(query, queryParams);
        return (ret != null && !ret.isEmpty()) ? ret.get(0) : null;
    }

    public void addParameter(Parameter parameter) {
        getHibernateTemplate().save(parameter);
    }

    public void removeParameter(Parameter parameter) {
        getHibernateTemplate().delete(parameter);
    }

    public void removeParameter(String owner, String name) {
        removeParameter(getParameter(owner, name));
    }

    public Set<String> getParameterNames(String owner, String name) {
        String query = "from AbstractParameter where owner = ? and name like ? and ? between fromDate and toDate";
        Object[] queryParams = {owner, name + "%", System.currentTimeMillis()};
        List<Parameter> ret = getHibernateTemplate().find(query, queryParams);
        if (ret == null || ret.isEmpty()) return null;
        Set<String> retSet = new HashSet<String>();
        for (Parameter param : ret)
            retSet.add(param.getName());
        return retSet;
    }

    public Set<String> getSubConfigNames(String owner, String name) {
        return getParameterNames(owner, name);
    }
}
