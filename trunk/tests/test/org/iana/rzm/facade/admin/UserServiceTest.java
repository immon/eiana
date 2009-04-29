package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.system.domain.TestAuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
public class UserServiceTest {

    private AdminUserService userService;

    private AuthenticationService authService;

    private UserManager userManager;

    @BeforeClass
    public void setUp() {
        try {
            ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
            userService = (AdminUserService) ctx.getBean("GuardedAdminUserServiceBean");
            authService = (AuthenticationService) ctx.getBean("authenticationServiceBean");
            userManager = (UserManager) ctx.getBean("userManager");

            // set up the authenticated user
            RZMUser admin = new RZMUser("an", "an", "aorg", "admin", "aemail", "apwd", false);
            admin.addRole(new AdminRole(AdminRole.AdminType.IANA));
            userManager.create(admin);
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(admin));
            userService.setUser(testAuthUser);

            UserVO user = new UserVO();
            user.setUserName("username-firstlogin");
            user.setFirstName("first-name");
            user.setLastName("last-name");
            user.setPassword("password");
            userService.createUser(user);
        } catch (BeansException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AfterClass
    public void cleanUp() {
        userManager.deleteAll();
    }

    @Test(expectedExceptions = PasswordExpiredException.class)
    public void testChangePasswordOnTheFirstLogin() throws Exception {
        AuthenticationData pwd = new PasswordAuth("username-firstlogin", "password");
        authService.authenticate(pwd);
    }


}
