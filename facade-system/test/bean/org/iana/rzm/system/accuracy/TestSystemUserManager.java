package org.iana.rzm.system.accuracy;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.user.*;
import org.iana.rzm.common.exceptions.InvalidNameException;

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

    private SystemUser createSystemUser() {
        SystemUser userCreated = new SystemUser();
        userCreated.setFirstName("Geordi");
        userCreated.setLastName("LaForge");
        userCreated.setLoginName("test");
        userCreated.setPassword("test");
        userCreated.setObjId(1L);
        try {
            Role role = new Role();
            role.setName("facadesystemiana.org");
            role.setType(Role.Type.TC);
            userCreated.addRole(role);
            role = new Role();
            role.setName("facadesystemiana.org");
            role.setType(Role.Type.AC);
            userCreated.addRole(role);
            role = new Role();
            role.setName("facadesystemiana1.org");
            role.setType(Role.Type.SO);
            userCreated.addRole(role);
            role = new Role();
            role.setName("facadesystemiana1.org");
            role.setType(Role.Type.AC);
            userCreated.addRole(role);
        } catch (InvalidNameException e) {
            //
        }
        return userCreated;
    }
}
