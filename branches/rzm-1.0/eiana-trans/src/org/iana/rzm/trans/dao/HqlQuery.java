package org.iana.rzm.trans.dao;

import java.util.Map;

/**
 * @author Jakub Laszkiewicz
 */
class HqlQuery {
    private String hql;
    private Map<String, Object> parameters;

    public HqlQuery(String hql, Map<String, Object> parameters) {
        this.hql = hql;
        this.parameters = parameters;
    }

    public String getHql() {
        return hql;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
