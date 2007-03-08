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

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring-facade-common.xml").getBean("authenticationServiceBean");
    }

    @Test
    public void testAuthenticate() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth(TestUserManager.ADMIN_WITH_SECURID_LOGIN_VALID, TestUserManager.ADMIN_WITH_SECURID_PASSWORD_VALID);

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {
                
                SecurIDAuth securIDAuth = new SecurIDAuth(
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_LOGIN_VALID,
                        TestSecurIDService.ADMIN_WITH_SECURID_SECURID_PASSWORD_VALID);

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
    public void testAuthenticateOnlySecurID() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth(
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_LOGIN_VALID,
                TestSecurIDService.ADMIN_WITH_SECURID_SECURID_PASSWORD_VALID);

        try {
            AuthenticatedUser authenticatedUser = authService.authenticate(securIDAuth);

        } catch (AuthenticationRequiredException e) {
            return;
        }
        
        assert false;
    }

}
