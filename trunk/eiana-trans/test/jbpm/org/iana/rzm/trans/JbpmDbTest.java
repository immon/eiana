package org.iana.rzm.trans;

import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */

@Test(groups = {"accuracy", "eiana-trans", "jbpm"})
public class JbpmDbTest {
    private ProcessDAO processDAO;
    private long processId;
    private SchedulerThread schedulerThread;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        schedulerThread = new SchedulerThread((JbpmConfiguration) ctx.getBean("jbpmConfiguration"));
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        try {
            processDAO.deploy(getProcessDefinition());
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testSimplePersistence() throws Exception {
        //System.exit(0);
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            beginProcess();
            Thread.sleep(1001L);
            schedulerThread.executeTimers();
            finishProcess();
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    private ProcessDefinition getProcessDefinition() {
         return ProcessDefinition.parseXmlString(
                "<process-definition name='jbpm db test'>" +
                        "  <start-state name='start'>" +
                        "    <transition name='to-first' to='first' />" +
                        "  </start-state>" +
                        "  <state name='first'>" +
                        "    <timer name='first-time-out'" +
                        "           duedate='1 second'" +
                        "           transition='first2second' />" +
                        "    <transition name = 'first2second'" +
                        "                to='second' />" +
                        "  </state>" +
                        "  <state name='second'>" +
                        "    <transition to='end' />" +
                        "  </state>" +
                        "  <end-state name='end' />" +
                        "</process-definition>"
        );
    }

    private void beginProcess() throws InterruptedException {
        ProcessInstance processInstance = processDAO.newProcessInstance("jbpm db test");
        processId = processInstance.getId();
        Token token = processInstance.getRootToken();
        //assert "start".equals(token.getNode().getName()) : "unexpected state: " + token.getNode().getName();

        token.signal("to-first");
        assert "first".equals(token.getNode().getName()) : "unexpected state: " + token.getNode().getName();
    }

    private void finishProcess() {
        ProcessInstance processInstance = processDAO.getProcessInstance(processId);

        Token token = processInstance.getRootToken();
        assert "second".equals(token.getNode().getName());

        processInstance.signal();
        assert processInstance.hasEnded();
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            ProcessInstance pi = processDAO.getProcessInstance(processId);
            if (pi != null) processDAO.delete(pi);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }
}
