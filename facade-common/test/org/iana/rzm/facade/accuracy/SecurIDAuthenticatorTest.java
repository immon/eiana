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

    @Test
    public void testAuthenticateWithNullData() throws Exception {

        try {
            securIDAuthenticator.authenticate(null);
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
            securIDAuthenticator.authenticate(null, securIDAuth);
        } catch(IllegalArgumentException e) {
            return;
        }
        assert false;
    }

    @Test
    public void testAuthenticateWithNullBoth() throws Exception {

        try {
            securIDAuthenticator.authenticate(null, null);
        } catch(IllegalArgumentException e) {
            return;
        }
        assert false;
    }
}
