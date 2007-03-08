package org.iana.rzm.user.dao;

import org.iana.rzm.user.RZMUser;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

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

    public RZMUser get(String loginName) {
           System.out.println("name = " + loginName);
           List<RZMUser> list = getHibernateTemplate().find("from RZMUser u where u.loginName = ?", loginName);
           // todo bug in spring or hibernate
           // todo iterate returns object but all values are set to null
           //Iterator<Domain> it = getHibernateTemplate().iterate("from Domain d where d.name.name = ?", name);
           //Domain ret = (it == null || !it.hasNext()) ? null : it.next();
           RZMUser ret = (list.size() < 1) ? null : list.get(0);
           System.out.println("retrieved = " + ((ret == null) ? null : ret.getLoginName()));
           return ret;
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
