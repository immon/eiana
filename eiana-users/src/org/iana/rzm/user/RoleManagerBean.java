package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.dao.RoleDAO;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class RoleManagerBean implements RoleManager {

    RoleDAO roleDAO;

    public RoleManagerBean(RoleDAO roleDAO) {
        CheckTool.checkNull(roleDAO, "roleDAO");
        this.roleDAO = roleDAO;
    }

    public Role get(long id) {
        return roleDAO.get(id);
    }

    public void create(Role role) {
        roleDAO.create(role);
    }

    public void update(Role role) {
        roleDAO.update(role);
    }

    public void delete(Role role) {
        roleDAO.delete(role);
    }

    public List<Role> findAll() {
        return roleDAO.findAll();
    }

    public List<Role> find(Criterion criteria) {
        return roleDAO.find(criteria);
    }
}
