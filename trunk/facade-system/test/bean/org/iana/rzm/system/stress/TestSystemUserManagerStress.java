/**
 * org.iana.rzm.system.stress
 * (C) Research and Academic Computer Network - NASK
 * piotrt, 2007-03-09, 10:37:53
 */
package org.iana.rzm.system.stress;

import org.iana.rzm.user.*;
import org.iana.rzm.common.exceptions.InvalidNameException;

import java.util.List;

public class TestSystemUserManagerStress implements UserManager {

    private static int NUMBER_OF_DOMAINS = 100;
    private final RZMUser user;

    public TestSystemUserManagerStress() {
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
            for (int i=0; i<NUMBER_OF_DOMAINS; i++) {
                Role role = new Role();
                role.setName("facadesystemiana"+i+".org");
                role.setType(Role.Type.TC);
                userCreated.addRole(role);
                role = new Role();
                role.setName("facadesystemiana"+i+".org");
                role.setType(Role.Type.AC);
                userCreated.addRole(role);
            }
        } catch (InvalidNameException e) {
            //
        }
        return userCreated;
    }
}
