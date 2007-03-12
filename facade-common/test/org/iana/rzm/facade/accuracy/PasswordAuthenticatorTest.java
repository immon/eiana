package org.iana.rzm.facade.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.auth.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * org.iana.rzm.facade.accuracy.PasswordAuthenticatorTest
 *
 * @author Marcin Zajaczkowski
 */
@Test(groups = {"facade", "facade-common"})
public class PasswordAuthenticatorTest {

    private AuthenticationService authService;
    private AuthenticationService passwordAuthenticator;

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("authenticationServiceBean");
        passwordAuthenticator = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("passwordAuthenticator");
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

    @Test
    public void testAuthenticateWithToken() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            //to get token for further test
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);

                AuthenticatedUser authenticatedUser;
                try {
                    //with PasswordAuthenticator try of authentication with secudID should throw an excepton
                    passwordAuthenticator.authenticate(e.getToken(), securIDAuth);

                } catch(AuthenticationRequiredException ee) {
                    assert ee.getRequired() == Authentication.SECURID;
                    return;
                }
                assert false;
            }
            assert false;
        }
        assert false;
    }

    @Test
    public void testAuthenticateWithSecurID() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);

        try {
            //as a parametr to PasswordAuthenticator can be passed only PasswordAuth (not SecurIDAuth)
            passwordAuthenticator.authenticate(securIDAuth);

        } catch(ClassCastException e) {
            return;
        }
        assert false;
    }
}
