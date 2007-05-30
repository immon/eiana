package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.TransactionStateLogEntry;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Date;
import java.util.List;
import java.util.Arrays;

/**
 * @author Jakub Laszkiewicz
 */
public class ProcessStateLogger implements ActionHandler {
    private static final String SYSTEM_NAME = "SYSTEM";
    private static final String CONTACT_NAME = "AC/TC";
    List<String> systemStates;
    List<String> contactStates;

    public void execute(ExecutionContext executionContext) throws Exception {
        TransactionManager transactionManager = (TransactionManager)
                executionContext.getJbpmContext().getObjectFactory().createObject("transactionManagerBean");
        Transaction transaction = transactionManager.getTransaction(executionContext.getProcessInstance().getId());
        TransactionState state = transaction.getState();
        state.setEnd(new Date());
        String userName;
        if (systemStates != null && systemStates.contains(state.getName().toString()))
            userName = SYSTEM_NAME;
        else if (contactStates != null && contactStates.contains(state.getName().toString()))
            userName = CONTACT_NAME;
        else
            userName = transaction.getTransactionData().getIdentityName();
        transaction.getTransactionData().addStateLogEntry(new TransactionStateLogEntry(state, userName));
    }
}
