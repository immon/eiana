package org.iana.rzm.user;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.dao.UserDAO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */

public class UserManagerBean implements UserManager {

    private UserDAO dao;

    public UserManagerBean(UserDAO dao) {
        this.dao = dao;
    }

    public RZMUser get(String name) {
        return dao.get(name);
    }

    public RZMUser getCloned(long id) throws CloneNotSupportedException {
        RZMUser user = dao.get(id);
        return (user == null ? null : user.clone());
    }

    public RZMUser getCloned(String loginName) throws CloneNotSupportedException {
        RZMUser user = dao.get(loginName);
        return (user == null ? null : user.clone());
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
        CheckTool.checkNull(user, "user");
        dao.delete(user);
    }

    public void delete(String loginName) {
        RZMUser user = get(loginName);
        if (user != null) delete(user);
    }

    public void deleteAll() {
        for (RZMUser user : findAll()) {
            delete(user);
        }
    }

    public List<RZMUser> findAll() {
        return dao.findAll();
    }

    public List<RZMUser> find(Criterion criteria) {
        return dao.find(criteria);
    }

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept) {
        return dao.findUsersInSystemRole(name, roleType, acceptFrom, mustAccept, false);
    }

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType,
                                               boolean acceptFrom, boolean mustAccept, boolean accessToDomain) {
        return dao.findUsersInSystemRole(name, roleType, acceptFrom, mustAccept, accessToDomain);
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
