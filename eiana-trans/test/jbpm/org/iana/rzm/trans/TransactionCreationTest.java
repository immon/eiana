package org.iana.rzm.trans;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManagerBean;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.jbpm.test.JbpmTestContextFactory;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionCreationTest {

    @Test(groups = {"jbpm", "eiana-trans"})
    public void testTransactionCreation() throws TransactionException {
        deployProcessDefinition();
        System.out.println("DEPLOY: OK !");
        JbpmContext context = JbpmTestContextFactory.getJbpmContext();
        Transaction transaction = new Transaction("jbpm db test");
        System.out.println("Transaction created: OK !");
        context.close();
        System.out.println("Cxt close: OK !");
        context = JbpmTestContextFactory.getJbpmContext();
        System.out.println("Cxt open: OK !");
        TransactionManagerBean manager = new TransactionManagerBean(context);
        System.out.println("Transaction manager created : OK !");
        System.out.println("id="+transaction.getTransactionID());
        transaction = manager.get(transaction.getTransactionID());
        TransactionState ts = transaction.getState();
        System.out.println("Stan transakcji:"+ts.getName());

    }

    private void deployProcessDefinition() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                "<process-definition name='jbpm db test'>" +
                        "  <start-state name='PENDING_EVALUATION'>" +
                        "    <transition to='first' />" +
                        "  </start-state>" +
                        "  <state name='PENDING_TECH_CHECK'>" +
                        "    <timer name='first-time-out'" +
                        "           duedate='1 second'" +
                        "           transition='first2second' />" +
                        "    <transition name = 'first2second'" +
                        "                to='second' />" +
                        "  </state>" +
                        "  <state name='second'>" +
                        "    <transition to='end' />" +
                        "  </state>" +
                        "  <end-state name='PENDING_IANA_CHECK' />" +
                        "</process-definition>"
        );

        JbpmContext jbpmContext = org.iana.rzm.trans.JbpmContextFactory.getJbpmContext();

        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }
    }
}
