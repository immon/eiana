package org.iana.rzm.user.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.RoleCriteria;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class HibernateRoleDAO extends HibernateDaoSupport implements RoleDAO {

    public Role getRole(long id) {
        return (Role) getHibernateTemplate().get(Role.class, id);
    }

    public void createRole(Role role) {
        getHibernateTemplate().save(role);
    }

    public void updateRole(Role role) {
        getHibernateTemplate().update(role);
    }

    public void deleteRole(Role role) {
        getHibernateTemplate().delete(role);
    }

    public List<Role> findRoles() {
        String query = "from Role";
        return (List<Role>) getHibernateTemplate().find(query);
    }

    public List<Role> findRoles(RoleCriteria criteria) {
        return null;
    }
}
