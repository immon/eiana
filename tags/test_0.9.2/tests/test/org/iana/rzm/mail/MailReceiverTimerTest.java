package org.iana.rzm.mail;

import org.iana.rzm.conf.DefinedTestProcess;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */

@Test(sequential = true, groups = "excluded")
public class MailReceiverTimerTest {

    private ProcessDAO processDAO;
    private long processId;
    private SchedulerThread schedulerThread;

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        schedulerThread = new SchedulerThread((JbpmConfiguration) ctx.getBean("jbpmConfiguration"));
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
        ProcessInstance processInstance = processDAO.newProcessInstance("Mails Receiver");
        processInstance.signal();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        processDAO.deleteAll();
    }
}
