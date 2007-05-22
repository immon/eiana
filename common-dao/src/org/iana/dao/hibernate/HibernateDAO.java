package org.iana.dao.hibernate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;


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

    public int count(Criterion criteria) {
        HQLBuffer hql = HQLGenerator.from(clazz, criteria);
        return getHibernateTemplate().find(hql.getHQL(), hql.getParams()).size();
    }

    public List<T> find(Criterion criteria, int offset, int limit) {
        HQLBuffer hql = HQLGenerator.from(clazz, criteria);
        List<T> list = getHibernateTemplate().find(hql.getHQL(), hql.getParams());
        List<T> retrieved = new ArrayList<T>();
        if (list.size() > offset) {
            for (ListIterator iterator=list.listIterator(offset); ((iterator.nextIndex() < (offset + limit)) && (iterator.hasNext()));)
                retrieved.add((T) iterator.next());
        }
        
        return retrieved;
    }
}
