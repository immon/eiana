package org.iana.rzm.user.dao;

import org.iana.rzm.user.User;

/**
 * org.iana.rzm.user.dao.UserDAO
 *
 * @author Marcin Zajaczkowski
 */
public interface UserDAO {

    public User get(long id);

    public void create(User user);

    public void update(User user);

    public void delete(User user);
}
