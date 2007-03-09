package org.iana.rzm.trans;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmDbTest {
    private static final String jbpmConfigurationXml =
            "<jbpm-configuration>" +
                    "  <jbpm-context>" +
                    "    <service name='persistence' " +
                    "             factory='org.jbpm.persistence.db.DbPersistenceServiceFactory' />" +
                    "    <service name='scheduler'" +
                    "             factory='org.jbpm.scheduler.db.DbSchedulerServiceFactory' />" +
                    "  </jbpm-context>" +
                    "  <string name='resource.hibernate.cfg.xml' " +
                    "          value='eiana-trans.jbpm.hibernate.cfg.xml' />" +
                    "  <string name='resource.business.calendar' " +
                    "          value='org/jbpm/calendar/jbpm.business.calendar.properties' />" +
                    "  <string name='resource.default.modules' " +
                    "          value='org/jbpm/graph/def/jbpm.default.modules.properties' />" +
                    "  <string name='resource.converter' " +
                    "          value='org/jbpm/db/hibernate/jbpm.converter.properties' />" +
                    "  <string name='resource.action.types' " +
                    "          value='org/jbpm/graph/action/action.types.xml' />" +
                    "  <string name='resource.node.types' " +
                    "          value='org/jbpm/graph/node/node.types.xml' />" +
                    "  <string name='resource.varmapping' " +
                    "          value='org/jbpm/context/exe/jbpm.varmapping.xml' />" +
                    "</jbpm-configuration>";

    private JbpmConfiguration jbpmConfiguration =
            JbpmConfiguration.parseXmlString(jbpmConfigurationXml);

    private SchedulerThread schedulerThread = new SchedulerThread(jbpmConfiguration);
    
    @Test
    public void testSimplePersistence() throws InterruptedException {
        deployProcessDefinition();
        beginProcess();
        Thread.sleep(1001L);
        schedulerThread.executeTimers();
        finishProcess();
    }

    private void deployProcessDefinition() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
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

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }
    }

    private void beginProcess() throws InterruptedException {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            GraphSession graphSession = jbpmContext.getGraphSession();

            ProcessDefinition processDefinition =
                    graphSession.findLatestProcessDefinition("jbpm db test");

            ProcessInstance processInstance =
                    new ProcessInstance(processDefinition);

            Token token = processInstance.getRootToken();
            assert "start".equals(token.getNode().getName());

            token.signal();
            assert "first".equals(token.getNode().getName());

            jbpmContext.save(processInstance);
        } finally {
            jbpmContext.close();
        }
    }

    private void finishProcess() {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            GraphSession graphSession = jbpmContext.getGraphSession();

            ProcessDefinition processDefinition =
                    graphSession.findLatestProcessDefinition("jbpm db test");

            List processInstances =
                    graphSession.findProcessInstances(processDefinition.getId());

            ProcessInstance processInstance =
                    (ProcessInstance) processInstances.get(0);

            Token token = processInstance.getRootToken();
            assert "second".equals(token.getNode().getName());

            processInstance.signal();
            assert processInstance.hasEnded();

            jbpmContext.save(processInstance);
        } finally {
            jbpmContext.close();
        }
    }
}
