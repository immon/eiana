package org.iana.rzm.facade.accuracy;

import org.iana.rzm.user.*;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * org.iana.rzm.facade.accuracy.TestUserManager
 *
 * @author Marcin Zajaczkowski
 */
public class TestUserManager implements UserManager {

    public static String NON_EXIST_LOGIN = "nonExistLogin";
    public static String COMMON_FIRST_NAME = "commonFirstName";
    public static String COMMON_LAST_NAME = "commonLastName";

    private final RZMUser testAdminUser;
    private final RZMUser testAdminUserWithSecurID;
    private final RZMUser testWrongPasswordUser;

    public TestUserManager() {

        this.testAdminUser = createTestAdminUser();
        this.testAdminUserWithSecurID = createTestAdminUserWithSecurID();
        this.testWrongPasswordUser = createTestWrongPasswordUser();
    }

    public RZMUser get(long id) {
        return testAdminUser;
    }

    public RZMUser get(String loginName) {

        if (ADMIN_LOGIN_VALID.equals(loginName)) {
            return testAdminUser;
        } else if (ADMIN_WITH_SECURID_VALID_LOGIN.equals(loginName)) {
            return testAdminUserWithSecurID;
        } else if (WRONG_PASSWORD_LOGIN.equals(loginName)) {
            return testWrongPasswordUser;
        } else {
            return null;
        }
    }

    public RZMUser getCloned(long id) throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public RZMUser getCloned(String loginName) throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public void create(RZMUser user) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public List<RZMUser> findAll() {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public List<RZMUser> find(UserCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType, boolean acceptFrom, boolean mustAccept) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public List<RZMUser> findUsersInSystemRole(String name, SystemRole.SystemType roleType, boolean acceptFrom, boolean mustAccept, boolean accessToDomain) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public List<RZMUser> findUsersInAdminRole(AdminRole.AdminType roleType) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public RZMUser findUserByEmail(String email) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public static String ADMIN_LOGIN_VALID = "adminLogin";
    public static String ADMIN_PASSWORD_VALID = "adminPassword";
    public static String ADMIN_FIRST_NAME_VALID = "adminFirstName";
    public static String ADMIN_LAST_NAME_VALID = "adminLastName";

    private RZMUser createTestAdminUser() {

        RZMUser adminUser = new RZMUser();
        adminUser.setObjId(1L);
        adminUser.setFirstName(ADMIN_FIRST_NAME_VALID);
        adminUser.setLastName(ADMIN_LAST_NAME_VALID);
        adminUser.setLoginName(ADMIN_LOGIN_VALID);
        adminUser.setPassword(ADMIN_PASSWORD_VALID);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(false);

        return adminUser;
    }

    public static String ADMIN_WITH_SECURID_VALID_LOGIN = "adminWithSecurIDLogin";
    public static String ADMIN_WITH_SECURID_PASSWORD_VALID = "adminWithSecurIDPassword";
    public static String ADMIN_WITH_SECURID_FIRST_NAME_VALID = "adminWithSecurIDFirstName";
    public static String ADMIN_WITH_SECURID_LAST_NAME_VALID = "adminWithSecurIDLastName";

    private RZMUser createTestAdminUserWithSecurID() {

        RZMUser adminUser = new RZMUser();
        adminUser.setObjId(1L);
        adminUser.setFirstName(ADMIN_WITH_SECURID_FIRST_NAME_VALID);
        adminUser.setLastName(ADMIN_WITH_SECURID_LAST_NAME_VALID);
        adminUser.setLoginName(ADMIN_WITH_SECURID_VALID_LOGIN);
        adminUser.setPassword(ADMIN_WITH_SECURID_PASSWORD_VALID);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(true);

        return adminUser;
    }

    public static String WRONG_PASSWORD_LOGIN = "wrongPasswordLogin";
    public static String WRONG_PASSWORD_PASSWORD = "wrongPasswordLogin";

    private RZMUser createTestWrongPasswordUser() {

        RZMUser adminUser = new RZMUser();
        adminUser.setFirstName(COMMON_FIRST_NAME);
        adminUser.setLastName(COMMON_LAST_NAME);
        adminUser.setLoginName(WRONG_PASSWORD_LOGIN);
        adminUser.setPassword("bad" + WRONG_PASSWORD_PASSWORD);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(false);

        return adminUser;
    }

    //todo createGenericAdminUSer() could be added
    public void delete(RZMUser user) {
        throw new UnsupportedOperationException("Not implemented in test class.");
    }

    public void delete(String loginName) {
        throw new UnsupportedOperationException("Not implemented in test class.");
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
