package org.iana.rzm.trans;

import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.trans.confirmation.UserConfirmation;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
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

    private long transactionId, transactionId2, transactionId3;

    private long ticketId, ticketId2;

    private ProcessDAO processDAO;
    private UserDAO userDAO;

    private TransactionManager manager;

    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private Set<Long> processes = new HashSet<Long>();
    private Set<String> users = new HashSet<String>();

    @BeforeClass(dependsOnGroups = {"JbpmTest"})
    public void init() throws Exception {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        manager = (TransactionManager) ctx.getBean("transactionManagerBean");
        userDAO = (UserDAO) ctx.getBean("userDAO");
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        processDAO.deploy(deployProcessDefinition());
        TransactionStatus txStatus;
        try {
            txStatus = txMgr.getTransaction(txDef);
        } finally {
            processDAO.close();
        }
        try {
            createUsers();
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    private void createUsers() {
        Set<RZMUser> userSet = new HashSet<RZMUser>();

        userSet.add(UserManagementTestUtil.createUser("sys1trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", true, true,
                        SystemRole.SystemType.AC)));
        userSet.add(UserManagementTestUtil.createUser("sys2trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", true, false,
                        SystemRole.SystemType.AC)));
        userSet.add(UserManagementTestUtil.createUser("sys3trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", false, false,
                        SystemRole.SystemType.TC)));
        userSet.add(UserManagementTestUtil.createUser("admin1trans", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userSet.add(UserManagementTestUtil.createUser("admin2trans", new AdminRole(AdminRole.AdminType.IANA)));
        userSet.add(UserManagementTestUtil.createUser("admin3trans", new AdminRole(AdminRole.AdminType.IANA)));

        for (RZMUser user : userSet) {
            users.add(user.getLoginName());
            userDAO.create(user);
        }
    }

    @Test
    public void testTransactionCreation() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            ProcessInstance pi = processDAO.newProcessInstance("Domain Modification Transaction (Unified Workflow)");
            processes.add(pi.getId());
            TransactionData td = new TransactionData();
            ticketId = 123L;
            td.setTicketID(ticketId);
            pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
            pi.signal();
            Transaction transaction = new Transaction(pi);
            transactionId = transaction.getTransactionID();

            pi = processDAO.newProcessInstance("Domain Modification Transaction (Unified Workflow)");
            processes.add(pi.getId());
            td = new TransactionData();
            ticketId2 = 124L;
            // PENDING_CONTACT_CONFIRMATION/reject transition authorized users
            StateConfirmations sc = new StateConfirmations();
            sc.addConfirmation(new UserConfirmation(userDAO.get("user-sys1trans")));
            td.setStateConfirmations(TransactionState.Name.PENDING_CONTACT_CONFIRMATION.name(), sc);
            td.setTicketID(ticketId2);
            pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
            pi.signal();
            Transaction transaction2 = new Transaction(pi);
            transactionId2 = transaction2.getTransactionID();

            pi = processDAO.newProcessInstance("Domain Modification Transaction (Unified Workflow)");
            processes.add(pi.getId());
            td = new TransactionData();
            long ticketId3 = 125L;
            td.setTicketID(ticketId3);
            // PENDING_CONTACT_CONFIRMATION/test transition authorized users and roles
            td.addTransitionConfirmation(TransactionState.Name.PENDING_CONTACT_CONFIRMATION.name(),
                    "test", new UserConfirmation(userDAO.get("user-sys1trans")));
            td.addTransitionConfirmation(TransactionState.Name.PENDING_CONTACT_CONFIRMATION.name(),
                    "test", new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)));
            td.addTransitionConfirmation(TransactionState.Name.PENDING_CONTACT_CONFIRMATION.name(),
                    "test", new RoleConfirmation(
                    UserManagementTestUtil.createSystemRole("transtestdomain", true, true,
                            SystemRole.SystemType.TC)));
            pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
            pi.signal();
            Transaction transaction3 = new Transaction(pi);
            transactionId3 = transaction3.getTransactionID();

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction transFromDB = manager.getTransaction(transactionId);
            assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
            Transaction transFromDB2 = manager.getTransaction(transactionId2);
            assert (transFromDB2 != null && transFromDB2.getTransactionID() == transactionId2 && transFromDB2.getTicketID().equals(new Long(ticketId2)));
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionCreation"})
    public void testTransactionUpdate() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction transToUpdate = manager.getTransaction(transactionId);
            assert transToUpdate.getTicketID().equals(new Long(ticketId));
            ticketId = 456L;
            transToUpdate.setTicketID(ticketId);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction transFromDB = manager.getTransaction(transactionId);
            assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionUpdate"})
    public void testTransactionAccept() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction trans = manager.getTransaction(transactionId);

            assert trans != null;
            trans.accept(null);

            System.out.println("State:" + trans.getState().getName());
            assert trans.getState().getName().equals(TransactionState.Name.COMPLETED);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionAccept"})
    public void testTransactionReject() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction trans = manager.getTransaction(transactionId2);
            assert trans != null;
            trans.reject(userDAO.get("user-sys1trans"));

            System.out.println("State:" + trans.getState().getName());
            assert trans.getState().getName().equals(TransactionState.Name.REJECTED);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionReject"},
          expectedExceptions = {UserNotAuthorizedToTransit.class})
    public void testTransactionTransitionFailed() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction trans = manager.getTransaction(transactionId3);
            assert trans != null;

            trans.transit(userDAO.get("user-sys2trans"), "test");
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionTransitionFailed"})
    public void testTransactionTransitionSuccessful() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            Transaction trans = manager.getTransaction(transactionId3);

            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION);

            trans.transit(userDAO.get("user-admin2trans"), "test");
            assert trans.getState().getName().equals(TransactionState.Name.ADMIN_CLOSE);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    private ProcessDefinition deployProcessDefinition() {
        return ProcessDefinition.parseXmlString(
                "<process-definition name='Domain Modification Transaction (Unified Workflow)'>\n" +
                        "   <start-state>\n" +
                        "       <transition to='PENDING_CONTACT_CONFIRMATION' />\n" +
                        "   </start-state>\n" +
                        "   <state name='PENDING_CONTACT_CONFIRMATION'>" +
                        "       <transition name='accept' to='COMPLETED' />" +
                        "       <transition name='reject' to='REJECTED' />" +
                        "       <transition name='test' to='ADMIN_CLOSE' />" +
                        "   </state>" +
                        "   <end-state name='COMPLETED' />" +
                        "   <end-state name='REJECTED' />" +
                        "   <end-state name='ADMIN_CLOSE' />" +
                        "</process-definition>"
        );
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            for (Long id : processes) {
                ProcessInstance pi = processDAO.getProcessInstance(id);
                if (pi != null) processDAO.delete(pi);
            }
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            for (String name : users) {
                RZMUser user = userDAO.get(name);
                if (user != null) userDAO.delete(user);
            }
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

}
