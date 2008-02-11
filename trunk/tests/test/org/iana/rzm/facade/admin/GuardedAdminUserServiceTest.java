package org.iana.rzm.facade.admin;

import org.iana.criteria.*;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        UserVO userVO = createTestUser(USER_NAME);
        userVO.addRole(new AdminRoleVO(AdminRoleVO.AdminType.IANA));
        String password = userVO.getPassword();
        MD5Password md5 = new MD5Password(password);
        gAdminUserServ.createUser(userVO);

        UserVO retUserVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(retUserVO.getUserName());

        retUserVO = gAdminUserServ.getUser(retUserVO.getObjId());
        assert USER_NAME.equals(retUserVO.getUserName());
        assert retUserVO.getRoles().size() == 1;
        assert md5.getPassword().equals(retUserVO.getPassword());

        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeCreateUser"})
    public void testCount() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        assert gAdminUserServ.count(new Equal("loginName", "gausadminuser")) == 1;

        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testCount"})
    public void testFacadeFindUserByCriteria_Offset_Limit() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        List<UserVO> retUserVOs = gAdminUserServ.find(new Equal("loginName", "gausadminuser"), 0, 5);
        assert retUserVOs.size() == 1;
        assert retUserVOs.iterator().next().getUserName().equals("gausadminuser");

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
    public void testFacadeUpdateUser_Password() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);
        UserVO userVO = gAdminUserServ.getUser(USER_NAME);
        String password = userVO.getPassword();
        assert !"newpassword".equals(password);
        userVO.setPassword("newpassword");
        gAdminUserServ.updateUser(userVO);

        userVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(userVO.getUserName());
        assert password.equals(userVO.getPassword());

        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeUpdateUser"})
    public void testFacadeUpdateUserSystemRole() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);
        UserVO userVO = gAdminUserServ.getUser(USER_NAME);
        userVO.setEmail("new@email.com");
        Set<RoleVO> newRoles = new HashSet<RoleVO>();
        SystemRoleVO systemRoleVO = new SystemRoleVO(SystemRoleVO.SystemType.AC);
        systemRoleVO.setName("new-role");
        newRoles.add(systemRoleVO);
        userVO.setRoles(newRoles);
        gAdminUserServ.updateUser(userVO);

        userVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(userVO.getUserName());
        assert "new@email.com".equals(userVO.getEmail());

        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeUpdateUserSystemRole"})
    public void testFacadeUpdateUserAdminRole() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);
        UserVO userVO = gAdminUserServ.getUser(USER_NAME);
        userVO.setEmail("new@email.com");
        Set<RoleVO> newRoles = new HashSet<RoleVO>();
        newRoles.add(new AdminRoleVO(AdminRoleVO.AdminType.GOV_OVERSIGHT));
        userVO.setRoles(newRoles);
        gAdminUserServ.updateUser(userVO);

        userVO = gAdminUserServ.getUser(USER_NAME);
        assert USER_NAME.equals(userVO.getUserName());
        assert "new@email.com".equals(userVO.getEmail());

        gAdminUserServ.close();
    }

    @Test(dependsOnMethods = {"testFacadeUpdateUserAdminRole"})
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

    @Test(dependsOnMethods = {"testFacadeUpdateUserAdminRole"})
    public void testFacadeFindUserByCriteriaOrderAndOffset() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        Equal equal = new Equal("loginName", "gauswronguser");

        List<UserVO> userVOs = gAdminUserServ.find(equal, new Order("loginName", true), 0,10 );
        assert userVOs.size() == 1;

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

    @Test(dependsOnMethods = {"testFacadeDeleteUser"})
    public void testFind() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminUserServ.setUser(testAuthUser);

        List<UserVO> result = gAdminUserServ.find(new Order("loginName"), 0, 1);

        assert result.size() == 1;
        assert "gausadminuser".equals(result.iterator().next().getUserName());

        gAdminUserServ.close();
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
    }

    private UserVO createTestUser(String userName) {
        RZMUser user = new RZMUser();
        user.setLoginName(userName);
        return UserConverter.convert(user);
    }
}
