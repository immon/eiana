package org.iana.authentication;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.accuracy.TestUserManager;
import org.iana.rzm.facade.accuracy.TestSecurIDService;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * org.iana.authentication.AuthenticationServiceBeanTest
 * (C) Research and Academic Computer Network - NASK
 * marcinz, 2007-04-25, 14:38:46
 */
public class AuthenticationServiceBeanTest {

    private PlatformTransactionManager txMgr;
    private UserDAO userDAO;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    TransactionStatus txStatus;
    private AuthenticationService authService;

    private RZMUser testAdminUser;
    private RZMUser testAdminUserWithSecurID;
    private RZMUser testWrongPasswordUser;
    private long testLazyUser1ID;
    private RZMUser testLazyUser2;


    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        userDAO = (UserDAO) appCtx.getBean("userDAO");
        authService = (AuthenticationService) appCtx.getBean("authenticationServiceBean");

        createTestUsers();
    }

    private void createTestUsers() {

        txStatus = txMgr.getTransaction(txDef);

        testAdminUser = createTestAdminUser();
        userDAO.create(testAdminUser);

        testAdminUserWithSecurID = createTestAdminUserWithSecurID();
        userDAO.create(testAdminUserWithSecurID);

        testWrongPasswordUser = createTestWrongPasswordUser();
        userDAO.create(testWrongPasswordUser);

        RZMUser tempTestLazyUser1 = createTestWrongPasswordUser();
        userDAO.create(tempTestLazyUser1);
        testLazyUser1ID = tempTestLazyUser1.getObjId();

        testLazyUser2 = createTestWrongPasswordUser();
        userDAO.create(testLazyUser2);
        long testLazyUser2ID = testLazyUser2.getObjId();

        txMgr.commit(txStatus);


        txStatus = txMgr.getTransaction(txDef);

        //get from a database to prevent keeping reference of a local object
        testLazyUser2 = userDAO.get(testLazyUser2ID);

        txMgr.commit(txStatus);
    }

    @Test
    public void testAuthenticate() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_LOGIN_VALID, TestUserManager.ADMIN_PASSWORD_VALID);

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);
        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_LOGIN_VALID.equals(authenticatedUser.getUserName());

        //todo try...finally block could be added
        txMgr.commit(txStatus);
    }

    //could be moved to failure package
    @Test (expectedExceptions = {AuthenticationFailedException.class})
    public void testAuthenticateNoUser() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        PasswordAuth passwordAuth = new PasswordAuth();
        passwordAuth.setUserName(TestUserManager.NON_EXIST_LOGIN);
        passwordAuth.setPassword("foo");

        authService.authenticate(passwordAuth);

        txMgr.commit(txStatus);
    }

    //could be moved to failure package
    @Test (expectedExceptions = {AuthenticationFailedException.class})
    public void testAuthenticateWrongPassword() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.WRONG_PASSWORD_LOGIN, TestUserManager.WRONG_PASSWORD_PASSWORD);

        authService.authenticate(passwordAuth);

        txMgr.commit(txStatus);
    }

    //from securID

    @Test (expectedExceptions = {AuthenticationRequiredException.class})
    public void testAuthenticateWrongSecurdIDNeeded() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        authService.authenticate(passwordAuth);

        txMgr.commit(txStatus);
    }

    @Test
    public void testAuthenticateWithSecurID() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

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
                txMgr.commit(txStatus);
                return;
            }
        }
        assert false;
    }

    //It's more complicated scenerio (exception can be thrown in 2 places) and
    //it's more reliable to catch exception in try..catch construction
    @Test
    public void testAuthenticateWrongSecurdID() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);

                try {
                    authService.authenticate(e.getToken(), securIDAuth);

                } catch (AuthenticationFailedException ee) {
                    txMgr.commit(txStatus);
                    return;
                }
            }
        }
        assert false;
    }

    @Test
    public void testAuthenticateOnlySecurID() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        try {
            authService.authenticate(securIDAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.PASSWORD) {
                txMgr.commit(txStatus);
                return;
            }
        }
        assert false;
    }

    //common

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        authService.authenticate(null);

        txMgr.commit(txStatus);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        SecurIDAuth securIDAuth = new SecurIDAuth();
        securIDAuth.setUserName(TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN);
        securIDAuth.setPassword(TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        authService.authenticate(null, securIDAuth);

        txMgr.commit(txStatus);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {
        txStatus = txMgr.getTransaction(txDef);

        authService.authenticate(null, null);

        txMgr.commit(txStatus);
    }

    @Test
    public void testInvalidateUser() throws AuthenticationFailedException, AuthenticationRequiredException {
        txStatus = txMgr.getTransaction(txDef);

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_LOGIN_VALID, TestUserManager.ADMIN_PASSWORD_VALID);

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_LOGIN_VALID.equals(authenticatedUser.getUserName());
        assert !authenticatedUser.isInvalidated();

        authenticatedUser.invalidate();

        assert authenticatedUser.isInvalidated();

        txMgr.commit(txStatus);
    }

    /**
     * Lazy collection should be initialized inside open session
     */
    @Test
    public void testGetLazyPropertyInSession() throws Exception {

        txStatus = txMgr.getTransaction(txDef);

        RZMUser testLazyUser1 = userDAO.get(testLazyUser1ID);
        assert testLazyUser1 != null;

        //first line should be enough to make a try to initialized lazy fetched collection
        int size = testLazyUser1.getRoles().size();
        if (size > 0) {
            Role role = testLazyUser1.getRoles().get(0);
        }

        txMgr.commit(txStatus);
    }

    /**
     * Lazy collection shouldn't be initialized outside open session
     */
    @Test (expectedExceptions = {org.hibernate.LazyInitializationException.class})
    public void testGetLazyPropertyOutsideSession() throws Exception {

        //first line should be enough to make a try to initialized lazy fetched collection
        int size = testLazyUser2.getRoles().size();
        if (size > 0) {
            Role role = testLazyUser2.getRoles().get(0);
        }
    }

    @AfterClass
    public void cleanUp() {
        txStatus = txMgr.getTransaction(txDef);

        //problem with delete when object taken another time in the same session
        userDAO.delete(testAdminUser);
        userDAO.delete(testAdminUserWithSecurID);
        userDAO.delete(testWrongPasswordUser);
        userDAO.delete(userDAO.get(testLazyUser1ID));
        userDAO.delete(testLazyUser2);

        txMgr.commit(txStatus);
    }


    //todo could be moved to some common class
    public static String NON_EXIST_LOGIN = "nonExistLogin";
    public static String COMMON_FIRST_NAME = "commonFirstName";
    public static String COMMON_LAST_NAME = "commonLastName";

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
        adminUser.setObjId(1L);
        adminUser.setFirstName(COMMON_FIRST_NAME);
        adminUser.setLastName(COMMON_LAST_NAME);
        adminUser.setLoginName(WRONG_PASSWORD_LOGIN);
        adminUser.setPassword("bad" + WRONG_PASSWORD_PASSWORD);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(false);

        return adminUser;
    }
}
