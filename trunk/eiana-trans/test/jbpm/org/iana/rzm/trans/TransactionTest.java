package org.iana.rzm.trans;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.SpringApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
@Test(groups={"eiana-trans"})
public class TransactionTest {

    private long transactionId;

    private long ticketId;

    private ProcessDAO procesDAO;

    private TransactionManager manager;

    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    TransactionStatus txStatus;

    @BeforeClass(groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
    public void init() {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        procesDAO = (ProcessDAO) ctx.getBean("processDAO");
        manager = (TransactionManager) ctx.getBean("transactionManagerBean");
        procesDAO.deploy(deployProcessDefinition());
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        txStatus = txMgr.getTransaction(txDef);
    }

    @Test(dependsOnGroups = "simple",groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
    public void testTransactionCreation() throws TransactionException {
        ProcessInstance pi = procesDAO.newProcessInstance("Domain Modification Transaction (Unified Workflow)");
        TransactionData td = new TransactionData();
        ticketId = 123L;
        td.setTicketID(ticketId);
        pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
        pi.getContextInstance().setVariable("TRACK_DATA",new TrackData());
        pi.signal();
        Transaction transaction = new Transaction(pi);
        //context.close();
        transactionId = transaction.getTransactionID();
        //context = procesDAO.getContext();
        //manager.setJBPMContext(context);
        Transaction transFromDB = manager.getTransaction(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
    }

    @Test(dependsOnMethods = ("testTransactionCreation"),groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
    public void testTransactionUpdate() throws NoSuchTransactionException {
        //JbpmContext context = procesDAO.getContext();
        //manager.setJBPMContext(context);
        Transaction transToUpdate = manager.getTransaction(transactionId);
        assert transToUpdate.getTicketID().equals(new Long(ticketId));
        ticketId = 456L;
        transToUpdate.setTicketID(ticketId);
        //context.close();
        //context = procesDAO.getContext();
        //manager.setJBPMContext(context);
        Transaction transFromDB = manager.getTransaction(transactionId);
        assert (transFromDB != null && transFromDB.getTransactionID() == transactionId && transFromDB.getTicketID().equals(new Long(ticketId)));
        //context.close();
       // procesDAO.close();
    }

    @Test(dependsOnMethods = ("testTransactionUpdate"),groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
       public void testTransactionAccept() throws TransactionException {
           //JbpmContext context = procesDAO.getContext();
           //manager.setJBPMContext(context);
           Transaction trans = manager.getTransaction(transactionId);
            
           assert trans != null;
           trans.accept(null);
           System.out.println("State:"+trans.getState().getName());
           assert trans.getState().getName().equals(TransactionState.Name.COMPLETED);           
       }

     @Test(dependsOnMethods = ("testTransactionUpdate"),groups = {"accuracy", "eiana-trans", "jbpm","transaction"})
       public void testTransactionReject() throws NoSuchTransactionException {
           //JbpmContext context = procesDAO.getContext();
           //manager.setJBPMContext(context);
           Transaction trans = manager.getTransaction(transactionId);
           assert trans != null;
           trans.reject();
           System.out.println("State:"+trans.getState().getName());
           assert trans.getState().getName().equals(TransactionState.Name.REJECTED);           
       }


       private ProcessDefinition deployProcessDefinition() {
        return ProcessDefinition.parseXmlString(
                "<process-definition name='Domain Modification Transaction (Unified Workflow)'>\n" +
                        "    <start-state>\n" +
                        "        <transition to='first' />\n" +
                        "    </start-state>\n" +
                        "   <task-node name='first'>" +
                            "    <task name='doSmth'></task>" +
                            "    <transition name='ok' to='COMPLETED' />" +
                            "    <transition name='reject' to='REJECTED' />" +
                            "  </task-node>" +
                        "  <end-state name='COMPLETED' />" +
                        "  <end-state name='REJECTED' />" +
                        "</process-definition>"
        );
    }

    @AfterClass
    public void finish() {

       // procesDAO.close();
    }

}
