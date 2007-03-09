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
        ProcessInstance processInstances = graphSession.loadProcessInstance(id);
        if (processInstances == null) throw new NoSuchTransactionException(id);
        Transaction transaction = new Transaction(processInstances);
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
