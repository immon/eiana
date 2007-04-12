package org.iana.rzm.trans.dao.test;

import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
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
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"dao", "eiana-trans"})
public class ProcessDAOTest {
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private TransactionStatus txStatus;
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;
    private Set<Long> domain1ProcIds = new HashSet<Long>();
    private Set<Long> domain2ProcIds = new HashSet<Long>();
    private UserDAO userDAO;

    @BeforeClass
    public void init() throws InvalidNameException {
        ApplicationContext appCtx = SpringTransApplicationContext.getInstance().getContext();
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        userDAO = (UserDAO) appCtx.getBean("userDAO");
        txStatus = txMgr.getTransaction(txDef);
        deployProcessDefinition();
        generateTestData();
        processDAO.close();
        txMgr.commit(txStatus);
    }

    /*
    @AfterClass
    public void destroy() {
        domainDAO.delete()
    }
    */

    private void generateTestData() throws InvalidNameException {
        Domain domain1 = new Domain("potestdomain1");
        domainDAO.create(domain1);

        ProcessInstance pi = createTransaction(1L, domain1);
        domain1ProcIds.add(pi.getId());
        pi = createTransaction(2L, domain1);
        domain1ProcIds.add(pi.getId());
        pi = createTransaction(3L, domain1);
        domain1ProcIds.add(pi.getId());

        Domain domain2 = new Domain("potestdomain2");
        domainDAO.create(domain2);

        pi = createTransaction(11L, domain2);
        domain2ProcIds.add(pi.getId());
        pi = createTransaction(12L, domain2);
        domain2ProcIds.add(pi.getId());
        pi = createTransaction(13L, domain2);
        domain2ProcIds.add(pi.getId());

        userDAO.create(UserManagementTestUtil.createUser("sys1",
                UserManagementTestUtil.createSystemRole("potestdomain1", true, true,
                        SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys2",
                UserManagementTestUtil.createSystemRole("potestdomain2", true, true,
                        SystemRole.SystemType.AC)));
    }

    private ProcessInstance createTransaction(final Long ticketId, final Domain domain) {
        try {
            ProcessInstance pi = processDAO.newProcessInstance(DefinedTestProcess.getProcessName());
            TransactionData td = new TransactionData();
            td.setTicketID(ticketId);
            td.setCurrentDomain(domain);
            pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
            Transaction transaction = new Transaction(pi);
            processDAO.save(pi);
            return pi;
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testFindAllProcessInstancesByDomain() {
        txStatus = txMgr.getTransaction(txDef);

        List<ProcessInstance> dbDomain1Processes = processDAO.findAllProcessInstances("potestdomain1");

        Set<Long> dbDomain1ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

        assert domain1ProcIds.equals(dbDomain1ProcIds);

        List<ProcessInstance> dbDomain2Processes = processDAO.findAllProcessInstances("potestdomain2");

        Set<Long> dbDomain2ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

        assert domain2ProcIds.equals(dbDomain2ProcIds);

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = "testFindAllProcessInstancesByDomain")
    public void testFindAllProcessInstancesByUser() {
        txStatus = txMgr.getTransaction(txDef);

        List<ProcessInstance> dbDomain1Processes = processDAO.findAllProcessInstances(userDAO.get("user-sys1"));

        Set<Long> dbDomain1ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

        assert domain1ProcIds.equals(dbDomain1ProcIds);

        List<ProcessInstance> dbDomain2Processes = processDAO.findAllProcessInstances(userDAO.get("user-sys2"));

        Set<Long> dbDomain2ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

        assert domain2ProcIds.equals(dbDomain2ProcIds);

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = "testFindAllProcessInstancesByUser")
    public void testFindAllProcessInstancesByUserAndDomain() {
        txStatus = txMgr.getTransaction(txDef);

        List<ProcessInstance> dbDomain1Processes = processDAO.findAllProcessInstances(userDAO.get("user-sys1"), "potestdomain1");

        Set<Long> dbDomain1ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

        assert domain1ProcIds.equals(dbDomain1ProcIds);

        List<ProcessInstance> dbDomain2Processes = processDAO.findAllProcessInstances(userDAO.get("user-sys2"), "potestdomain2");

        Set<Long> dbDomain2ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

        assert domain2ProcIds.equals(dbDomain2ProcIds);

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = "testFindAllProcessInstancesByUserAndDomain")
    public void testTxRollback() {
        txStatus = txMgr.getTransaction(txDef);
        Domain domain = new Domain("potestdomain3");
        domainDAO.create(domain);
        ProcessInstance pi = createTransaction(1L, domain);
        long piId = pi.getId();
        txMgr.rollback(txStatus);
        ProcessInstance dbPi = processDAO.getProcessInstance(piId);
        assert dbPi == null;
        processDAO.close();
    }

    private void deployProcessDefinition() {
        processDAO.deploy(DefinedTestProcess.getDefinition());
    }
}
