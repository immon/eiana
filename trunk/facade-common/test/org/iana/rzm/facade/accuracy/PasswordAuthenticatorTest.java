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
}
