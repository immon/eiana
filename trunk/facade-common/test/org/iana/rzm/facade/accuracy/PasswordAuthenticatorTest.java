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

        PasswordAuth passwordAuth = new PasswordAuth("facade-common-test-adminuser", "facade-common-test-adminuser-password");

        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);
        assert authenticatedUser != null;
        assert "facade-common-test-adminuser-first-name".equals(authenticatedUser.getFirstName());
        assert "facade-common-test-adminuser-last-name".equals(authenticatedUser.getLastName());
        //todo Add more tests
    }
}
