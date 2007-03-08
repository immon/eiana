package org.iana.rzm.user.dao;

import org.iana.rzm.user.RZMUser;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * org.iana.rzm.user.dao.HibernateUserDAO
 *
 * @author Marcin Zajaczkowski
 *
 * todo Generic methods (get, create, delete) need to be implemented in every specific DAO. That could be (probably) simplified.
 * For example by something like that:
 * interface DataAccessObject<T> {}
 * interface UserDAO extends DataAccessObject<User> {}
 * class HibernateUserDAO extends HibernateDAO<User> implements UserDAO {}
 *
 * class abstract HibernateDAO<T> extends HibernateDaoSupport implements DataAccessObject<T> {}
 */
public class HibernateUserDAO extends HibernateDaoSupport implements UserDAO {

    public RZMUser get(long id) {
        return (RZMUser) getHibernateTemplate().get(RZMUser.class, id);
    }

    public void create(RZMUser user) {
        getHibernateTemplate().save(user);
    }

    public void update(RZMUser user) {
        getHibernateTemplate().update(user);
    }

    public void delete(RZMUser user) {
        getHibernateTemplate().delete(user);
    }
}
