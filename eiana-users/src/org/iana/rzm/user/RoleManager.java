package org.iana.rzm.user;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public interface RoleManager {

    public Role getRole(long id);

    public void createRole(Role role);

    public void updateRole(Role role);

    public void deleteRole(Role role);

    public List<Role> findRoles();

    public List<Role> findRoles(RoleCriteria criteria);

}
