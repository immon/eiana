package org.iana.rzm.user;

import java.util.List;

/**
 * This interface provides user management functionality.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface UserManager {

    public RZMUser get(long id);

    public RZMUser get(String loginName);

    public void create(RZMUser user);

    public List<RZMUser> findAll();

    public List<RZMUser> find(UserCriteria criteria);

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept);

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType);
}
