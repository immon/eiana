package org.iana.config;

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

    public AbstractParameter getParameter(String owner, String name) {
        String query = "from AbstractParameter where owner like ? and name like ? and ? between fromDate and toDate";
        Object[] queryParams = {owner, name, System.currentTimeMillis()};
        List<AbstractParameter> ret = getHibernateTemplate().find(query, queryParams);
        return (ret != null && !ret.isEmpty()) ? ret.get(0) : null;
    }

    public void addParameter(AbstractParameter parameter) {
        getHibernateTemplate().save(parameter);
    }

    public void removeParameter(AbstractParameter parameter) {
        getHibernateTemplate().delete(parameter);
    }

    public void removeParameter(String owner, String name) {
        removeParameter(getParameter(owner, name));
    }


    public Set<String> getParameterNames(String owner) {
        String query = "from AbstractParameter where owner like ? and ? between fromDate and toDate";
        Object[] queryParams = {owner, System.currentTimeMillis()};
        List<AbstractParameter> ret = getHibernateTemplate().find(query, queryParams);
        if (ret == null || ret.isEmpty()) return null;
        Set<String> retSet = new HashSet<String>();
        for (AbstractParameter param : ret)
            retSet.add(param.getName());
        return retSet;
    }

    public Set<String> getSubConfigNames(String owner) {
        String query = "from AbstractParameter where owner like ? and ? between fromDate and toDate";
        Object[] queryParams = {owner, System.currentTimeMillis()};
        List<AbstractParameter> ret = getHibernateTemplate().find(query, queryParams);
        if (ret == null || ret.isEmpty()) return null;
        Set<String> retSet = new HashSet<String>();
        for (AbstractParameter param : ret) {
            String fullName = param.getName();
            if (fullName.contains("."))
                retSet.add(fullName.substring(0, fullName.lastIndexOf(".")));
        }
        return (retSet.isEmpty()) ? null : retSet;
    }
}
