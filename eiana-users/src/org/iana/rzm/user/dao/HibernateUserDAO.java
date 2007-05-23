package org.iana.rzm.user.dao;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.dao.hibernate.HibernateDAO;

import java.util.List;
import java.util.ArrayList;

/**
 * org.iana.rzm.user.dao.HibernateUserDAO
 *
 * @author Marcin Zajaczkowski
 *         <p/>
 *         todo Generic methods (get, create, delete) need to be implemented in every specific DAO. That could be (probably) simplified.
 *         For example by something like that:
 *         interface DataAccessObject<T> {}
 *         interface UserDAO extends DataAccessObject<User> {}
 *         class HibernateUserDAO extends HibernateDAO<User> implements UserDAO {}
 *         <p/>
 *         class abstract HibernateDAO<T> extends HibernateDaoSupport implements DataAccessObject<T> {}
 */
public class HibernateUserDAO extends HibernateDAO<RZMUser> implements UserDAO {

    public HibernateUserDAO() {
        super(RZMUser.class);
    }

    public RZMUser get(String loginName) {
        System.out.println("name = " + loginName);
        List<RZMUser> list = (List<RZMUser>) getHibernateTemplate().find("from RZMUser u where u.loginName = ?", loginName);
        // todo bug in spring or hibernate
        // todo iterate returns object but all values are set to null
        //Iterator<Domain> it = getHibernateTemplate().iterate("from Domain d where d.name.name = ?", name);
        //Domain ret = (it == null || !it.hasNext()) ? null : it.next();
        RZMUser ret = (list.size() < 1) ? null : list.get(0);
        System.out.println("retrieved = " + ((ret == null) ? null : ret.getLoginName()));
        return ret;
    }

    public List<RZMUser> findUsersInSystemRole(String roleName, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept) {
        List<Object> args = new ArrayList<Object>();
        args.add(roleName);
        String query = "select user " +
                "from " +
                "    RZMUser as user " +
                "    inner join user.roles as role " +
                "where " +
                "    role.class = SystemRole" +
                "    and role.name.name = ? ";
        if (roleType != null) {
            query += "    and role.type = ? ";
            args.add(roleType);
        }
        if (mustAccept)
            query += "    and role.mustAccept = true ";
        if (acceptFrom)
            query += "    and role.acceptFrom = true";
        return (List<RZMUser>) getHibernateTemplate().find(query, args.toArray());
    }

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType) {
        String query = "select user " +
                "from " +
                "    RZMUser as user " +
                "    inner join user.roles as role " +
                "where " +
                "    role.class = AdminRole" +
                "    and role.type = ? ";
        return (List<RZMUser>) getHibernateTemplate().find(query, roleType);
    }

    public RZMUser findUserByEmail(String email) {
        String query = "select user " +
                "from " +
                "    RZMUser as user " +
                "where " +
                "    user.email = ? ";
        //todo: additional conditions, ie. admin role type?
        List result = getHibernateTemplate().find(query, email);
        return (RZMUser) (result.isEmpty() ? null : result.iterator().next());
    }

    public RZMUser findUserByEmailAndRole(String email, String domainName) {
        String query = "select user " +
                "from " +
                "    RZMUser as user " +
                "    inner join user.roles as role " +
                "where " +
                "    user.email = ? " +
                "    and role.class = SystemRole " +
                "    and role.name.name = ? ";
        List result = getHibernateTemplate().find(query, new Object[]{email, domainName});
        return (RZMUser) (result.isEmpty() ? null : result.iterator().next());
    }

    public List<RZMUser> findAll() {
        String query = "from RZMUser";
        return (List<RZMUser>) getHibernateTemplate().find(query);
    }
}
