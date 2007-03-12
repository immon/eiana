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
@Test(groups = {"facade", "facade-common", "facade-auth"})
public class PasswordAuthenticatorTest {

    private AuthenticationService authService;
    private AuthenticationService passwordAuthenticator;

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("authenticationServiceBean");
        passwordAuthenticator = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("passwordAuthenticator");
    }

    //Note: expectedExceptions cannot be used because that exception can be throw in multiple places (and in some of them it's ok)
    @Test
    public void testAuthenticateWithToken() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            //to get token for the next test
            authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

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

    @Test (expectedExceptions = {ClassCastException.class})
    public void testAuthenticateWithSecurID() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD);

        //as a parametr to PasswordAuthenticator can be passed only PasswordAuth (not SecurIDAuth)
        passwordAuthenticator.authenticate(securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {

        passwordAuthenticator.authenticate(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        passwordAuthenticator.authenticate(null, securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {

        passwordAuthenticator.authenticate(null, null);
    }
}
