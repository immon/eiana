package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.*;
import org.iana.rzm.user.conf.SpringUsersApplicationContext;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
@Test (sequential=true, groups = {"UserManagerBean", "dao", "eiana-users", "user"})
public class UserManagerBeanAccuracyTest {
    UserManager userManager;
    Long        userId;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    Set<RZMUser> usersMap;

    @BeforeClass
    public void init() {
        ApplicationContext ctx = SpringUsersApplicationContext.getInstance().getContext();
        userManager = (UserManager) ctx.getBean("userManager");
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
    }

    @Test
    public void testCreateUser() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        RZMUser userCreated = UserManagementTestUtil.createUser("ivan123", UserManagementTestUtil.createSystemRole("Test", true, true, SystemRole.SystemType.AC));
        userManager.create(userCreated);
        userId = userCreated.getObjId();
        RZMUser userRetrieved = userManager.get(userId);
        assert userRetrieved.getFirstName().equals("fnivan123");
        assert userRetrieved.getLastName().equals("lnivan123");
        assert userRetrieved.getLoginName().equals("user-ivan123");
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testCreateUser"})
    public void testGetUserById() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            RZMUser userRetrieved = userManager.get(userId);
            assert userRetrieved != null;
            assert userRetrieved.getFirstName().equals("fnivan123");
            assert userRetrieved.getLastName().equals("lnivan123");
            assert userRetrieved.getLoginName().equals("user-ivan123");
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testGetUserById"})
    public void testGetUserByName() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            RZMUser userRetrieved = userManager.get("user-ivan123");
            assert userRetrieved != null;
            assert userRetrieved.getFirstName().equals("fnivan123");
            assert userRetrieved.getLastName().equals("lnivan123");
            assert userRetrieved.getLoginName().equals("user-ivan123");
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        }
    }

    @Test
    public void testFindUsersInRoles() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            usersMap = new HashSet<RZMUser>();
            usersMap.add(UserManagementTestUtil.createUser("sys1", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.AC)));
            usersMap.add(UserManagementTestUtil.createUser("sys2", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.AC)));
            usersMap.add(UserManagementTestUtil.createUser("sys3", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
            usersMap.add(UserManagementTestUtil.createUser("sys4", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
            usersMap.add(UserManagementTestUtil.createUser("sys5", UserManagementTestUtil.createSystemRole("aaa", false, false, SystemRole.SystemType.TC)));
            usersMap.add(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
            usersMap.add(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));

            for(RZMUser user : usersMap)
                userManager.create(user);

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

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        }
    }

    @Test
    public void testFindUserByEmailAndRole() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            userManager.create(UserManagementTestUtil.createUser("s1", "email1@example.email", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.AC)));
            userManager.create(UserManagementTestUtil.createUser("s2", "email1@example.email", UserManagementTestUtil.createSystemRole("bbb", true, true, SystemRole.SystemType.AC)));
            userManager.create(UserManagementTestUtil.createUser("s3", "email2@example.email", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.TC)));
            userManager.create(UserManagementTestUtil.createUser("s4", "email3@example.email", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.TC)));

            RZMUser sys1 = userManager.findUserByEmailAndRole("email1@example.email", "bbb");
            assert "email1@example.email".equals(sys1.getEmail()) && "user-s2".equals(sys1.getLoginName());

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            for (RZMUser user : userManager.findAll())
               userManager.delete(user);
            
            userManager.delete("user-ivan123");
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        }
    }
}
