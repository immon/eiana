package org.iana.rzm.trans;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManagerBean;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.JbpmTestContextFactory;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
@Test(groups={"eiana-trans"})
public class TransactionCreationTest {

    @Test
    public void testTransactionCreation() throws TransactionException {
        deployProcessDefinition();
        JbpmContext context = JbpmTestContextFactory.getJbpmContext();
        ProcessInstance pi = new ProcessInstance(context.getGraphSession().findLatestProcessDefinition("process creation test"));
        Transaction transaction = new Transaction(pi);
        context.close();

        context = JbpmTestContextFactory.getJbpmContext();
        TransactionManagerBean manager = new TransactionManagerBean(context);
        manager.get(transaction.getTransactionID());
    }

    
       private void deployProcessDefinition() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                "<process-definition name='process creation test'>" +
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

        JbpmContext jbpmContext = JbpmTestContextFactory.getJbpmContext();
        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }
    }
}
