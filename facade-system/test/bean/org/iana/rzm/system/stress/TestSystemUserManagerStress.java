package org.iana.rzm.system.stress;

import org.iana.rzm.user.*;
import org.iana.criteria.Criterion;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public class TestSystemUserManagerStress implements UserManager {

    private static int NUMBER_OF_DOMAINS = 100;
    private final RZMUser user;

    @Test
    public TestSystemUserManagerStress() {
        this.user = createSystemUser();
    }

    public RZMUser get(long id) {
        return user;
    }

    public RZMUser get(String loginName) {
        //"facade-common-test" could be a constant
        if ("testSystemStress".equals(loginName)) {
            return user;
        } else {
            return null;
        }
    }

    public RZMUser getCloned(long id) throws CloneNotSupportedException {
        throw new IllegalStateException("Not implemented yet.");
    }

    public RZMUser getCloned(String loginName) throws CloneNotSupportedException {
        throw new IllegalStateException("Not implemented yet.");
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

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType, boolean acceptFrom, boolean mustAccept, boolean accessToDomain) {
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
        userCreated.setLoginName("testSystemStress");
        userCreated.setPassword("testSystemStress");
        userCreated.setObjId(1L);
        for (int i = 0; i < NUMBER_OF_DOMAINS; i++) {
            SystemRole role = new SystemRole();
            role.setName("stressfacadesystemiana" + i + ".org");
            role.setType(SystemRole.SystemType.TC);
            userCreated.addRole(role);

            role = new SystemRole();
            role.setName("stressfacadesystemiana" + i + ".org");
            role.setType(SystemRole.SystemType.AC);
            userCreated.addRole(role);
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

    public int count(Criterion criteria) {
        return 0;
    }

    public List<RZMUser> find(Criterion criteria, int offset, int limit) {
        return null;
    }

    public RZMUser findUserByEmailAndRole(String email, String domainName) {
        return null;
    }

    public void deleteAll() {

    }
}
