package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.*;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
@Test (sequential=true, groups = {"UserManagerBean", "dao", "eiana-users"})
public class UserManagerBeanAccuracyTest {
    UserManager manager;
    Long        userId;
    UserDAO dao;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();

    @BeforeClass
    public void init() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("eiana-users-spring.xml");
        manager = (UserManager) ctx.getBean("userManager");
        dao = (UserDAO) ctx.getBean("userDAO");
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
    }

    @Test
    public void testCreateUser() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
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
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testCreateUser"})
    public void testGetUserById() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        RZMUser userRetrived = manager.get(userId);
        assert userRetrived != null;
        assert userRetrived.getRoles().size() == 1;
        Role role = userRetrived.getRoles().iterator().next();
        assert role instanceof AdminRole;
        AdminRole ar = (AdminRole) role;
        assert AdminRole.AdminType.GOV_OVERSIGHT.equals(ar.getType());
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testGetUserById"})
    public void testGetUserByName() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        RZMUser userRetrived = manager.get("ivan123");
        assert userRetrived != null;
        assert userRetrived.getRoles().size() == 1;
        Role role = userRetrived.getRoles().iterator().next();
        assert role instanceof AdminRole;
        AdminRole ar = (AdminRole) role;
        assert AdminRole.AdminType.GOV_OVERSIGHT.equals(ar.getType());
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
        txMgr.commit(txStatus);
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
