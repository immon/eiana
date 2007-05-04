package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.ticketing.TicketingService;
import org.iana.objectdiff.DiffConfiguration;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Jakub Laszkiewicz
 */
public class TestTransactionManagerBean extends TransactionManagerBean implements TestTransactionManager {
    private static final String TRANSACTION_TEST_PROCESS = "Transaction Test";
    private static final String CONFIRAMTION_TEST_PROCESS = "Confirmation Test";

    private ProcessDAO processDAO;
    private TicketingService ticketingService;
    private DomainDAO domainDAO;

    public TestTransactionManagerBean(ProcessDAO processDAO, DomainDAO domainDAO, TicketingService ticketingService, DiffConfiguration diffConfig) {
        super(processDAO, domainDAO, ticketingService, diffConfig);
        this.processDAO = processDAO;
        this.ticketingService = ticketingService;
        this.domainDAO = domainDAO;
    }

    public Transaction createTransactionTestTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        td.setTicketID(ticketingService.generateID());
        ProcessInstance pi = processDAO.newProcessInstance(TRANSACTION_TEST_PROCESS);
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.signal();
        return new Transaction(pi);
    }
    
    public Transaction createConfirmationTestTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        td.setTicketID(ticketingService.generateID());
        ProcessInstance pi = processDAO.newProcessInstance(CONFIRAMTION_TEST_PROCESS);
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.signal();
        return new Transaction(pi);
    }
}
