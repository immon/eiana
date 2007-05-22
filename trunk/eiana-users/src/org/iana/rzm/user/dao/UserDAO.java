package org.iana.rzm.user.dao;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * org.iana.rzm.user.dao.UserDAO
 *
 * @author Marcin Zajaczkowski
 *
 * Q: what with exceptions? DataAccessException is a runtime exception, but maybe could be caught somewhere
 */
public interface UserDAO {

    public RZMUser get(long id);

    public RZMUser get(String loginName);

    public void create(RZMUser user);

    public void update(RZMUser user);

    public void delete(RZMUser user);

    public List<RZMUser> findUsersInSystemRole(String roleName, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept);

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType);

    public RZMUser findUserByEmail(String email);

    public RZMUser findUserByEmailAndRole(String email, String domainName);

    public List<RZMUser> findAll();

    public List<RZMUser> find(Criterion criteria);

    public int count(Criterion criteria);
    public List<RZMUser> find(Criterion criteria, int offset, int limit);
}
