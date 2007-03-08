package org.iana.rzm.user.dao;

import org.iana.rzm.user.RZMUser;

/**
 * org.iana.rzm.user.dao.UserDAO
 *
 * @author Marcin Zajaczkowski
 */
public interface UserDAO {

    public RZMUser get(long id);

    public void create(RZMUser user);

    public void update(RZMUser user);

    public void delete(RZMUser user);
}
