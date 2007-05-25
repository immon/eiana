package org.iana.rzm.trans;

import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */

@Test(groups = {"accuracy", "eiana-trans", "jbpm"})
public class JbpmDbTest {
    private ProcessDAO processDAO;
    private long processId;
    private SchedulerThread schedulerThread;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        schedulerThread = new SchedulerThread((JbpmConfiguration) ctx.getBean("jbpmConfiguration"));
        try {
            processDAO.deploy(getProcessDefinition());
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testSimplePersistence() throws Exception {
        beginProcess();
        Thread.sleep(1001L);
        schedulerThread.executeTimers();
        finishProcess();
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
        try {
            ProcessInstance processInstance = processDAO.newProcessInstance("jbpm db test");
            processId = processInstance.getId();
            Token token = processInstance.getRootToken();
            assert "start".equals(token.getNode().getName()) : "unexpected state: " + token.getNode().getName();

            token.signal("to-first");
            assert "first".equals(token.getNode().getName()) : "unexpected state: " + token.getNode().getName();
        } finally {
            processDAO.close();
        }
    }

    private void finishProcess() {
        try {
            ProcessInstance processInstance = processDAO.getProcessInstance(processId);

            Token token = processInstance.getRootToken();
            assert "second".equals(token.getNode().getName());

            processInstance.signal();
            assert processInstance.hasEnded();
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
    }
}
