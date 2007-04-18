package org.iana.rzm.trans;

import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.confirmation.UserConfirmation;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import java.util.Set;
import java.util.HashSet;

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
    private TransactionStatus globalTxStatus;

    @BeforeClass(dependsOnGroups = {"JbpmTest"})
    public void init() {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        manager = (TransactionManager) ctx.getBean("transactionManagerBean");
        userDAO = (UserDAO) ctx.getBean("userDAO");
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        processDAO.deploy(deployProcessDefinition());
        createUsers();
        processDAO.close();
        txMgr.commit(txStatus);
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
    public void testTransactionCreation() throws TransactionException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

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
        td.addTransitionConfirmation(TransactionState.Name.PENDING_CONTACT_CONFIRMATION.name(),
                "reject", new UserConfirmation(userDAO.get("user-sys1trans")));
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

        processDAO.close();
        txMgr.commit(txStatus);
        txStatus = txMgr.getTransaction(txDef);

        Transaction transFromDB = manager.getTransaction(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
        Transaction transFromDB2 = manager.getTransaction(transactionId2);
        assert (transFromDB2 != null && transFromDB2.getTransactionID() == transactionId2 && transFromDB2.getTicketID().equals(new Long(ticketId2)));

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testTransactionCreation"})
    public void testTransactionUpdate() throws NoSuchTransactionException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Transaction transToUpdate = manager.getTransaction(transactionId);
        assert transToUpdate.getTicketID().equals(new Long(ticketId));
        ticketId = 456L;
        transToUpdate.setTicketID(ticketId);
        processDAO.close();
        txMgr.commit(txStatus);
        txStatus = txMgr.getTransaction(txDef);

        Transaction transFromDB = manager.getTransaction(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testTransactionUpdate"})
    public void testTransactionAccept() throws TransactionException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Transaction trans = manager.getTransaction(transactionId);

        assert trans != null;
        trans.accept(null);

        System.out.println("State:" + trans.getState().getName());
        assert trans.getState().getName().equals(TransactionState.Name.COMPLETED);

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testTransactionAccept"})
    public void testTransactionReject() throws TransactionException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Transaction trans = manager.getTransaction(transactionId2);
        assert trans != null;
        trans.reject(userDAO.get("user-sys1trans"));

        System.out.println("State:" + trans.getState().getName());
        assert trans.getState().getName().equals(TransactionState.Name.REJECTED);

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = {"testTransactionReject"},
          expectedExceptions = {UserNotAuthorizedToTransit.class})
    public void testTransactionTransitionFailed() throws TransactionException {
        globalTxStatus = txMgr.getTransaction(txDef);

        Transaction trans = manager.getTransaction(transactionId3);
        assert trans != null;

        trans.transit(userDAO.get("user-sys2trans"), "test");
    }

    @Test(dependsOnMethods = {"testTransactionTransitionFailed"})
    public void testTransactionTransitionSuccessful() throws TransactionException {
        Transaction trans = manager.getTransaction(transactionId3);

        assert trans != null;
        assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION);

        trans.transit(userDAO.get("user-admin2trans"), "test");
        assert trans.getState().getName().equals(TransactionState.Name.ADMIN_CLOSE);

        processDAO.close();
        txMgr.commit(globalTxStatus);
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
    public void cleanUp() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        for (Long id : processes)
            processDAO.delete(processDAO.getProcessInstance(id));
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        for (String name : users)
            userDAO.delete(userDAO.get(name));
        txMgr.commit(txStatus);
    }

}
