package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.jbpm.db.GraphSession;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionManagerBean implements TransactionManager {
    private JbpmContext context;


    public TransactionManagerBean(JbpmContext context) {
        this.context = context;
    }

    public Transaction get(long id) throws NoSuchTransactionException {
        GraphSession graphSession = context.getGraphSession();
        List processInstances =
                graphSession.findProcessInstances(id);
        if (processInstances == null || processInstances.isEmpty()) throw new NoSuchTransactionException(id);
        ProcessInstance pi = (ProcessInstance) processInstances.get(0);
        Transaction transaction = new Transaction(pi.getProcessDefinition().getName());
        return transaction;
    }

    public Transaction create(Domain domain) {
        return null;
    }

    public List<Transaction> findAll() {
        return null;
    }

    public List<Transaction> find(TransactionCriteria criteria) {
        return null;
    }
}
