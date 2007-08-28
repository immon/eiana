package org.iana.rzm.trans.dao.test;

import org.iana.criteria.*;
import org.iana.dns.validator.InvalidDomainNameException;
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
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"dao", "eiana-trans"})
public class ProcessDAOTest extends TransactionalSpringContextTests {
    protected ProcessDAO processDAO;
    protected DomainDAO domainDAO;
    protected UserDAO userDAO;

    private Date date0, date1, date2;
    private Set<Long> domain1ProcIds = new HashSet<Long>();
    private Long domain1FirstProcId;
    private Set<Long> domain2ProcIds = new HashSet<Long>();
    private Long domain2FirstProcId;

    public ProcessDAOTest() {
        super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
            generateTestData();
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
            for (RZMUser user : userDAO.findAll())
                userDAO.delete(user);
            for (Domain domain : domainDAO.findAll())
                domainDAO.delete(domain);
        } finally {
            processDAO.close();
        }
    }

    private void generateTestData() throws InvalidDomainNameException, InterruptedException {
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

    /*
    @Test(dependsOnMethods = "findProcessInstancesByUserAndDomain")
    public void testTxRollback() throws Exception {
        try {
            Domain domain = new Domain("potestdomain3");
            domainDAO.create(domain);
            ProcessInstance pi = createTransaction(101L, domain);
            long piId = pi.getId();
            // rollback
            ProcessInstance dbPi = processDAO.getProcessInstance(piId);
            assert dbPi == null;
        } finally {
            processDAO.close();
        }
    }
    */

    @Test(dependsOnMethods = "findProcessInstancesByUserAndDomain")
    public void findProcessInstancesByState() throws Exception {
        try {
            ProcessCriteria criteria1 = new ProcessCriteria();
            criteria1.addState(TransactionState.Name.EXCEPTION.toString());
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            ProcessCriteria criteria2 = new ProcessCriteria();
            criteria2.addState(TransactionState.Name.PENDING_CREATION.toString());
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

    @Test(dependsOnMethods = "findProcessInstancesByProcDates")
    public void findProcessInstancesByDomainCriteria() throws Exception {
        try {
            Criterion criteria1 = new Equal("currentDomain.name.name", "potestdomain1");
            List<ProcessInstance> dbDomain1Processes = processDAO.find(criteria1);

            assert dbDomain1Processes != null;

            Set<Long> dbDomain1ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

            assert domain1ProcIds.equals(dbDomain1ProcIds);

            Criterion criteria2 = new Equal("currentDomain.name.name", "potestdomain2");
            List<ProcessInstance> dbDomain2Processes = processDAO.find(criteria2);

            assert dbDomain2Processes != null;

            Set<Long> dbDomain2ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

            assert domain2ProcIds.equals(dbDomain2ProcIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByDomainCriteria")
    public void findProcessInstancesByUserCriteria() throws Exception {
        try {
            Criterion criteria1 = new Equal("loginName", "user-posys1");
            List<ProcessInstance> dbDomain1Processes = processDAO.find(criteria1);

            assert dbDomain1Processes != null;

            Set<Long> dbDomain1ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

            assert domain1ProcIds.equals(dbDomain1ProcIds);

            Criterion criteria2 = new Equal("loginName", "user-posys2");
            List<ProcessInstance> dbDomain2Processes = processDAO.find(criteria2);

            assert dbDomain2Processes != null;

            Set<Long> dbDomain2ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

            assert domain2ProcIds.equals(dbDomain2ProcIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByUserCriteria")
    public void findProcessInstancesByUserAndDomainCriteria() throws Exception {
        try {
            List<Criterion> criteria1 = new ArrayList<Criterion>();
            criteria1.add(new Equal("loginName", "user-posys1"));
            criteria1.add(new Equal("currentDomain.name.name", "potestdomain1"));
            List<ProcessInstance> dbDomain1Processes = processDAO.find(new And(criteria1));

            assert dbDomain1Processes != null;

            Set<Long> dbDomain1ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

            assert domain1ProcIds.equals(dbDomain1ProcIds);

            List<Criterion> criteria2 = new ArrayList<Criterion>();
            criteria2.add(new Equal("loginName", "user-posys2"));
            criteria2.add(new Equal("currentDomain.name.name", "potestdomain2"));
            List<ProcessInstance> dbDomain2Processes = processDAO.find(new And(criteria2));

            assert dbDomain2Processes != null;

            Set<Long> dbDomain2ProcIds = new HashSet<Long>();
            for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

            assert domain2ProcIds.equals(dbDomain2ProcIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByUserAndDomainCriteria")
    public void findProcessInstancesByStateCriteria() throws Exception {
        try {
            Criterion criteria1 = new Equal("state",
                    TransactionState.Name.EXCEPTION.toString());
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            Criterion criteria2 = new Equal("state",
                    TransactionState.Name.PENDING_CREATION.toString());
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

    @Test(dependsOnMethods = "findProcessInstancesByStateCriteria")
    public void findProcessInstancesByTicketIdCriteria() throws Exception {
        try {
            Criterion criteria1 = new In("ticketId",
                    new HashSet(Arrays.asList(121L, 122L, 125L)));
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            Criterion criteria2 = new In("ticketId",
                    new HashSet(Arrays.asList(110L, 111L, 112L, 113L)));
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;

            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());

            assert domain2ProcIds.equals(processIds2);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByTicketIdCriteria")
    public void findProcessInstancesByProcessNameCriteria() throws Exception {
        try {
            Criterion criteria1 = new Equal("name", "Nonexistent Transaction (Unified Workflow)");
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.isEmpty();

            Criterion criteria2 = new Equal("name", DefinedTestProcess.getProcessName());
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

    @Test(dependsOnMethods = "findProcessInstancesByProcessNameCriteria")
    public void findProcessInstancesByCreatorsAndModifiersCriteria() throws Exception {
        try {
            List<Criterion> criteria0 = new ArrayList<Criterion>();
            criteria0.add(new Equal("createdBy", "101-creator"));
            criteria0.add(new Equal("modifiedBy", "102-modifier"));
            List<ProcessInstance> processes0 = processDAO.find(new And(criteria0));

            assert processes0 != null;
            assert processes0.isEmpty();

            Criterion criteria1 = new Equal("createdBy", "101-creator");
            List<ProcessInstance> processes1 = processDAO.find(criteria1);

            assert processes1 != null;
            assert processes1.size() == 1;
            assert processes1.iterator().next().getId() == domain1FirstProcId;

            List<Criterion> criteria2 = new ArrayList<Criterion>();
            criteria2.add(new Equal("createdBy", "111-creator"));
            criteria2.add(new Equal("modifiedBy", "111-modifier"));
            List<ProcessInstance> processes2 = processDAO.find(new And(criteria2));

            assert processes2 != null;
            assert processes2.size() == 1;
            assert processes2.iterator().next().getId() == domain2FirstProcId;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByCreatorsAndModifiersCriteria")
    public void findProcessInstancesByTrackDatesCriteria() throws Exception {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2006, 11, 15, 0, 0);
            Criterion criteria0 = new Lower("created", new Date(cal.getTimeInMillis()));
            List<ProcessInstance> processes0 = processDAO.find(criteria0);

            assert processes0 != null;
            assert processes0.isEmpty();

            List<Criterion> criteria1 = new ArrayList<Criterion>();
            cal.set(2006, 11, 15, 0, 0);
            criteria1.add(new Greater("created", new Date(cal.getTimeInMillis())));
            cal.set(2007, 0, 15, 0, 0);
            criteria1.add(new Lower("created", new Date(cal.getTimeInMillis())));
            List<ProcessInstance> processes1 = processDAO.find(new And(criteria1));

            assert processes1 != null;
            assert processes1.size() == 1;
            assert processes1.iterator().next().getId() == domain1FirstProcId;

            List<Criterion> criteria2 = new ArrayList<Criterion>();
            cal.set(2007, 8, 15, 0, 0);
            criteria2.add(new Greater("modified", new Date(cal.getTimeInMillis())));
            cal.set(2007, 9, 15, 0, 0);
            criteria2.add(new Lower("modified", new Date(cal.getTimeInMillis())));
            List<ProcessInstance> processes2 = processDAO.find(new And(criteria2));

            assert processes2 != null;
            assert processes2.size() == 1;
            assert processes2.iterator().next().getId() == domain2FirstProcId;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByTrackDatesCriteria")
    public void findProcessInstancesByProcDatesCriteria() throws Exception {
        try {
            Criterion criteria0 = new Lower("start", date0);
            List<ProcessInstance> processes0 = processDAO.find(criteria0);

            assert processes0 != null;
            assert processes0.isEmpty();

            List<Criterion> criteria1 = new ArrayList<Criterion>();
            criteria1.add(new Greater("start", date0));
            criteria1.add(new Lower("start", date1));
            List<ProcessInstance> processes1 = processDAO.find(new And(criteria1));

            assert processes1 != null;

            Set<Long> processIds1 = new HashSet<Long>();
            for (ProcessInstance pi : processes1) processIds1.add(pi.getId());

            assert domain1ProcIds.equals(processIds1);

            for (Long id : domain2ProcIds) {
                ProcessInstance pi = processDAO.getProcessInstance(id);
                pi.end();
            }

            Criterion criteria2 = new Greater("end", date2);
            List<ProcessInstance> processes2 = processDAO.find(criteria2);

            assert processes2 != null;

            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());

            assert domain2ProcIds.equals(processIds2);

            Criterion criteria3 = new Lower("end", date1);
            List<ProcessInstance> processes3 = processDAO.find(criteria3);

            assert processes3 != null;
            assert processes3.isEmpty();
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByProcDatesCriteria")
    public void findProcessInstancesByComplexCriteria() throws Exception {
        try {
            List<Criterion> criteria1 = new ArrayList<Criterion>();
            criteria1.add(new Greater("start", date0));
            criteria1.add(new Lower("start", date1));
            List<ProcessInstance> processes1 = processDAO.find(new Or(criteria1));

            assert processes1 != null;

            Set<Long> processIds1 = new HashSet<Long>();
            for (ProcessInstance pi : processes1) processIds1.add(pi.getId());

            Set<Long> allProcIds = new HashSet<Long>(domain1ProcIds);
            allProcIds.addAll(domain2ProcIds);
            assert allProcIds.equals(processIds1);

            for (Long id : domain2ProcIds) {
                ProcessInstance pi = processDAO.getProcessInstance(id);
                pi.end();
            }

            List<Criterion> criteria2 = new ArrayList<Criterion>();
            criteria2.add(new Equal("createdBy", "111-creator"));
            criteria2.add(new Equal("modifiedBy", "111-modifier"));
            List<Criterion> criteria21 = new ArrayList<Criterion>();
            criteria21.add(new Or(criteria2));
            criteria21.add(new Greater("end", date2));
            List<ProcessInstance> processes2 = processDAO.find(new And(criteria21));

            assert processes2 != null;
            assert processes2.size() == 1;
            assert processes2.iterator().next().getId() == domain2FirstProcId;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByComplexCriteria")
    public void countProcessInstancesByCriteria() throws Exception {
        try {
            Criterion criteria1 = new Equal("name", DefinedTestProcess.getProcessName());
            int count1 = processDAO.count(criteria1);
            assert count1 == 6;

            List<Criterion> criteria2 = new ArrayList<Criterion>();
            criteria2.add(new Equal("createdBy", "111-creator"));
            criteria2.add(new Equal("modifiedBy", "111-modifier"));
            criteria2.add(new Equal("currentDomain.name.name", "potestdomain1"));
            List<Criterion> criteria21 = new ArrayList<Criterion>();
            criteria21.add(new Or(criteria2));
            criteria21.add(new Greater("end", date2));
            int count2 = processDAO.count(new And(criteria21));
            assert count2 == 1;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "countProcessInstancesByCriteria")
    public void findProcessInstancesByCriteriaOffsetLimit() throws Exception {
        try {
            List<Criterion> criteria1 = new ArrayList<Criterion>();
            criteria1.add(new Greater("start", date0));
            criteria1.add(new Lower("start", date1));
            List<ProcessInstance> processes1 = processDAO.find(new Or(criteria1), 0, 3);
            assert processes1 != null;
            assert processes1.size() == 3;
            Set<Long> processIds1 = new HashSet<Long>();
            for (ProcessInstance pi : processes1) processIds1.add(pi.getId());
            List<ProcessInstance> processes2 = processDAO.find(new Or(criteria1), 3, 3);
            assert processes2 != null;
            assert processes2.size() == 3;
            Set<Long> processIds2 = new HashSet<Long>();
            for (ProcessInstance pi : processes2) processIds2.add(pi.getId());
            assert Collections.disjoint(processIds1, processIds2);
            Set<Long> allProcIds = new HashSet<Long>(domain1ProcIds);
            allProcIds.addAll(domain2ProcIds);
            processIds1.addAll(processIds2);
            assert allProcIds.equals(processIds1);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "findProcessInstancesByCriteriaOffsetLimit")
    public void findAndCountProcessInstancesByNullCriteria() throws Exception {
        try {
            List<ProcessInstance> processes1 = processDAO.find((Criterion) null);
            assert processes1 != null;
            assert processes1.size() == 6 : "Unexpected number of processes found: " + processes1.size();

            int count1 = processDAO.count(null);
            assert count1 == 6 : "Unexpected processes count: " + count1;
        } finally {
            processDAO.close();
        }
    }
}
