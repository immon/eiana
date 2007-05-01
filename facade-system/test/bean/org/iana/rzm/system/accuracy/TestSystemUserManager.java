package org.iana.rzm.system.accuracy;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.user.*;
import org.iana.criteria.Criterion;

import java.util.List;

public class TestSystemUserManager implements UserManager {

    private final RZMUser user;

    public TestSystemUserManager() {
        this.user = createSystemUser();
    }

    public RZMUser get(long id) {
        return user;
    }

    public RZMUser get(String loginName) {
        //"facade-common-test" could be a constant
        if ("test".equals(loginName)) {
            return user;
        } else {
            return null;
        }
    }

    public void create(RZMUser user) {
        throw new IllegalStateException("Not implemented yet.");
    }

    public List<RZMUser> findAll() {
        throw new IllegalStateException("Not implemented yet.");
    }

    public List<RZMUser> find(UserCriteria criteria) {
        throw new IllegalStateException("Not implemented yet.");
    }

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType, boolean acceptFrom, boolean mustAccept) {
        throw new IllegalStateException("Not implemented yet.");
    }

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType) {
        throw new IllegalStateException("Not implemented yet.");
    }

    public RZMUser findUserByEmail(String email) {
        throw new IllegalStateException("Not implemented yet.");
    }

    private RZMUser createSystemUser() {
        RZMUser userCreated = new RZMUser();
        userCreated.setFirstName("Geordi");
        userCreated.setLastName("LaForge");
        userCreated.setLoginName("test");
        userCreated.setPassword("test");
        userCreated.setObjId(1L);
        try {
            SystemRole role = new SystemRole();
            role.setName("facadesystemiana.org");
            role.setType(SystemRole.SystemType.TC);
            userCreated.addRole(role);

            role = new SystemRole();
            role.setName("facadesystemiana.org");
            role.setType(SystemRole.SystemType.AC);
            userCreated.addRole(role);

            role = new SystemRole();
            role.setName("facadesystemiana1.org");
            role.setType(SystemRole.SystemType.SO);
            userCreated.addRole(role);

            role = new SystemRole();
            role.setName("facadesystemiana1.org");
            role.setType(SystemRole.SystemType.AC);
            userCreated.addRole(role);
        } catch (InvalidNameException e) {
            //
        }
        return userCreated;
    }

    public void delete(RZMUser user) {
        throw new IllegalStateException("Not implemented yet.");
    }

    public void delete(String loginName) {
        throw new IllegalStateException("Not implemented yet.");
    }                                     

    public void update(RZMUser user) {}
    public List<RZMUser> find(Criterion criteria) {return null;}
}
