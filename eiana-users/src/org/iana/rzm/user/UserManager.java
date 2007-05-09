package org.iana.rzm.user;

import org.iana.criteria.Criterion;

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

    public RZMUser getCloned(long id) throws CloneNotSupportedException;

    public RZMUser getCloned(String loginName) throws CloneNotSupportedException;

    public void create(RZMUser user);

    public void delete(RZMUser user);

    public void delete(String loginName);

    public void update(RZMUser user);

    public List<RZMUser> findAll();

    public List<RZMUser> find(Criterion criteria);

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept);

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType);

    public RZMUser findUserByEmail(String email);
}
