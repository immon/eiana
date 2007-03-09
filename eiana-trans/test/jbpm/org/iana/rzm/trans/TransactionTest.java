package org.iana.rzm.trans;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManagerBean;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.JbpmTestContextFactory;
import org.iana.rzm.common.TrackData;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
@Test(groups={"eiana-trans"})
public class TransactionTest {

    private long transactionId;

    private long ticketId;

    @BeforeClass(groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
    public void init() {        
        deployProcessDefinition();
    }

    @Test(dependsOnGroups = "simple",groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
    public void testTransactionCreation() throws TransactionException {
        JbpmContext context = JbpmTestContextFactory.getJbpmContext();
        ProcessInstance pi = new ProcessInstance(context.getGraphSession().findLatestProcessDefinition("process trans test"));        
        TransactionData td = new TransactionData();
        ticketId = 123L;
        td.setTicketID(ticketId);
        pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
        pi.getContextInstance().setVariable("TRACK_DATA",new TrackData());
        pi.signal();
        Transaction transaction = new Transaction(pi);
        context.close();
        transactionId = transaction.getTransactionID();
        context = JbpmTestContextFactory.getJbpmContext();
        TransactionManagerBean manager = new TransactionManagerBean(context);
        Transaction transFromDB = manager.get(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
    }

    @Test(dependsOnMethods = ("testTransactionCreation"),groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
    public void testTranactionUpdate() throws NoSuchTransactionException {
        JbpmContext context = JbpmTestContextFactory.getJbpmContext();
        TransactionManagerBean manager = new TransactionManagerBean(context);
        Transaction transToUpdate = manager.get(transactionId);
        assert transToUpdate.getTicketID().equals(new Long(ticketId));
        ticketId = 456L;
        transToUpdate.setTicketID(ticketId);
        context.close();
        context = JbpmTestContextFactory.getJbpmContext();
        manager = new TransactionManagerBean(context);
        Transaction transFromDB = manager.get(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
        context.close();
    }

    @Test(dependsOnMethods = ("testTranactionUpdate"),groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
       public void testTranactionAccept() throws NoSuchTransactionException {
           JbpmContext context = JbpmTestContextFactory.getJbpmContext();
           TransactionManagerBean manager = new TransactionManagerBean(context);
           Transaction trans = manager.get(transactionId);
           assert trans != null;
           trans.accept();
           System.out.println("State:"+trans.getState().getName());
           assert trans.getState().getName().equals(TransactionState.Name.COMPLETED);           
       }

     @Test(dependsOnMethods = ("testTranactionUpdate"),groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
       public void testTranactionReject() throws NoSuchTransactionException {
           JbpmContext context = JbpmTestContextFactory.getJbpmContext();
           TransactionManagerBean manager = new TransactionManagerBean(context);
           Transaction trans = manager.get(transactionId);
           assert trans != null;
           trans.reject();
           System.out.println("State:"+trans.getState().getName());
           assert trans.getState().getName().equals(TransactionState.Name.REJECTED);           
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
