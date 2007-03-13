package org.iana.rzm.trans.dao.test;

import org.iana.rzm.trans.dao.TransactionDAO;
import org.iana.rzm.trans.JbpmTestContextFactory;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.Domain;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.def.ProcessDefinition;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"dao", "eiana-trans"})
public class TransactionDAOTest {
    private TransactionDAO dao;
    private Set<Long> domain1ProcIds = new HashSet<Long>();
    private Set<Long> domain2ProcIds = new HashSet<Long>();

    @BeforeClass
    public void init() throws InvalidNameException {
        dao = (TransactionDAO) new ClassPathXmlApplicationContext("eiana-trans-spring.xml").getBean("transactionDAO");
        deployProcessDefinition();
        generateTestData();
    }

    private void generateTestData() throws InvalidNameException {
        JbpmContext context = JbpmTestContextFactory.getJbpmContext();
        Domain domain1 = new Domain("testdomain1");
        context.getSession().save(domain1);

        ProcessInstance pi = createTransaction(context, 1L, domain1);
        domain1ProcIds.add(pi.getId());
        pi = createTransaction(context, 2L, domain1);
        domain1ProcIds.add(pi.getId());
        pi = createTransaction(context, 3L, domain1);
        domain1ProcIds.add(pi.getId());

        Domain domain2 = new Domain("testdomain2");
        context.getSession().save(domain2);

        pi = createTransaction(context, 11L, domain2);
        domain2ProcIds.add(pi.getId());
        pi = createTransaction(context, 12L, domain2);
        domain2ProcIds.add(pi.getId());
        pi = createTransaction(context, 13L, domain2);
        domain2ProcIds.add(pi.getId());

        context.close();
    }

    private ProcessInstance createTransaction(JbpmContext context, Long ticketId, Domain domain) {
        ProcessInstance pi = new ProcessInstance(context.getGraphSession().findLatestProcessDefinition("process trans test"));
        TransactionData td = new TransactionData();
        td.setTicketID(ticketId);
        td.setCurrentDomain(domain);
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.getContextInstance().setVariable("TRACK_DATA", new TrackData());
        pi.signal();
        Transaction transaction = new Transaction(pi);
        return pi;
    }

    @Test
    public void testFindAllProcessInstances() {
        List<ProcessInstance> dbDomain1Processes = dao.findAllProcessInstances("testdomain1");

        Set<Long> dbDomain1ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain1Processes) dbDomain1ProcIds.add(pi.getId());

        assert domain1ProcIds.equals(dbDomain1ProcIds);

        List<ProcessInstance> dbDomain2Processes = dao.findAllProcessInstances("testdomain2");

        Set<Long> dbDomain2ProcIds = new HashSet<Long>();
        for (ProcessInstance pi : dbDomain2Processes) dbDomain2ProcIds.add(pi.getId());

        assert domain2ProcIds.equals(dbDomain2ProcIds);
    }

    private void deployProcessDefinition() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                "<process-definition name='process trans test'>" +
                        "  <start-state name='PENDING_IANA_CONFIRMATION'>" +
                        "    <transition to='first' />" +
                        "  </start-state>" +
                        "   <task-node name='first'>" +
                        "    <task name='doSmth'></task>" +
                        "    <transition name='ok' to='COMPLETED' />" +
                        "    <transition name='reject' to='REJECTED' />" +
                        "  </task-node>" +
                        "  <end-state name='COMPLETED' />" +
                        "  <end-state name='REJECTED' />" +
                        "</process-definition>"
        );

        JbpmContext jbpmContext = JbpmTestContextFactory.getJbpmContext();
        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }
    }
}
