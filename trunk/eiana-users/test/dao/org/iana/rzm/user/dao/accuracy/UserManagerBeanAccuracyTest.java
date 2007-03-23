package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.*;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.user.dao.UserDAO;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Piotr Tkaczyk
 */
@Test (sequential=true, groups = {"UserManagerBean", "dao", "eiana-users"})
public class UserManagerBeanAccuracyTest {
    UserManager manager;
    Long        userId;
    UserDAO dao;

    @BeforeClass
    public void init() {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("eiana-users-spring.xml");
        manager = (UserManager) ctx.getBean("userManager");
        dao = (UserDAO) ctx.getBean("userDAO");
    }

    @Test
    public void testCreateUser() {
        RZMUser userCreated = new RZMUser();
        userCreated.setFirstName("Ivan");
        userCreated.setLastName("Delphin");
        userCreated.setLoginName("ivan123");
        userCreated.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        manager.create(userCreated);
        userId = userCreated.getObjId();
        RZMUser userRetrieved = manager.get(userId);

        assert userRetrieved.getFirstName().equals("Ivan");
        assert userRetrieved.getLastName().equals("Delphin");
    }

    @Test(dependsOnMethods = {"testCreateUser"})
    public void testGetUserById() throws Exception {
        RZMUser userRetrived = manager.get(userId);
        assert userRetrived != null;
        assert userRetrived.getRoles().size() == 1;
        Role role = userRetrived.getRoles().iterator().next();
        assert role instanceof AdminRole;
        AdminRole ar = (AdminRole) role;
        assert AdminRole.AdminType.GOV_OVERSIGHT.equals(ar.getTrackData());
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
    }

    @Test(dependsOnMethods = {"testGetUserById"})
    public void testGetUserByName() throws Exception {
        RZMUser userRetrived = manager.get("ivan123");
        assert userRetrived != null;
        assert userRetrived.getRoles().size() == 1;
        Role role = userRetrived.getRoles().iterator().next();
        assert role instanceof AdminRole;
        AdminRole ar = (AdminRole) role;
        assert AdminRole.AdminType.GOV_OVERSIGHT.equals(ar.getTrackData());
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
    }

    @Test
    public void testFindConfirmingUsers() {
        dao.create(UserManagementTestUtil.createUser("sys1", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.AC)));
        dao.create(UserManagementTestUtil.createUser("sys2", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.AC)));
        dao.create(UserManagementTestUtil.createUser("sys3", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
        dao.create(UserManagementTestUtil.createUser("sys4", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
        dao.create(UserManagementTestUtil.createUser("sys5", UserManagementTestUtil.createSystemRole("aaa", false, false, SystemRole.SystemType.TC)));
        dao.create(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        dao.create(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));

        List<RZMUser> result = manager.findUsersRequiredToConfirm("aaa", SystemRole.SystemType.AC);
        assert result.size() == 1;
        RZMUser user = result.iterator().next();
        assert "user-sys1".equals(user.getLoginName());

        result = manager.findUsersEligibleToConfirm("aaa", SystemRole.SystemType.TC);
        assert result.size() == 2;
        Set<String> loginNames = new HashSet<String>();
        for (RZMUser u : result) loginNames.add(u.getLoginName());
        assert loginNames.contains("user-sys3") && loginNames.contains("user-sys4");
    }
}
