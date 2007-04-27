package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.dao.RoleDAO;

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

    public Role getRole(long id) {
        return roleDAO.getRole(id);
    }

    public void createRole(Role role) {
        roleDAO.createRole(role);
    }

    public void updateRole(Role role) {
        roleDAO.updateRole(role);
    }

    public void deleteRole(Role role) {
        roleDAO.deleteRole(role);
    }

    public List<Role> findRoles() {
        return roleDAO.findRoles();
    }

    public List<Role> findRoles(RoleCriteria criteria) {
        return roleDAO.findRoles(criteria);
    }
}
