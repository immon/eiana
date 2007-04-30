package org.iana.rzm.user;

import org.iana.notifications.dao.NotificationDAO;
import org.iana.rzm.user.dao.UserDAO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */

public class UserManagerBean implements UserManager {

    private UserDAO dao;
    private NotificationDAO notificationDAO;

    public UserManagerBean(UserDAO dao) {
        this.dao = dao;
    }

    public UserManagerBean(UserDAO dao, NotificationDAO notificationDAO) {
        this.dao = dao;
        this.notificationDAO = notificationDAO;
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

    public void update(RZMUser user) {
        dao.update(user);
    }

    public void delete(RZMUser user) {
        if (notificationDAO != null) {
            notificationDAO.deleteUserNotifications(user);
            RZMUser tempUser = dao.get(user.getObjId());
            dao.delete(tempUser);
        } else {
            dao.delete(user);
        }
    }

    public void delete(String loginName) {
        delete(get(loginName));
    }

    public List<RZMUser> findAll() {
        return dao.findAll();
    }

    public List<RZMUser> find(UserCriteria criteria) {
        return null;
    }

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept) {
        return dao.findUsersInSystemRole(name, roleType, acceptFrom, mustAccept);
    }

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType) {
        return dao.findUsersInAdminRole(roleType);
    }

    public RZMUser findUserByEmail(String email) {
        return dao.findUserByEmail(email);
    }
}
