package org.iana.rzm.facade.common.auth;

import org.iana.rzm.conf.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.SecurIDAuth;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;

/**
 * org.iana.rzm.facade.common.auth.SecurIDAuthenticatorTest
 *
 * @author Marcin Zajaczkowski
 * @author Piotr Tkaczyk
 */
@Test(groups = {"facade", "facade-common", "facade-auth"})
public class SecurIDAuthenticatorTest extends RollbackableSpringContextTest {

    protected AuthenticationService authenticationServiceBean;
    protected AuthenticationService securIDAuthenticator;
    protected UserManager userManager;

    RZMUser adminUser;

    public SecurIDAuthenticatorTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() {
        adminUser = new RZMUser();
        adminUser.setFirstName("admin");
        adminUser.setLastName("admin");
        adminUser.setLoginName("admin_login");
        adminUser.setPassword("admin_password");
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        adminUser.setSecurID(true);

        userManager.create(adminUser);
    }

    @Test (expectedExceptions = {ClassCastException.class})
    public void testAuthenticateWithWrongAuthType() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth("admin_login", "admin_password");

        try {
            authenticationServiceBean.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                //as a parametr to SecurIDAuthenticator with Token can be passed only SecurdIDAuth (not PasswordAuth)
                AuthenticatedUser authUser = securIDAuthenticator.authenticate(e.getToken(), passwordAuth);
            }
        }
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {

        securIDAuthenticator.authenticate(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth("admin_login", "admin_password");

        securIDAuthenticator.authenticate(null, securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {

        securIDAuthenticator.authenticate(null, null);
    }

    protected void cleanUp() throws Exception {
    }
}
