package org.iana.rzm.trans;

import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.TransactionTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"eiana-trans", "TransactionTest"})
public class TransactionTest {
    private PlatformTransactionManager txManager;
    private TransactionDefinition txDefinition = new DefaultTransactionDefinition();
    private long transactionId1, transactionId2, transactionId3;
    private long ticketId1;
    private ProcessDAO processDAO;
    private UserManager userManager;
    private TestTransactionManager transactionManager;
    private DomainManager domainManager;
    private Domain domain;
    private Set<Long> processes = new HashSet<Long>();
    private Set<String> users = new HashSet<String>();

    @BeforeClass(dependsOnGroups = {"JbpmTest"})
    public void init() throws Exception {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        txManager = (PlatformTransactionManager) ctx.getBean("transactionManager");
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        transactionManager = (TestTransactionManager) ctx.getBean("testTransactionManagerBean");
        userManager = (UserManager) ctx.getBean("userManager");
        domainManager = (DomainManager) ctx.getBean("domainManager");
        try {
            processDAO.deploy(TransactionTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
        createDomain();
        createUsers();
    }

    private void createDomain() {
        domain = new Domain("transtestdomain"); 
        domainManager.create(domain);
    }

    private void createUsers() {
        Set<RZMUser> userSet = new HashSet<RZMUser>();

        userSet.add(UserManagementTestUtil.createUser("sys1trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", true, true,
                        SystemRole.SystemType.AC)));
        userSet.add(UserManagementTestUtil.createUser("sys2trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", true, false,
                        SystemRole.SystemType.TC)));
        userSet.add(UserManagementTestUtil.createUser("sys3trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", false, false,
                        SystemRole.SystemType.TC)));
        userSet.add(UserManagementTestUtil.createUser("admin1trans", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userSet.add(UserManagementTestUtil.createUser("admin2trans", new AdminRole(AdminRole.AdminType.IANA)));

        for (RZMUser user : userSet) {
            users.add(user.getLoginName());
            userManager.create(user);
        }
    }

    @Test
    public void testTransactionCreation() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = transactionManager.createTransactionTestTransaction(domain);
            ticketId1 = 123L;
            transaction.setTicketID(ticketId1);
            transactionId1 = transaction.getTransactionID();
            processes.add(transactionId1);
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        Long ticketId2 = 456L;
        txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = transactionManager.createTransactionTestTransaction(domain);
            transaction.setTicketID(ticketId2);
            transactionId2 = transaction.getTransactionID();
            processes.add(transactionId2);
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }


        Long ticketId3 = 125L;
        txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = transactionManager.createTransactionTestTransaction(domain);
            transaction.setTicketID(ticketId3);
            transactionId3 = transaction.getTransactionID();
            processes.add(transactionId3);
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transFromDB = transactionManager.getTransaction(transactionId1);
            assert (transFromDB != null && transFromDB.getTransactionID() == transactionId1
                    && transFromDB.getTicketID().equals(new Long(ticketId1)));

            Transaction transFromDB2 = transactionManager.getTransaction(transactionId2);
            assert (transFromDB != null && transFromDB2.getTransactionID() == transactionId2
                    && transFromDB2.getTicketID().equals(new Long(ticketId2)));

            Transaction transFromDB3 = transactionManager.getTransaction(transactionId3);
            assert (transFromDB != null && transFromDB3.getTransactionID() == transactionId3
                    && transFromDB3.getTicketID().equals(new Long(ticketId3)));

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
        /*
        Transaction transFromDB2 = transactionManager.getTransaction(transactionId2);
        assert (transFromDB2 != null && transFromDB2.getTransactionID() == transactionId2 && transFromDB2.getTicketID().equals(new Long(ticketId2)));
        */
        //todo: lazy initialization error
    }

    @Test(dependsOnMethods = {"testTransactionCreation"})
    public void testTransactionUpdate() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transToUpdate = transactionManager.getTransaction(transactionId1);
            assert transToUpdate.getTicketID().equals(new Long(ticketId1));
            ticketId1 = 456L;
            transToUpdate.setTicketID(ticketId1);
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transFromDB = transactionManager.getTransaction(transactionId1);
            assert (transFromDB != null && transFromDB.getTransactionID() == transactionId1 && transFromDB.getTicketID().equals(new Long(ticketId1)));
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionUpdate"})
    public void testTransactionAccept() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction trans = transactionManager.getTransaction(transactionId1);
            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.accept(userManager.get("user-sys1trans"));
            trans.accept(userManager.get("user-sys2trans"));
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_IANA_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionAccept"})
    public void testTransactionReject() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction trans = transactionManager.getTransaction(transactionId2);
            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.reject(userManager.get("user-sys1trans"));
            assert trans.getState().getName().equals(TransactionState.Name.REJECTED)
                    : "unexpected state: " + trans.getState().getName();
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionReject"},
            expectedExceptions = {UserNotAuthorizedToTransit.class})
    public void testTransactionTransitionFailed() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction trans = transactionManager.getTransaction(transactionId3);
            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.transit(userManager.get("user-admin1trans"), "normal");
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionTransitionFailed"})
    public void testTransactionTransitionSuccessful() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction trans = transactionManager.getTransaction(transactionId3);

            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.accept(userManager.get("user-sys1trans"));
            trans.accept(userManager.get("user-sys2trans"));
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_IANA_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.transit(userManager.get("user-admin2trans"), "normal");
            assert trans.getState().getName().equals(TransactionState.Name.COMPLETED)
                    : "unexpected state: " + trans.getState().getName();
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
            for (Long id : processes)
                transactionManager.deleteTransaction(id);
            for (String name : users)
                userManager.delete(name);
            domainManager.delete(domain.getName());
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
