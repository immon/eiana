package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.passwd.PasswordChangeService;
import org.iana.rzm.facade.system.domain.TestAuthenticatedUser;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class LoginTest {

    private AdminUserService userService;

    private AuthenticationService authService;

    private PasswordChangeService pwdChangeService;

    private UserManager userManager;

    @BeforeClass
    public void setUp() {
        try {
            ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
            userService = (AdminUserService) ctx.getBean("GuardedAdminUserServiceBean");
            pwdChangeService = (PasswordChangeService) ctx.getBean("passwordChangeService");
            authService = (AuthenticationService) ctx.getBean("authenticationServiceBean");
            userManager = (UserManager) ctx.getBean("userManager");

            // set up the authenticated user
            RZMUser admin = new RZMUser("an", "an", "aorg", "admin", "aemail", "apwd", false);
            admin.addRole(new AdminRole(AdminRole.AdminType.IANA));
            userManager.create(admin);
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(admin));
            userService.setUser(testAuthUser);

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

    @BeforeMethod
    public void initUser() {
        UserVO user = new UserVO();
        user.setUserName("username");
        user.setFirstName("first-name");
        user.setLastName("last-name");
        user.setPassword("password");
        user.addRole(new AdminRoleVO(AdminRoleVO.AdminType.IANA));
        userService.createUser(user);
    }

    @AfterMethod
    public void deleteUser() {
        userManager.delete("username");
    }

    @Test(expectedExceptions = PasswordExpiredException.class)
    public void testFirstLogin() throws Exception {
        AuthenticationData pwd = new PasswordAuth("username", "password");
        authService.authenticate(pwd);
    }

    @Test
    public void testLoginAfterChangingPassword() throws Exception {
        pwdChangeService.changePassword("username", "password", "password2", "password2");
        AuthenticationData pwd = new PasswordAuth("username", "password2");
        authService.authenticate(pwd);
    }

}
