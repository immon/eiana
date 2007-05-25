package org.iana.rzm.trans.dao.test;

import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessCriteria;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"dao", "eiana-trans"})
public class ProcessDAOTest {
    private Date date0, date1, date2;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;
    private Set<Long> domain1ProcIds = new HashSet<Long>();
    private Long domain1FirstProcId;
    private Set<Long> domain2ProcIds = new HashSet<Long>();
    private Long domain2FirstProcId;
    private UserDAO userDAO;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringTransApplicationContext.getInstance().getContext();
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        userDAO = (UserDAO) appCtx.getBean("userDAO");
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
            generateTestData();
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        try {
            List<ProcessInstance> pis = processDAO.findAll();
            for (ProcessInstance pi : pis) {
                processDAO.delete(pi);
            }
        } finally {
            processDAO.close();
        }

        try {
            List<RZMUser> users = userDAO.findAll();
            for (RZMUser user : users) {
                userDAO.delete(user);
            }
        } finally {
            processDAO.close();
        }

        try {
            List<Domain> domains = domainDAO.findAll();
            for (Domain domain : domains) {
                domainDAO.delete(domain);
            }
        } finally {
            processDAO.close();
        }
    }

    private void generateTestData() throws InvalidNameException, InterruptedException {
        date0 = new Date();
        Thread.sleep(5000);

        Domain domain1 = new Domain("potestdomain1");
        domainDAO.create(domain1);

        ProcessInstance pi = createTransaction(101L, domain1);
        domain1FirstProcId = pi.getId();
        domain1ProcIds.add(pi.getId());
        pi = createTransaction(102L, domain1);
        domain1ProcIds.add(pi.getId());
        pi = createTransaction(103L, domain1);
        domain1ProcIds.add(pi.getId());

        Thread.sleep(5000);
        date1 = new Date();
        Thread.sleep(5000);

        Domain domain2 = new Domain("potestdomain2");
        domainDAO.create(domain2);

        pi = createTransaction(111L, domain2);
        domain2FirstProcId = pi.getId();
        domain2ProcIds.add(pi.getId());
        pi = createTransaction(112L, domain2);
        domain2ProcIds.add(pi.getId());
        pi = createTransaction(113L, domain2);
        domain2ProcIds.add(pi.getId());

        Thread.sleep(5000);
        date2 = new Date();

        userDAO.create(UserManagementTestUtil.createUser("posys1",
                UserManagementTestUtil.createSystemRole("potestdomain1", true, true,
                        SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("posys2",
                UserManagementTestUtil.createSystemRole("potestdomain2", true, true,
                        SystemRole.SystemType.AC)));
    }

    private ProcessInstance createTransaction(final Long ticketId, final Domain domain) {
        try {
            ProcessInstance pi = processDAO.newProcessInstance(DefinedTestProcess.getProcessName());
            TransactionData td = new TransactionData();
            td.setTicketID(ticketId);
            td.setCurrentDomain(domain);
            Calendar cal = Calendar.getInstance();
            cal.set(2007, (ticketId.intValue() - 100) % 7 - 1, 1, 12, 0);
            td.getTrackData().setCreated(new Timestamp(cal.getTimeInMillis()));
            cal.set(2007, (ticketId.intValue() - 100) % 7 + 5, 1, 12, 0);
            td.getTrackData().setModified(new Timestamp(cal.getTimeInMillis()));
            td.getTrackData().setCreatedBy(ticketId + "-creator");
            td.getTrackData().setModifiedBy(ticketId + "-modifier");
            pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
            pi.signal();
            processDAO.save(pi);
            return pi;
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void findProcessInstancesByDomain() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addDomainName("potestdomain1");
            List<ProcessInstance> dbDomain1Processes = processDAO.find(criteria1);

            assert dbDomain1Processes != null;

            Set<Long> dbDomain1ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

            assert domain1ProcIds.equals(dbDomain1ProcIds);

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addDomainName("potestdomain2");
            List<ProcessInstance> dbDomain2Processes = processDAO.find(criteria2);

            assert dbDomain2Processes != null;

            Set<Long> dbDomain2ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

            assert domain2ProcIds.equals(dbDomain2ProcIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByDomain")
    public void findProcessInstancesByUser() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addUserName("user-posys1");
            List<ProcessInstance> dbDomain1Processes = processDAO.find(criteria1);

            assert dbDomain1Processes != null;

            Set<Long> dbDomain1ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

            assert domain1ProcIds.equals(dbDomain1ProcIds);

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addUserName("user-posys2");
            List<ProcessInstance> dbDomain2Processes = processDAO.find(criteria2);

            assert dbDomain2Processes != null;

            Set<Long> dbDomain2ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

            assert domain2ProcIds.equals(dbDomain2ProcIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByUser")
    public void findProcessInstancesByUserAndDomain() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addUserName("user-posys1");
            criteria1.addDomainName("potestdomain1");
            List<ProcessInstance> dbDomain1Processes = processDAO.find(criteria1);

            assert dbDomain1Processes != null;

            Set<Long> dbDomain1ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

            assert domain1ProcIds.equals(dbDomain1ProcIds);

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addUserName("user-posys2");
            criteria2.addDomainName("potestdomain2");
            List<ProcessInstance> dbDomain2Processes = processDAO.find(criteria2);

            assert dbDomain2Processes != null;

            Set<Long> dbDomain2ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

            assert domain2ProcIds.equals(dbDomain2ProcIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByUserAndDomain")
    public void testTxRollback() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            Domain domain = new Domain("potestdomain3");
            domainDAO.create(domain);
            ProcessInstance pi = createTransaction(101L, domain);
            long piId = pi.getId();
            txMgr.rollback(txStatus);
            ProcessInstance dbPi = processDAO.getProcessInstance(piId);
            assert dbPi == null;
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testTxRollback")
    public void findProcessInstancesByState() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addState(TransactionState.Name.EXCEPTION.toString());
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addState(TransactionState.Name.PENDING_CONTACT_CONFIRMATION.toString());
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;

            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());

            Set<Long> allProcIds = new HashSet<Long>(domain1ProcIds);
            allProcIds.addAll(domain2ProcIds);

            assert allProcIds.equals(processIds2);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByState")
    public void findProcessInstancesByTicketId() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addTicketId(121L);
            criteria1.addTicketId(122L);
            criteria1.addTicketId(125L);
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addTicketId(110L);
            criteria2.addTicketId(111L);
            criteria2.addTicketId(112L);
            criteria2.addTicketId(113L);
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;

            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());

            assert domain2ProcIds.equals(processIds2);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByTicketId")
    public void findProcessInstancesByProcessName() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addProcessName("Nonexistent Transaction (Unified Workflow)");
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addProcessName(DefinedTestProcess.getProcessName());
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;

            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());

            Set<Long> allProcIds = new HashSet<Long>(domain1ProcIds);
            allProcIds.addAll(domain2ProcIds);

            assert allProcIds.equals(processIds2);

        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByProcessName")
    public void findProcessInstancesByCreatorsAndModifiers() throws Exception {
        try {
            ProcessCriteria criteria0 = new ProcessCriteria();
            criteria0.addCreator("101-creator");
            criteria0.addModifier("102-modifier");
            List<ProcessInstance> processes0 = processDAO.find(criteria0);

            assert processes0 != null;
            assert processes0.isEmpty();

            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addCreator("101-creator");
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.size() == 1;
            assert processes1.iterator().next().getId() == domain1FirstProcId;

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addCreator("111-creator");
            criteria2.addModifier("111-modifier");
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;
            assert processes2.size() == 1;
            assert processes2.iterator().next().getId() == domain2FirstProcId;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByCreatorsAndModifiers")
    public void findProcessInstancesByTrackDates() throws Exception {
        try {
            ProcessCriteria criteria0 = new ProcessCriteria();
            Calendar cal = Calendar.getInstance();
            cal.set(2006, 11, 15, 0, 0);
            criteria0.setCreatedBefore(new Date(cal.getTimeInMillis()));
            List<ProcessInstance> processes0 = processDAO.find(criteria0);

            assert processes0 != null;
            assert processes0.isEmpty();

            ProcessCriteria criteria1 = new ProcessCriteria();
            cal.set(2006, 11, 15, 0, 0);
            criteria1.setCreatedAfter(new Date(cal.getTimeInMillis()));
            cal.set(2007, 0, 15, 0, 0);
            criteria1.setCreatedBefore(new Date(cal.getTimeInMillis()));
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.size() == 1;
            assert processes1.iterator().next().getId() == domain1FirstProcId;

            ProcessCriteria criteria2 = new ProcessCriteria();
            cal.set(2007, 8, 15, 0, 0);
            criteria2.setModifiedAfter(new Date(cal.getTimeInMillis()));
            cal.set(2007, 9, 15, 0, 0);
            criteria2.setModifiedBefore(new Date(cal.getTimeInMillis()));
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;
            assert processes2.size() == 1;
            assert processes2.iterator().next().getId() == domain2FirstProcId;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByTrackDates")
    public void findProcessInstancesByProcDates() throws Exception {
        try {
            ProcessCriteria criteria0 = new ProcessCriteria();
            criteria0.setStartedBefore(date0);
            List<ProcessInstance> processes0 = processDAO.find(criteria0);

            assert processes0 != null;
            assert processes0.isEmpty();

            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.setStartedAfter(date0);
            criteria1.setStartedBefore(date1);
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;

            Set<Long> processIds1 = new HashSet<Long>();
            for (ProcessInstance pi : processes1) processIds1.add(pi.getId());

            assert domain1ProcIds.equals(processIds1);

            for (Long id : domain2ProcIds) {
                ProcessInstance pi = processDAO.getProcessInstance(id);
                pi.end();
            }

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.setFinishedAfter(date2);
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;

            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());

            assert domain2ProcIds.equals(processIds2);

            ProcessCriteria criteria3 = new ProcessCriteria();
            criteria3.setFinishedBefore(date1);
            List<ProcessInstance> processes3 = processDAO.find(criteria3);

            assert processes3 != null;
            assert processes3.isEmpty();
        } finally {
            processDAO.close();
        }
    }
}
