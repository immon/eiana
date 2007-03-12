package org.iana.rzm.facade.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.iana.rzm.facade.auth.*;

/**
 * org.iana.rzm.facade.accuracy.SecurIDAuthenticatorTest
 *
 * @author Marcin Zajaczkowski
 */
@Test(groups = {"facade", "facade-common"})
public class SecurIDAuthenticatorTest {

    private AuthenticationService authService;
    private AuthenticationService securIDAuthenticator;

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("authenticationServiceBean");
        securIDAuthenticator = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("securIDAuthenticator");
    }

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
    public void testAuthenticate() throws Exception {

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
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);

                AuthenticatedUser authenticatedUser;
                try {
                    authenticatedUser = authService.authenticate(e.getToken(), securIDAuth);

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
            AuthenticatedUser authenticatedUser = authService.authenticate(securIDAuth);

        } catch (AuthenticationRequiredException e) {
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

    @Test
    public void testAuthenticateWithWrongAuthType() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);

                try {
                    //as a parametr to SecurIDAuthenticator with Token can be passed only SecurdIDAuth (not PasswordAuth)
                    securIDAuthenticator.authenticate(e.getToken(), passwordAuth);

                } catch (ClassCastException ee) {
                    return;
                }
                assert false;
            }
            assert false;
        }
        assert false;
    }
}
