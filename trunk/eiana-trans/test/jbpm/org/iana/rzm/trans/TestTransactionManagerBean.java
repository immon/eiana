package org.iana.rzm.trans;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.conf.ConfirmationTestProcess;
import org.iana.rzm.trans.conf.TransactionTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Jakub Laszkiewicz
 */
public class TestTransactionManagerBean extends TransactionManagerBean implements TestTransactionManager {
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;

    public TestTransactionManagerBean(ProcessDAO processDAO, DomainDAO domainDAO,
                                      DiffConfiguration diffConfig) {
        super(processDAO, domainDAO, diffConfig);
        this.processDAO = processDAO;
        this.domainDAO = domainDAO;
    }

    public Transaction createTransactionTestTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        ProcessInstance pi = processDAO.newProcessInstance(TransactionTestProcess.getProcessName());
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.signal();
        return new Transaction(pi);
    }

    public Transaction createConfirmationTestTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        ProcessInstance pi = processDAO.newProcessInstance(ConfirmationTestProcess.getProcessName());
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.signal();
        return new Transaction(pi);
    }
}
