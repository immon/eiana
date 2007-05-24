package org.iana.dao.hibernate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.iana.criteria.Criterion;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import java.util.List;
import java.sql.SQLException;


/**
 * The hibernate implementation of CRUD-based data access object with a standard set of find methods.
 *
 * @author Patrycja Wegrzynowicz
 */
@SuppressWarnings("unchecked")
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

    public List<T> find() {
        return find(null);
    }

    public List<T> find(Criterion criteria) {
        HQLBuffer hql = HQLGenerator.from(clazz, criteria);
        return getHibernateTemplate().find(hql.getHQL(), hql.getParams());
    }

    public List<T> find(final Criterion criteria, final int offset, final int limit) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                HQLBuffer hql = HQLGenerator.from(clazz, criteria);
                Query query = session.createQuery(hql.getHQL());

                int idx = 0;
                for (Object param : hql.getParams()) {
                    query.setParameter(idx++, param);
                }

                query.setFirstResult(offset)
                        .setMaxResults(limit)
                        .setFetchSize(limit);
                return query.list();
            }
        });
    }

    public int count(final Criterion criteria) {
        long ret = (Long) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                HQLBuffer hql = HQLGenerator.from(clazz, criteria);
                Query query = session.createQuery("select count(*) " + hql.getHQL());

                int idx = 0;
                for (Object param : hql.getParams()) {
                    query.setParameter(idx++, param);
                }
                return query.uniqueResult();
            }
        });
        return (int) ret;
    }
}
