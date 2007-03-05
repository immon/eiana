package org.iana.rzm.trans.jbpm.test.accuracy;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManagerBean;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.jbpm.test.JbpmTestContextFactory;
import org.jbpm.JbpmContext;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionCreationTest {
    @Test
    public void testTransactionCreation() throws TransactionException {
        JbpmContext context = JbpmTestContextFactory.getJbpmContext();
        Transaction transaction = new Transaction("process creation test");
        context.close();

        context = JbpmTestContextFactory.getJbpmContext();
        TransactionManagerBean manager = new TransactionManagerBean(context);
        manager.get(transaction.getTransactionID());
    }
}
