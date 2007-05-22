package org.iana.rzm.user;

import org.iana.notifications.NotificationManager;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */

public class UserManagerBean implements UserManager {

    private UserDAO dao;
    private NotificationManager notificationManager;

    public UserManagerBean(UserDAO dao) {
        this.dao = dao;
    }

    public UserManagerBean(UserDAO dao, NotificationManager notificationManager) {
        this.dao = dao;
        this.notificationManager = notificationManager;
    }

    public RZMUser get(String name) {
        return dao.get(name);
    }

    public RZMUser getCloned(long id) throws CloneNotSupportedException {
        RZMUser user = dao.get(id);
        return (RZMUser) (user == null ? null : user.clone());
    }

    public RZMUser getCloned(String loginName) throws CloneNotSupportedException {
        RZMUser user = dao.get(loginName);
        return (RZMUser) (user == null ? null : user.clone());
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
        if (notificationManager != null) {
            notificationManager.deleteNotificationsByAddresse(user);
            RZMUser tempUser = dao.get(user.getObjId());
            dao.delete(tempUser);
        } else {
            dao.delete(user);
        }
    }

    public void delete(String loginName) {
        RZMUser user = get(loginName);
        if (user != null) delete(user);
    }

    public List<RZMUser> findAll() {
        return dao.findAll();
    }

    public List<RZMUser> find(Criterion criteria) {
        return dao.find(criteria);
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

    public RZMUser findUserByEmailAndRole(String email, String domainName) {
        return dao.findUserByEmailAndRole(email, domainName);
    }

    public int count(Criterion criteria) {
        return dao.count(criteria);
    }

    public List<RZMUser> find(Criterion criteria, int offset, int limit) {
        CheckTool.checkNoNegative(offset, "offset is negative");
        CheckTool.checkNoNegative(limit, "limit is negative");
        return dao.find(criteria, offset, limit);
    }
}
