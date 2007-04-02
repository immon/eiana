package org.iana.rzm.trans;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential=true, groups={"eiana-trans", "TransactionTest"})
public class TransactionTest {

    private long transactionId, transactionId2;

    private long ticketId, ticketId2;

    private ProcessDAO procesDAO;

    private TransactionManager manager;

    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    TransactionStatus txStatus;

    @BeforeClass (dependsOnGroups = {"JbpmTest"})
    public void init() {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        procesDAO = (ProcessDAO) ctx.getBean("processDAO");
        manager = (TransactionManager) ctx.getBean("transactionManagerBean");
        procesDAO.deploy(deployProcessDefinition());
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        txStatus = txMgr.getTransaction(txDef);
    }

    @Test
    public void testTransactionCreation() throws TransactionException {
        ProcessInstance pi = procesDAO.newProcessInstance("Domain Modification Transaction (Unified Workflow)");
        TransactionData td = new TransactionData();
        ticketId = 123L;
        td.setTicketID(ticketId);
        pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
        pi.getContextInstance().setVariable("TRACK_DATA",new TrackData());
        pi.signal();
        Transaction transaction = new Transaction(pi);
        transactionId = transaction.getTransactionID();

        pi = procesDAO.newProcessInstance("Domain Modification Transaction (Unified Workflow)");
        td = new TransactionData();
        ticketId2 = 124L;
        td.setTicketID(ticketId2);
        pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
        pi.getContextInstance().setVariable("TRACK_DATA",new TrackData());
        pi.signal();
        Transaction transaction2 = new Transaction(pi);
        transactionId2  = transaction2.getTransactionID();
        
        procesDAO.close();
        
        Transaction transFromDB = manager.getTransaction(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
        Transaction transFromDB2 = manager.getTransaction(transactionId2);
        assert (transFromDB2 != null && transFromDB2.getTransactionID() == transactionId2 && transFromDB2.getTicketID().equals(new Long(ticketId2)));

    }

    @Test(dependsOnMethods = {"testTransactionCreation"})
    public void testTransactionUpdate() throws NoSuchTransactionException {
        Transaction transToUpdate = manager.getTransaction(transactionId);
        assert transToUpdate.getTicketID().equals(new Long(ticketId));
        ticketId = 456L;
        transToUpdate.setTicketID(ticketId);
        procesDAO.close();
        Transaction transFromDB = manager.getTransaction(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
    }

    @Test(dependsOnMethods = {"testTransactionUpdate"})
       public void testTransactionAccept() throws TransactionException {
           Transaction trans = manager.getTransaction(transactionId);

           assert trans != null;
           trans.accept(null);
           System.out.println("State:"+trans.getState().getName());
           assert trans.getState().getName().equals(TransactionState.Name.COMPLETED);
       }

     @Test(dependsOnMethods = {"testTransactionAccept"})
      public void testTransactionReject() throws TransactionException {
           Transaction trans = manager.getTransaction(transactionId2);
           assert trans != null;
           trans.reject(null);
           System.out.println("State:"+trans.getState().getName());
           assert trans.getState().getName().equals(TransactionState.Name.REJECTED);
     }


       private ProcessDefinition deployProcessDefinition() {
        return ProcessDefinition.parseXmlString(
                "<process-definition name='Domain Modification Transaction (Unified Workflow)'>\n" +
                        "   <start-state>\n" +
                        "       <transition to='first' />\n" +
                        "   </start-state>\n" +
                        "   <state name='first'>" +
                        "       <transition name='accept' to='COMPLETED' />" +
                        "       <transition name='reject' to='REJECTED' />" +
                        "   </state>" +
                        "   <end-state name='COMPLETED' />" +
                        "   <end-state name='REJECTED' />" +
                        "</process-definition>"
        );
    }

    @AfterClass
    public void finish() {
        procesDAO.close();
        txMgr.commit(txStatus);
    }

}
