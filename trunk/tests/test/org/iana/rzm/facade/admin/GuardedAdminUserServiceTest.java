package org.iana.rzm.facade.admin;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.springframework.context.ApplicationContext;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.Not;
import org.iana.criteria.And;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "GuardedAdminUserServiceTest"})
public class GuardedAdminUserServiceTest {

    ApplicationContext appCtx;
    AdminUserService gAdminUserServ;

    UserManager userManager;
    RZMUser user, wrongUser;

    final static String USER_NAME = "gaustestuser";

    @BeforeClass
    public void init() {
        appCtx = SpringApplicationContext.getInstance().getContext();
        gAdminUserServ = (AdminUserService) appCtx.getBean("GuardedAdminUserServiceBean");
        userManager = (UserManager) appCtx.getBean("userManager");
 
        user = new RZMUser();
        user.setLoginName("gausadminuser");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        this.userManager.create(user);

        wrongUser = new RZMUser();
        wrongUser.setLoginName("gauswronguser");
        wrongUser.setFirstName("firstName");
        wrongUser.setLastName("lastName");
        wrongUser.setEmail("email@some.com");
        wrongUser.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        this.userManager.create(wrongUser);
    }

    @Test(expectedExceptions = {AccessDeniedException.class})
    public void testFacadeCreateUserByWrongUser() {
        try {
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(wrongUser)).getAuthUser();
            gAdminUserServ.setUser(testAuthUser);

            gAdminUserServ.createUser(createTestUser(USER_NAME));
        } catch (AccessDeniedException e) {
            gAdminUserServ.close();
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testFacadeCreateUserByWrongUser"})
    public void testFacadeCreateUser() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        gAdminUserServ.createUser(createTestUser(USER_NAME));

        UserVO retUserVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(retUserVO.getUserName());

        retUserVO = gAdminUserServ.getUser(retUserVO.getObjId());
        assert USER_NAME.equals(retUserVO.getUserName());
        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeCreateUser"})
    public void testFacadeFindUser() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);
        List<UserVO> retUsersVO = gAdminUserServ.findUsers();
        assert !retUsersVO.isEmpty();
        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeFindUser"})
    public void testFacadeUpdateUser() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);
        UserVO userVO = gAdminUserServ.getUser(USER_NAME);
        userVO.setEmail("new@email.com");
        gAdminUserServ.updateUser(userVO);

        userVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(userVO.getUserName());
        assert "new@email.com".equals(userVO.getEmail());
        
        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeUpdateUser"})
    public void testFacadeFindUserByCriteria() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(new Equal("email", "email@some.com"));
        criteria.add(new Not(new Equal("loginName", "gauswronguser")));

        List<UserVO> userVOs = gAdminUserServ.findUsers(new And(criteria));
        assert userVOs.size() == 1;
        assert userVOs.iterator().next().getUserName().equals("gausadminuser");

        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeFindUserByCriteria"})
    public void testFacadeDeleteUser() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        UserVO retUserVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(retUserVO.getUserName());
        gAdminUserServ.deleteUser(USER_NAME);

        gAdminUserServ.createUser(createTestUser(USER_NAME));
        retUserVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(retUserVO.getUserName());
        gAdminUserServ.deleteUser(retUserVO.getObjId());
        
        gAdminUserServ.close();
    }

    @AfterClass
    public void cleanUp() {
        userManager.delete(user);
        userManager.delete(wrongUser);
    }

    private UserVO createTestUser(String userName) {
        RZMUser user = new RZMUser();
        user.setLoginName(userName);
        return UserConverter.convert(user);
    }
}
