package org.iana.rzm.facade.admin;

import org.iana.criteria.Equal;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.admin.users.AdminRoleService;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.TestAuthenticatedUser;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.converter.RoleConverter;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.*;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "GuardedAdminRoleServiceTest"})
public class GuardedAdminRoleServiceTest {

    ApplicationContext appCtx;
    AdminRoleService gAdminRoleServ;

    UserManager userManager;
    RZMUser user, wrongUser;
    long roleId;

    final static String SYSTEM_ROLE = "newsystemrole";

    @BeforeClass
    public void init() {
        appCtx = SpringApplicationContext.getInstance().getContext();
        gAdminRoleServ = (AdminRoleService) appCtx.getBean("GuardedAdminRoleServiceBean");
        userManager = (UserManager) appCtx.getBean("userManager");

        user = new RZMUser();
        user.setLoginName("garsadminuser");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        this.userManager.create(user);

        wrongUser = new RZMUser();
        wrongUser.setLoginName("garswronguser");
        wrongUser.setFirstName("firstName");
        wrongUser.setLastName("lastName");
        wrongUser.setEmail("email@some.com");
        wrongUser.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        this.userManager.create(wrongUser);
    }

    @Test(expectedExceptions = {AccessDeniedException.class})
    public void testFacadeCreateRoleByWrongUser() {
        try {
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(wrongUser)).getAuthUser();
            gAdminRoleServ.setUser(testAuthUser);

            gAdminRoleServ.createRole(createTestSystemRole());
        } catch (AccessDeniedException e) {
            gAdminRoleServ.close();
            throw e;
        }
    }

    @Test (dependsOnMethods = {"testFacadeCreateRoleByWrongUser"})
    public void testFacadeCreateRole() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);

        roleId = gAdminRoleServ.createRole(createTestSystemRole());

        RoleVO retRole = gAdminRoleServ.getRole(roleId);
        assert retRole != null;
        assert retRole instanceof SystemRoleVO;

        assert ((SystemRoleVO)retRole).getName().equals(SYSTEM_ROLE);
        assert ((SystemRoleVO)retRole).getType().equals(SystemRoleVO.SystemType.AC);
        assert !retRole.isAdmin();
        assert !((SystemRoleVO)retRole).isAcceptFrom();
        assert !((SystemRoleVO)retRole).isMustAccept();

        gAdminRoleServ.close();

    }

    @Test (dependsOnMethods = "testFacadeCreateRole")
    public void testCount() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);

        assert gAdminRoleServ.count(new Equal("objId", roleId)) == 1;

        gAdminRoleServ.close();
    }

    @Test (dependsOnMethods = "testCount")
    public void testFacadeFindRoleByCriteria_Offset_Limit() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);

        List<RoleVO> roleVOs = gAdminRoleServ.find(new Equal("objId", roleId), 0, 10);
        assert roleVOs.size() == 1;
        assert ((SystemRoleVO)roleVOs.iterator().next()).getName().equals("newsystemrole");

        gAdminRoleServ.close();
    }

    @Test (dependsOnMethods = {"testFacadeFindRoleByCriteria_Offset_Limit"})
    public void testFacadeUpdateRole() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);

        RoleVO retRole = gAdminRoleServ.getRole(roleId);
        assert retRole != null;

        retRole.setType(SystemRoleVO.SystemType.TC);

        gAdminRoleServ.updateRole(retRole);

        retRole = gAdminRoleServ.getRole(roleId);
        assert retRole != null;
        assert retRole.getType().equals(SystemRoleVO.SystemType.TC);

        gAdminRoleServ.close();
    }

    @Test (dependsOnMethods = {"testFacadeUpdateRole"})
    public void testFacadeFindRole() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);
        List<RoleVO> retRolesVO = gAdminRoleServ.findRoles();

        assert !retRolesVO.isEmpty();

        gAdminRoleServ.close();
    }

    @Test (dependsOnMethods = {"testFacadeFindRole"})
    public void testFacadeFindRoleByCriteria() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);

        List<RoleVO> roleVOs = gAdminRoleServ.findRoles(new Equal("objId", roleId));
        assert roleVOs.size() == 1;
        assert roleVOs.iterator().next().getType().equals(SystemRoleVO.SystemType.TC);

        gAdminRoleServ.close();
    }

    @Test (dependsOnMethods = {"testFacadeFindRoleByCriteria"})
    public void testFacadeDeleteRole() {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminRoleServ.setUser(testAuthUser);

        gAdminRoleServ.deleteRole(roleId);

        gAdminRoleServ.close();
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
    }

    private RoleVO createTestSystemRole() {
        Role newRole = new SystemRole(SystemRole.SystemType.AC, SYSTEM_ROLE, false, false);
        return RoleConverter.convertRole(newRole);
    }

}