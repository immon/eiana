package org.iana.rzm.facade.admin;

import org.iana.criteria.*;
import org.iana.rzm.conf.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.user.*;
import org.iana.rzm.facade.user.converter.*;
import org.iana.rzm.facade.admin.users.AdminUserService;
import org.iana.rzm.user.*;
import org.springframework.context.*;
import org.testng.annotations.*;

import java.util.*;

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
