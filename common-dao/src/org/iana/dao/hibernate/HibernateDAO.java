package org.iana.dao.hibernate;

import org.iana.dao.*;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * This class is a hibernate-based implementation of <code>DataAccessObject</code> interface.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 *
 * @see DataAccessObject
 */
public class HibernateDAO<T> implements DataAccessObject<T> {

    private static SessionFactory sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();

    private Class<T> clazz;
    final private Map<String, Query<T>> queries = new HashMap<String, Query<T>>();
    final private Map<String, Update<T>> updates = new HashMap<String, Update<T>>();

    public HibernateDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T get(long id) throws DataAccessFailure {
        try {
            return (T) currentSession().get(clazz, id);
        } catch (HibernateException e) {
            throw new DataAccessFailure(e);
        }
    }

    public T load(long id) throws DataAccessFailure, NoSuchObjectException {
        T ret = get(id);
        if (ret == null) throw new NoSuchObjectException(clazz, id);
        return ret;
    }

    public void persist(T object) throws DataAccessFailure {
        currentSession().saveOrUpdate(object);
    }

    public void remove(T object) throws DataAccessFailure {
        currentSession().delete(object);
    }

    public Query<T> query(String name) throws NoSuchStatementException {
        if (!queries.containsKey(name)) throw new NoSuchStatementException(name);
        return queries.get(name).clone();
    }

    public Update<T> update(String name) throws NoSuchStatementException {
        if (!updates.containsKey(name)) throw new NoSuchStatementException(name);
        return updates.get(name).clone();
    }

    static protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}
