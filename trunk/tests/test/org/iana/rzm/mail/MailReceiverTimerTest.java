package org.iana.rzm.mail;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.conf.SpringApplicationContext;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MailReceiverTimerTest {

    private ProcessDAO processDAO;
    private long processId;
    private SchedulerThread schedulerThread;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        schedulerThread = new SchedulerThread((JbpmConfiguration) ctx.getBean("jbpmConfiguration"));
        try {
            processDAO.deploy(getProcessDefinition());
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testMailAction() throws Exception {
        beginProcess();
        Thread.sleep(1001L);
        schedulerThread.executeTimers();
    }

    private ProcessDefinition getProcessDefinition() {
        return DefinedTestProcess.getDefinition(DefinedTestProcess.MAILS_RECEIVER);
    }

    private void beginProcess() throws InterruptedException {
        try {
            ProcessInstance processInstance = processDAO.newProcessInstance("Mails Receiver");
            processInstance.signal();
        } finally {
            processDAO.close();
        }
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() throws Exception {
        try {
            List<ProcessInstance> processInstances = processDAO.findAll();
            for (ProcessInstance processInstance : processInstances)
                processDAO.delete(processInstance);
        } finally {
            processDAO.close();
        }
    }
}
