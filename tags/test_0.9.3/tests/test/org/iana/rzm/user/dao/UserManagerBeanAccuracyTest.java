package org.iana.rzm.user.dao;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.trans.UserManagementTestUtil;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"UserManagerBean", "dao", "eiana-users", "user"})
public class UserManagerBeanAccuracyTest extends RollbackableSpringContextTest {

    protected UserManager userManager;

    public UserManagerBeanAccuracyTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() {
//        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
//        userManager = (UserManager) ctx.getBean("userManager");
    }

    @Test
    public void testCreateUser() {
        RZMUser userCreated = UserManagementTestUtil.createUser("ivan123", UserManagementTestUtil.createSystemRole("Test", true, true, SystemRole.SystemType.AC));
        userManager.create(userCreated);
        RZMUser userRetrieved = userManager.get("user-ivan123");
        assert userRetrieved.getFirstName().equals("fnivan123");
        assert userRetrieved.getLastName().equals("lnivan123");
        assert userRetrieved.getLoginName().equals("user-ivan123");
    }

    @Test(dependsOnMethods = {"testCreateUser"})
    public void testGetUserById() throws Exception {
        RZMUser userRetrieved = userManager.get("user-ivan123");
        assert userRetrieved != null;
        assert userRetrieved.getFirstName().equals("fnivan123");
        assert userRetrieved.getLastName().equals("lnivan123");
        assert userRetrieved.getLoginName().equals("user-ivan123");
    }

    @Test(dependsOnMethods = {"testGetUserById"})
    public void testGetUserByName() throws Exception {
        RZMUser userRetrieved = userManager.get("user-ivan123");
        assert userRetrieved != null;
        assert userRetrieved.getFirstName().equals("fnivan123");
        assert userRetrieved.getLastName().equals("lnivan123");
        assert userRetrieved.getLoginName().equals("user-ivan123");
    }

    @Test(dependsOnMethods = {"testGetUserByName"})
    public void testFindUsersInRoles() throws Exception {
        userManager.create(UserManagementTestUtil.createUser("sys1", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("sys2", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("sys3", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
        userManager.create(UserManagementTestUtil.createUser("sys4", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
        userManager.create(UserManagementTestUtil.createUser("sys5", UserManagementTestUtil.createSystemRole("aaa", false, false, SystemRole.SystemType.TC)));
        userManager.create(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userManager.create(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));

        List<RZMUser> result = userManager.findUsersInSystemRole("aaa", SystemRole.SystemType.AC, true, true);
        assert result.size() == 1;
        RZMUser user = result.iterator().next();
        assert "user-sys1".equals(user.getLoginName());

        result = userManager.findUsersInSystemRole("aaa", SystemRole.SystemType.TC, true, false);
        assert result.size() == 2;
        Set<String> loginNames = new HashSet<String>();
        for (RZMUser u : result) loginNames.add(u.getLoginName());
        assert loginNames.contains("user-sys3") && loginNames.contains("user-sys4");

        result = userManager.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-admin1".equals(user.getLoginName());

        result = userManager.findUsersInAdminRole(AdminRole.AdminType.IANA);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-admin2".equals(user.getLoginName());
    }

    @Test(dependsOnMethods = {"testFindUsersInRoles"})
    public void testFindUserByEmailAndRole() throws Exception {
        userManager.create(UserManagementTestUtil.createUser("s1", "email1@example.email", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("s2", "email1@example.email", UserManagementTestUtil.createSystemRole("bbb", true, true, SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("s3", "email2@example.email", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.TC)));
        userManager.create(UserManagementTestUtil.createUser("s4", "email3@example.email", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.TC)));

        RZMUser sys1 = userManager.findUserByEmailAndRole("email1@example.email", "bbb");
        assert "email1@example.email".equals(sys1.getEmail()) && "user-s2".equals(sys1.getLoginName());
    }

    protected void cleanUp() throws Exception {
    }
}
