package org.iana.rzm.trans;

import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.conf.ConfirmationTestProcess;
import org.iana.rzm.trans.conf.TransactionTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.technicalcheck.CheckHelper;
import org.iana.ticketing.TicketingService;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author Jakub Laszkiewicz
 */
public class TestTransactionManagerBean extends TransactionManagerBean implements TestTransactionManager {
    private ProcessDAO processDAO;
    private TicketingService ticketingService;
    private DomainDAO domainDAO;

    public TestTransactionManagerBean(ProcessDAO processDAO, DomainDAO domainDAO, TicketingService ticketingService,
                                      DiffConfiguration diffConfig, NotificationManager notificationManager,
                                      NotificationSender notificationSender, CheckHelper technicalCheckHelper) {
        super(processDAO, domainDAO, ticketingService, diffConfig, notificationManager, notificationSender, technicalCheckHelper);
        this.processDAO = processDAO;
        this.ticketingService = ticketingService;
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
