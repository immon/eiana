package org.iana.rzm.user.dao;

import org.iana.rzm.user.RoleCriteria;
import org.iana.rzm.user.Role;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public interface RoleDAO {

    public Role getRole(long id);

    public void createRole(Role role);

    public void updateRole(Role role);

    public void deleteRole(Role role);

    public List<Role> findRoles();

    public List<Role> findRoles(RoleCriteria criteria);

}
