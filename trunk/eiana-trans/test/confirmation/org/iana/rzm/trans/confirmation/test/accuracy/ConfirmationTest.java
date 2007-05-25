package org.iana.rzm.trans.confirmation.test.accuracy;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TestTransactionManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.conf.ConfirmationTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"confirmation", "eiana-trans"})
public class ConfirmationTest {
    private PlatformTransactionManager txManager;
    private TransactionDefinition txDefinition = new DefaultTransactionDefinition();
    private TestTransactionManager transactionManager;
    private ProcessDAO processDAO;
    private DomainManager domainManager;
    private UserManager userManager;
    private Set<RZMUser> users = new HashSet<RZMUser>();
    private Set<String> domains = new HashSet<String>();
    private Long transactionId;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringTransApplicationContext.getInstance().getContext();
        txManager = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        transactionManager = (TestTransactionManager) appCtx.getBean("testTransactionManagerBean");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        userManager = (UserManager) appCtx.getBean("userManager");
        try {
            processDAO.deploy(ConfirmationTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }

        users.add(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        users.add(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));
        users.add(UserManagementTestUtil.createUser("admin3", new AdminRole(AdminRole.AdminType.IANA)));

        for (RZMUser user : users)
            userManager.create(user);
    }

    private Transaction createTransaction(String suffix) throws CloneNotSupportedException {
        Set<RZMUser> systemUsers = new HashSet<RZMUser>();

        systemUsers.add(UserManagementTestUtil.createUser("sys1" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, true,
                        SystemRole.SystemType.AC)));
        systemUsers.add(UserManagementTestUtil.createUser("sys2" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, true,
                        SystemRole.SystemType.AC)));
        systemUsers.add(UserManagementTestUtil.createUser("sys3" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.AC)));
        systemUsers.add(UserManagementTestUtil.createUser("sys4" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.TC)));
        systemUsers.add(UserManagementTestUtil.createUser("sys5" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.TC)));
        systemUsers.add(UserManagementTestUtil.createUser("sys6" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, false, false,
                        SystemRole.SystemType.TC)));

        users.addAll(systemUsers);

        for (RZMUser user : systemUsers)
            userManager.create(user);

        Domain domain = new Domain("conftestdomain" + suffix);
        domainManager.create(domain);
        domains.add(domain.getName());
        return transactionManager.createConfirmationTestTransaction(domain);
    }

    @Test
    public void testContactConfirmations() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = createTransaction("contact");
            transactionId = transaction.getTransactionID();
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();

            transaction.accept(userManager.get("user-sys1contact"));
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();

            transaction.accept(userManager.get("user-sys4contact"));
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();

            RZMUser anotherUser = UserManagementTestUtil.createUser("sys7contact",
                    UserManagementTestUtil.createSystemRole("conftestdomaincontact", true, true,
                            SystemRole.SystemType.AC));
            users.add(anotherUser);
            userManager.create(anotherUser);

            transaction.accept(userManager.get("user-sys2contact"));
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();

            transaction.accept(userManager.get("user-sys7contact"));
            assert TransactionState.Name.PENDING_IANA_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testAdminConfirmations() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = transactionManager.getTransaction(transactionId);
            assert transaction != null;
            assert TransactionState.Name.PENDING_IANA_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();
            transaction.accept(userManager.get("user-admin2"));
            assert TransactionState.Name.COMPLETED.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            try {
                for (ProcessInstance pi : processDAO.findAll())
                    processDAO.delete(pi);
            } finally {
                processDAO.close();
            }
            for (RZMUser user : userManager.findAll())
                userManager.delete(user);
            for (Domain domain : domainManager.findAll())
                domainManager.delete(domain);
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }
}
