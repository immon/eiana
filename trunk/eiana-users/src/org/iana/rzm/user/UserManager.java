package org.iana.rzm.user;

import java.util.List;

/**
 * This interface provides user management functionality.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface UserManager {

    public RZMUser get(long id);

    public RZMUser get(String loginName);

    public void create(RZMUser user);

    public List<RZMUser> findAll();

    public List<RZMUser> find(UserCriteria criteria);
}
