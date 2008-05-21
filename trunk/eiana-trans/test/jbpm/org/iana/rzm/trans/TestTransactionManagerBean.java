package org.iana.rzm.trans;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.conf.ConfirmationTestProcess;
import org.iana.rzm.trans.conf.TransactionTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.epp.mock.MockEPPClient;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Jakub Laszkiewicz
 */
public class TestTransactionManagerBean extends TransactionManagerBean implements TestTransactionManager {
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;


    public TestTransactionManagerBean(ProcessDAO processDAO, DomainManager domainManager, DiffConfiguration diffConfiguration, DomainDAO domainDAO) {
        super(processDAO, domainManager, diffConfiguration, new MockEPPClient());
        this.processDAO = processDAO;
        this.domainDAO = domainDAO;
    }

    public Transaction createTransactionTestTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        ProcessInstance pi = processDAO.newProcessInstance(TransactionTestProcess.getProcessName(), td);
        processDAO.signal(pi);
        return new Transaction(pi, processDAO);
    }

    public Transaction createConfirmationTestTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        ProcessInstance pi = processDAO.newProcessInstance(ConfirmationTestProcess.getProcessName(), td);
        processDAO.signal(pi);
        return new Transaction(pi, processDAO);
    }
}
