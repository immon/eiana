package org.iana.rzm.facade.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.AuthenticatorMapObject;
import org.iana.rzm.user.UserManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * org.iana.rzm.facade.accuracy.PasswordAuthenticatorTest
 *
 * @author Marcin Zajaczkowski
 */
@Test(groups = {"facade", "facade-common"})
public class PasswordAuthenticatorTest {

    private UserManager manager;
    private Map<String,Authenticator> authenticatorMap;

    private AuthenticationService authService;

    @BeforeClass
    public void init() {
        authService = (AuthenticationService) new ClassPathXmlApplicationContext("spring.xml").getBean("authenticationServiceBean");
    }

    @Test
    public void testAuthenticate() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth("facade-common-test-adminuser", "engine");

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);
        assert authenticatedUser != null;
        assert "facade-common-test-adminuser-first-name".equals(authenticatedUser.getFirstName());
        assert "facade-common-test-adminuser-last-name".equals(authenticatedUser.getLastName());
        //todo Add more tests
    }
}
