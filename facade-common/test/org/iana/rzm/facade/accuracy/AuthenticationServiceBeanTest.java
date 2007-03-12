package org.iana.rzm.facade.accuracy;

import org.iana.rzm.facade.auth.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * org.iana.rzm.facade.accuracy.AuthenticationServiceBeanTest
 *
 * @author Marcin Zajaczkowski
 */
@Test(groups = {"facade", "facade-common", "facade-auth"})
public class AuthenticationServiceBeanTest {

    private AuthenticationService authService;

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("authenticationServiceBean");
    }

    @Test
    public void testAuthenticate() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_LOGIN_VALID, TestUserManager.ADMIN_PASSWORD_VALID);

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);
        assert authenticatedUser != null;
        assert TestUserManager.ADMIN_FIRST_NAME_VALID.equals(authenticatedUser.getFirstName());
        assert TestUserManager.ADMIN_LAST_NAME_VALID.equals(authenticatedUser.getLastName());
    }

    //could be moved to failure package
    @Test (expectedExceptions = {AuthenticationFailedException.class})
    public void testAuthenticateNoUser() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.NON_EXIST_LOGIN, "foo");

        authService.authenticate(passwordAuth);
    }

    //could be moved to failure package
    @Test (expectedExceptions = {AuthenticationFailedException.class})
    public void testAuthenticateWrongPassword() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.WRONG_PASSWORD_LOGIN, TestUserManager.WRONG_PASSWORD_PASSWORD);

        authService.authenticate(passwordAuth);
    }

    //from securID

    @Test (expectedExceptions = {AuthenticationRequiredException.class})
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
                assert TestUserManager.ADMIN_WITH_SECURID_FIRST_NAME_VALID.equals(authenticatedUser.getFirstName());
                assert TestUserManager.ADMIN_WITH_SECURID_LAST_NAME_VALID.equals(authenticatedUser.getLastName());
                return;
            }
        }
        assert false;
    }

    //It's more complicated scenerio (exception can be thrown in 2 places) and
    //it's more reliable to catch exception in try..catch construction
    @Test
    public void testAuthenticateWrongSecurdID() throws Exception {

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
                    return;
                }
            }
        }
        assert false;
    }

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

    //common

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {

        authService.authenticate(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        authService.authenticate(null, securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {

        authService.authenticate(null, null);
    }
}
