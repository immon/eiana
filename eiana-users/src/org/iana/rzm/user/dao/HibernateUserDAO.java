package org.iana.rzm.user.dao;

import org.iana.criteria.Criterion;
import org.iana.criteria.FieldCriterion;
import org.iana.criteria.In;
import org.iana.criteria.ValuedFieldCriterion;
import org.iana.dao.hibernate.HQLBuffer;
import org.iana.dao.hibernate.HQLGenerator;
import org.iana.dao.hibernate.HibernateDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marcin Zajaczkowski
 * @author Jakub Laszkiewicz
 */
@SuppressWarnings("unchecked")
public class HibernateUserDAO extends HibernateDAO<RZMUser> implements UserDAO {

    public HibernateUserDAO() {
        super(RZMUser.class);
    }

    public RZMUser get(String loginName) {
        System.out.println("name = " + loginName);
        List<RZMUser> list = (List<RZMUser>) getHibernateTemplate().find("from RZMUser u where u.loginName = ?", loginName);
        RZMUser ret = (list.size() < 1) ? null : list.get(0);
        System.out.println("retrieved = " + ((ret == null) ? null : ret.getLoginName()));
        return ret;
    }

    public List<RZMUser> findUsersInSystemRole(String roleName, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept, boolean accessToDomain) {
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
        if (accessToDomain)
            query += "    and role.accessToDomain = true";
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


    protected HQLBuffer createFindSelect(Criterion criteria) {
        return getGenerator().select(
                "select distinct user ",
                "RZMUser as user " +
                "left join user.roles as role ",
                criteria);
    }

    protected HQLBuffer createCountSelect(Criterion criteria) {
        return getGenerator().select(
                "select count(distinct user) ",
                "RZMUser as user " +
                "left join user.roles as role ",
                criteria);
    }

    protected HQLGenerator getGenerator() {
        return new HQLGenerator() {
            protected String getFieldName(FieldCriterion crit) {
                String name = crit.getFieldName();
                if ("role".equals(name)) return "role.class";
                if (name.startsWith("role.")) return name;
                return "user."+name;
            }

            protected Object getValue(ValuedFieldCriterion crit) {
                String name = crit.getFieldName();
                if ("role.type".equals(name)) {
                    return getType(String.valueOf(crit.getValue()));
                }
                return crit.getValue();
            }

            protected Set<? extends Object> getValues(In crit) {
                String name = crit.getFieldName();
                if ("role.type".equals(name)) {
                    Set<Object> ret = new HashSet<Object>();
                    for (Object object : crit.getValues()) {
                        ret.add(getType(String.valueOf(object)));
                    }
                    return ret;
                }
                return crit.getValues();
            }

            private Role.Type getType(String val) {
                try {
                    return AdminRole.AdminType.valueOf(val);
                } catch (IllegalArgumentException e) {
                    return SystemRole.SystemType.valueOf(val);
                }
            }
        };
    }
}
