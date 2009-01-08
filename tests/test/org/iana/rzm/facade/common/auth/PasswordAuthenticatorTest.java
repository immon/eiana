package org.iana.rzm.facade.common.auth;

import org.iana.rzm.conf.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.user.*;
import org.iana.test.spring.*;
import org.testng.annotations.*;

/**
 * org.iana.rzm.facade.common.auth.PasswordAuthenticatorTest
 *
 * @author Marcin Zajaczkowski
 * @author Piotr Tkaczyk
 */
@Test(groups = {"facade", "facade-common", "facade-auth"})
public class PasswordAuthenticatorTest extends RollbackableSpringContextTest {

    protected AuthenticationService authenticationServiceBean;
    protected AuthenticationService passwordAuthenticator;
    protected UserManager userManager;

    RZMUser adminUser;

    public PasswordAuthenticatorTest() {
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

    //Note: expectedExceptions cannot be used because that exception can be throw in multiple places (and in some of them it's ok)
    @Test
    public void testAuthenticateWithToken() throws Exception {

        PasswordAuth passwordAuth = new PasswordAuth("admin_login", "admin_password");

        try {
            //to get token for the next test
            authenticationServiceBean.authenticate(passwordAuth);

        } catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {

                SecurIDAuth securIDAuth = new SecurIDAuth("admin_login","admin_password");

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

        SecurIDAuth securIDAuth = new SecurIDAuth("admin_login","admin_password");

        //as a parametr to PasswordAuthenticator can be passed only PasswordAuth (not SecurIDAuth)
        passwordAuthenticator.authenticate(securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullData() throws Exception {

        passwordAuthenticator.authenticate(null);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullToken() throws Exception {

        SecurIDAuth securIDAuth = new SecurIDAuth("admin_login","admin_password");

        passwordAuthenticator.authenticate(null, securIDAuth);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class})
    public void testAuthenticateWithNullBoth() throws Exception {

        passwordAuthenticator.authenticate(null, null);
    }

    protected void cleanUp() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
