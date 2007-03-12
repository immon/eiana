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
@Test(groups = {"facade", "facade-common", "facade-auth"})
public class SecurIDAuthenticatorTest {

    private AuthenticationService authService;
    private AuthenticationService securIDAuthenticator;

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("authenticationServiceBean");
        securIDAuthenticator = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("securIDAuthenticator");
    }

    @Test (expectedExceptions = {ClassCastException.class})
    public void testAuthenticateWithWrongAuthType() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                    //as a parametr to SecurIDAuthenticator with Token can be passed only SecurdIDAuth (not PasswordAuth)
                    securIDAuthenticator.authenticate(e.getToken(), passwordAuth);
            }
        }
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {

        securIDAuthenticator.authenticate(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_LOGIN,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD);

        securIDAuthenticator.authenticate(null, securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {

        securIDAuthenticator.authenticate(null, null);
    }
}
