package org.iana.rzm.user.dao;

import org.iana.rzm.user.RZMUser;

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
}
