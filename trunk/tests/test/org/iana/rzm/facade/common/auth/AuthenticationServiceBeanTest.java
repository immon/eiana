package org.iana.rzm.facade.common.auth;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.accuracy.TestSecurIDService;
import org.iana.rzm.facade.accuracy.TestUserManager;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;

/**
 * @author Marcin Zajaczkowski
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"test", "authenticationManager"})
public class AuthenticationServiceBeanTest {

    private UserManager userManager;
    private AuthenticationService authService;

    private RZMUser testAdminUser;
    private RZMUser testAdminUserWithSecurID;
    private RZMUser testWrongPasswordUser;

    @BeforeClass
    public void init() throws IOException {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        authService = (AuthenticationService) appCtx.getBean("authenticationServiceBean");
        createTestUsers();
    }

    private void createTestUsers() throws IOException {
        testAdminUser = createTestAdminUser();
        userManager.create(testAdminUser);

        testAdminUserWithSecurID = createTestAdminUserWithSecurID();
        userManager.create(testAdminUserWithSecurID);

        testWrongPasswordUser = createTestWrongPasswordUser();
        userManager.create(testWrongPasswordUser);
    }

    @Test
    public void testAuthenticate() throws Exception {
        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_LOGIN_VALID, TestUserManager.ADMIN_PASSWORD_VALID);

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);
        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_LOGIN_VALID.equals(authenticatedUser.getUserName());
    }

    @Test(expectedExceptions = {AuthenticationFailedException.class})
    public void testAuthenticateNoUser() throws Exception {
        PasswordAuth passwordAuth = new PasswordAuth();
        passwordAuth.setUserName(TestUserManager.NON_EXIST_LOGIN);
        passwordAuth.setPassword("foo");

        authService.authenticate(passwordAuth);
    }

    @Test(expectedExceptions = {AuthenticationFailedException.class})
    public void testAuthenticateWrongPassword() throws Exception {
        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.WRONG_PASSWORD_LOGIN, TestUserManager.WRONG_PASSWORD_PASSWORD);

        authService.authenticate(passwordAuth);
    }

    @Test(expectedExceptions = {AuthenticationRequiredException.class})
    public void testAuthenticateWrongSecurdIDNeeded() throws Exception {
        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        authService.authenticate(passwordAuth);
    }

    @Test
    public void testAuthenticateWithSecurID() throws Exception {
        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

                AuthenticatedUser authenticatedUser = authService.authenticate(e.getToken(), securIDAuth);
                assert TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN.equals(authenticatedUser.getUserName());
                return;
            }
        }
        assert false;
    }

    //It's more complicated scenerio (exception can be thrown in 2 places) and
    //it's more reliable to catch exception in try..catch construction
//    @Test
//    public void testAuthenticateWrongSecurdID() throws Exception {
//        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);
//
//        try {
//            authService.authenticate(passwordAuth);
//
//        } catch (AuthenticationRequiredException e) {
//            if (e.getRequired() == Authentication.SECURID) {
//
//                SecurIDAuth securIDAuth = new SecurIDAuth(
//                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
//                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);
//
//                try {
//                    authService.authenticate(e.getToken(), securIDAuth);
//
//                } catch (AuthenticationFailedException ee) {
//                    return;
//                }
//            }
//        }
//        assert false;
//    }

    @Test
    public void testAuthenticateOnlySecurID() throws Exception {
        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        try {
            authService.authenticate(securIDAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.PASSWORD) {
                return;
            }
        }
        assert false;
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {
        authService.authenticate(null);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {
        SecurIDAuth securIDAuth = new SecurIDAuth();
        securIDAuth.setUserName(TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN);
        securIDAuth.setPassword(TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        authService.authenticate(null, securIDAuth);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {
        authService.authenticate(null, null);
    }

    @Test
    public void testInvalidateUser() throws AuthenticationFailedException, AuthenticationRequiredException {
        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_LOGIN_VALID, TestUserManager.ADMIN_PASSWORD_VALID);

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_LOGIN_VALID.equals(authenticatedUser.getUserName());
        assert !authenticatedUser.isInvalidated();

        authenticatedUser.invalidate();

        assert authenticatedUser.isInvalidated();
    }

    @Test
    public void testAuthenticateByMail() throws AuthenticationFailedException, AuthenticationRequiredException {
        MailAuth mailAuth = new MailAuth(ADMIN_EMAIL);

        AuthenticatedUser authenticatedUser = authService.authenticate(mailAuth);

        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_LOGIN_VALID.equals(authenticatedUser.getUserName());
    }

    @Test (expectedExceptions = AuthenticationFailedException.class)
    public void testAuthenticateByMailFails() throws AuthenticationFailedException, AuthenticationRequiredException {
        MailAuth mailAuth = new MailAuth("bad" + ADMIN_EMAIL);
        authService.authenticate(mailAuth);
    }

    public static String ADMIN_SIGNED_MESSAGE_FILE_NAME = "test-message.txt.asc";

    @Test
    public void testAuthenticateByPgpMail() throws AuthenticationFailedException, AuthenticationRequiredException, IOException {
        PgpMailAuth pgpMailAuth = new PgpMailAuth(ADMIN_EMAIL, loadFromFile(ADMIN_SIGNED_MESSAGE_FILE_NAME));

        AuthenticatedUser authenticatedUser = authService.authenticate(pgpMailAuth);

        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_LOGIN_VALID.equals(authenticatedUser.getUserName());
    }

    public static String WRONG_SIGNED_MESSAGE_FILE_NAME = "test-message-1.txt.asc";

    @Test (expectedExceptions = AuthenticationFailedException.class)
    public void testAuthenticateByPgpMailFails() throws AuthenticationFailedException, AuthenticationRequiredException, IOException {
        PgpMailAuth pgpMailAuth = new PgpMailAuth(ADMIN_EMAIL, loadFromFile(WRONG_SIGNED_MESSAGE_FILE_NAME));
        authService.authenticate(pgpMailAuth);
    }

    @AfterClass
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
    }

    public static String NON_EXIST_LOGIN = "nonExistLogin";
    public static String COMMON_FIRST_NAME = "commonFirstName";
    public static String COMMON_LAST_NAME = "commonLastName";

    public static String ADMIN_LOGIN_VALID = "adminLogin";
    public static String ADMIN_PASSWORD_VALID = "adminPassword";
    public static String ADMIN_FIRST_NAME_VALID = "adminFirstName";
    public static String ADMIN_LAST_NAME_VALID = "adminLastName";
    public static String ADMIN_EMAIL = "admin@no-mail.org";
    public static String ADMIN_KEY_FILE_NAME = "tester.pgp.asc";

    private RZMUser createTestAdminUser() throws IOException {

        RZMUser adminUser = new RZMUser();
        adminUser.setObjId(1L);
        adminUser.setFirstName(ADMIN_FIRST_NAME_VALID);
        adminUser.setLastName(ADMIN_LAST_NAME_VALID);
        adminUser.setLoginName(ADMIN_LOGIN_VALID);
        adminUser.setPassword(ADMIN_PASSWORD_VALID);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(false);
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setPublicKey(loadFromFile(ADMIN_KEY_FILE_NAME));

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
    public static String WRONG_PASSWORD_KEY_FILE_NAME = "tester1.pgp.asc";

    private RZMUser createTestWrongPasswordUser() throws IOException {

        RZMUser adminUser = new RZMUser();
        adminUser.setObjId(1L);
        adminUser.setFirstName(COMMON_FIRST_NAME);
        adminUser.setLastName(COMMON_LAST_NAME);
        adminUser.setLoginName(WRONG_PASSWORD_LOGIN);
        adminUser.setPassword("bad" + WRONG_PASSWORD_PASSWORD);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(false);
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setPublicKey(loadFromFile(WRONG_PASSWORD_KEY_FILE_NAME));

        return adminUser;
    }

    private String loadFromFile(String fileName) throws IOException {
        InputStream in = getClass().getResourceAsStream(fileName);
        if (in == null) throw new FileNotFoundException(fileName);
        DataInputStream dis = new DataInputStream(in);
        StringBuffer buf;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(dis, "US-ASCII"));
            try {
                buf = new StringBuffer();
                String line = reader.readLine();
                if (line != null) {
                    buf.append(line);
                    while ((line = reader.readLine()) != null) {
                        buf.append("\n");
                        buf.append(line);
                    }
                }
                return buf.toString();
            } finally {
                reader.close();
            }
        } finally {
            dis.close();
        }
    }
}
