package org.iana.rzm.user.dao;

import org.iana.rzm.user.Role;
import org.iana.dao.hibernate.HibernateDAO;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class HibernateRoleDAO extends HibernateDAO<Role> implements RoleDAO {

    public HibernateRoleDAO() {
        super(Role.class);
    }

    public List<Role> findAll() {
        String query = "from Role";
        return (List<Role>) getHibernateTemplate().find(query);
    }

    public List<Role> findRoles(Criterion criteria) {
        return null;
    }
}
