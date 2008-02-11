package org.iana.rzm.mail;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.conf.DefinedMailReceiverProcess;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
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
 * This test requires use of fakeMailsProcessor in mails-receiver process configuration.
 * That's why it's excluded by default.
 *
 * @author Jakub Laszkiewicz
 */
@Test (groups = {"excluded"})
public class MailsProcessingActionTest {
    private PlatformTransactionManager txManager;
    private TransactionDefinition txDefinition = new DefaultTransactionDefinition();
    private TransactionManager transactionManager;
    private ProcessDAO processDAO;
    private UserManager userManager;
    private DomainManager domainManager;
    private Set<RZMUser> users = new HashSet<RZMUser>();
    private Domain domain;
    private Long domainTrId;
    private Long recTrId;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        txManager = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        transactionManager = (TransactionManager) appCtx.getBean("transactionManagerBean");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        userManager = (UserManager) appCtx.getBean("userManager");
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
            processDAO.deploy(DefinedMailReceiverProcess.getDefinition());

            RZMUser user = UserManagementTestUtil.createUser("sys1mailrec",
                    UserManagementTestUtil.createSystemRole("mailrecdomain", true, true,
                            SystemRole.SystemType.AC));
            user.setEmail("ac@no-mail.org");
            users.add(user);
            userManager.create(user);

            user = UserManagementTestUtil.createUser("sys2mailrec",
                    UserManagementTestUtil.createSystemRole("mailrecdomain", true, true,
                            SystemRole.SystemType.TC));
            user.setEmail("tc@no-mail.org");
            users.add(user);
            userManager.create(user);

            domain = new Domain("mailrecdomain");
            domainManager.create(domain);
            domainTrId = transactionManager.createDomainModificationTransaction(domain, null).getTransactionID();
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testProcessConfirmationMail() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = transactionManager.getTransaction(domainTrId);
            assert transaction != null;
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();

            ProcessInstance pi = processDAO.newProcessInstance(DefinedMailReceiverProcess.getProcessName());
            recTrId = pi.getId();
            Token token = pi.getRootToken();
            token.signal();
            assert token.getNode().getName().equals("RECEIVE");
            
            transaction = transactionManager.getTransaction(domainTrId);
            assert transaction != null;
            assert TransactionState.Name.PENDING_IMPACTED_PARTIES.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() throws Exception {
        for (Transaction trans : transactionManager.findAll())
            try {
                transactionManager.deleteTransaction(trans);
            } finally {
                processDAO.close();
            }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
