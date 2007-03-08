package org.iana.rzm.facade.accuracy;

import org.iana.rzm.user.*;

import java.util.List;

/**
 * org.iana.rzm.facade.accuracy.TestUserManager
 *
 * @author Marcin Zajaczkowski
 *
 * Q: should be here or in directory with tests?
 */
public class TestUserManager implements UserManager {

    private final RZMUser facadeCommonTestAdminUser;

    public TestUserManager() {

        this.facadeCommonTestAdminUser = createFacadeCommonTestAdminUser();
    }

    public RZMUser get(long id) throws UserException {
        return facadeCommonTestAdminUser;
    }

    public RZMUser get(String loginName) throws UserException {
        //"facade-common-test" could be a constant
        if ("facade-common-test-adminuser".equals(loginName)) {
            return facadeCommonTestAdminUser;
        } else {
            return null;
        }
    }

    public void create(RZMUser user) throws UserException {
        throw new IllegalStateException("Not implemented yet.");
    }

    public List<RZMUser> findAll() throws UserException {
        throw new IllegalStateException("Not implemented yet.");
    }

    public List<RZMUser> find(UserCriteria criteria) throws UserException {
        throw new IllegalStateException("Not implemented yet.");
    }

    private AdminUser createFacadeCommonTestAdminUser() {

        AdminUser adminUser = new AdminUser();
        adminUser.setFirstName("facade-common-test-adminuser-first-name");
        adminUser.setLastName("facade-common-test-adminuser-last-name");
        adminUser.setLoginName("facade-common-test-adminuser-login");
        adminUser.setPassword("facade-common-test-adminuser-password");
        adminUser.setType(AdminUser.Type.IANA_STAFF);

        return adminUser;
    }
}
