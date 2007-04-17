package org.iana.rzm.trans;

import org.jbpm.JbpmConfiguration;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
    TransactionStatus txStatus;

    @BeforeClass
    public void init() {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        txStatus = txMgr.getTransaction(txDef);
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        processDAO.deploy(getProcessDefinition());
        schedulerThread = new SchedulerThread((JbpmConfiguration) ctx.getBean("jbpmConfiguration"));
    }

    @Test
    public void testSimplePersistence() throws InterruptedException {
        beginProcess();
        Thread.sleep(1001L);
        schedulerThread.executeTimers();
        finishProcess();
    }

    private ProcessDefinition getProcessDefinition() {
         return ProcessDefinition.parseXmlString(
                "<process-definition name='jbpm db test'>" +
                        "  <start-state name='start'>" +
                        "    <transition to='first' />" +
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
        assert "start".equals(token.getNode().getName());

        token.signal();
        assert "first".equals(token.getNode().getName());
        processDAO.close();
    }

    private void finishProcess() {
        ProcessInstance processInstance = processDAO.getProcessInstance(processId);

        Token token = processInstance.getRootToken();
        assert "second".equals(token.getNode().getName());

        processInstance.signal();
        assert processInstance.hasEnded();
        processDAO.close();
    }

    @AfterClass
    public void cleanUp() {
        processDAO.delete(processDAO.getProcessInstance(processId));
        processDAO.close();
        txMgr.commit(txStatus);
    }
}
