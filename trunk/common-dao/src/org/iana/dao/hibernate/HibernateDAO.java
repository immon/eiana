package org.iana.dao.hibernate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.iana.criteria.Criterion;
import org.hibernate.Query;

import javax.swing.*;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HibernateDAO<T> extends HibernateDaoSupport {

    private Class clazz;

    public HibernateDAO(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public T get(final long id) {
        return (T) getHibernateTemplate().get(clazz, id);
    }

    public void create(final T object) {
        getHibernateTemplate().save(object);
    }

    public void update(final T object) {
        getHibernateTemplate().merge(object);
    }

    public void delete(T object) {
        getHibernateTemplate().delete(object);
    }

    public List<T> find(Criterion criteria) {
        HQLBuffer hql = HQLGenerator.from(clazz, criteria);
        return getHibernateTemplate().find(hql.getHQL(), hql.getParams());
    }
}
