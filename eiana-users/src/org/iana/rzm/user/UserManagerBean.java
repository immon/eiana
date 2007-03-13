package org.iana.rzm.user;

import org.iana.rzm.user.dao.UserDAO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public class UserManagerBean implements UserManager {

    private UserDAO dao;

    public UserManagerBean(UserDAO dao) {
        this.dao = dao;
    }

    public RZMUser get(String name) {
        return dao.get(name);
    }

    public RZMUser get(long id) {
        return dao.get(id);
    }

    public void create(RZMUser user) {
        dao.create(user);
    }

    public List<RZMUser> findAll() {
        return null;
    }

    public List<RZMUser> find(UserCriteria criteria) {
        return null;
    }
}
