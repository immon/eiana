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

    public static String NON_EXIST_LOGIN = "nonExistLogin";
    public static String COMMON_FIRST_NAME = "commonFirstName";
    public static String COMMON_LAST_NAME = "commonLastName";

    private final AdminUser testAdminUser;
    private final AdminUser testAdminUserWithSecurID;
    private final AdminUser testWrongPasswordUser;

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
        } else if (ADMIN_WITH_SECURID_LOGIN_VALID.equals(loginName)) {
            return testAdminUserWithSecurID;
        } else if (WRONG_PASSWORD_LOGIN.equals(loginName)) {
            return testWrongPasswordUser;
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

    public List<RZMUser> find(UserCriteria criteria){
        throw new IllegalStateException("Not implemented yet.");
    }

    public static String ADMIN_LOGIN_VALID = "adminLogin";
    public static String ADMIN_PASSWORD_VALID = "adminPassword";
    public static String ADMIN_FIRST_NAME_VALID = "adminFirstName";
    public static String ADMIN_LAST_NAME_VALID = "adminLastName";

    private AdminUser createTestAdminUser() {

        AdminUser adminUser = new AdminUser();
        adminUser.setFirstName(ADMIN_FIRST_NAME_VALID);
        adminUser.setLastName(ADMIN_LAST_NAME_VALID);
        adminUser.setLoginName(ADMIN_LOGIN_VALID);
        adminUser.setPassword(ADMIN_PASSWORD_VALID);
        adminUser.setType(AdminUser.Type.IANA_STAFF);
        adminUser.setSecurID(false);

        return adminUser;
    }

    public static String ADMIN_WITH_SECURID_LOGIN_VALID = "adminWithSecurIDLogin";
    public static String ADMIN_WITH_SECURID_PASSWORD_VALID = "adminWithSecurIDPassword";
    public static String ADMIN_WITH_SECURID_FIRST_NAME_VALID = "adminWithSecurIDFirstName";
    public static String ADMIN_WITH_SECURID_LAST_NAME_VALID = "adminWithSecurIDLastName";

    private AdminUser createTestAdminUserWithSecurID() {

        AdminUser adminUser = new AdminUser();
        adminUser.setFirstName(ADMIN_WITH_SECURID_FIRST_NAME_VALID);
        adminUser.setLastName(ADMIN_WITH_SECURID_LAST_NAME_VALID);
        adminUser.setLoginName(ADMIN_WITH_SECURID_LOGIN_VALID);
        adminUser.setPassword(ADMIN_WITH_SECURID_PASSWORD_VALID);
        adminUser.setType(AdminUser.Type.IANA_STAFF);
        adminUser.setSecurID(true);

        return adminUser;
    }

    public static String WRONG_PASSWORD_LOGIN = "wrongPasswordLogin";
    public static String WRONG_PASSWORD_PASSWORD = "wrongPasswordLogin";

    private AdminUser createTestWrongPasswordUser() {

        AdminUser adminUser = new AdminUser();
        adminUser.setFirstName(COMMON_FIRST_NAME);
        adminUser.setLastName(COMMON_LAST_NAME);
        adminUser.setLoginName(WRONG_PASSWORD_LOGIN);
        adminUser.setPassword("bad" + WRONG_PASSWORD_PASSWORD);
        adminUser.setType(AdminUser.Type.IANA_STAFF);
        adminUser.setSecurID(false);

        return adminUser;
    }

    //todo createGenericAdminUSer() could be added
}
