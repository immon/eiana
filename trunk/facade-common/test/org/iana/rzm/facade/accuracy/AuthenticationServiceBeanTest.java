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

    @Test
    //could be moved to failure package
    public void testAuthenticateNoUser() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.NON_EXIST_LOGIN, "foo");

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch(AuthenticationFailedException e) {
            return;
        }
        assert false;
    }

    @Test
    //could be moved to failure package
    public void testAuthenticateWrongPassword() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.WRONG_PASSWORD_LOGIN, TestUserManager.WRONG_PASSWORD_PASSWORD);

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch(AuthenticationFailedException e) {
            return;
        }
        assert false;
    }

    @Test
    public void testAuthenticateWithNullData() throws Exception {

        try {
            authService.authenticate(null);
        } catch(IllegalArgumentException e) {
            return;
        }

        assert false;
    }


    //from securID

    @Test
    public void testAuthenticateWrongSecurdIDNeeded() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {
                return;
            }
            assert false;
        }
        assert false;
    }

    @Test
    public void testAuthenticateWithSecurID() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

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
            assert false;
        }
        assert false;
    }

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
                assert false;
            }
            assert false;
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
            return;
        }
        assert false;
    }

    @Test
    public void testAuthenticateWithNullToken() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        try {
            authService.authenticate(null, securIDAuth);

        } catch(IllegalArgumentException e) {
            return;
        }
        assert false;
    }

    @Test
    public void testAuthenticateWithNullBoth() throws Exception {

        try {
            authService.authenticate(null, null);

        } catch(IllegalArgumentException e) {
            return;
        }
        assert false;
    }
}
