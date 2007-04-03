package org.iana.rzm.trans;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.change.ChangeDetector;
import org.iana.rzm.trans.change.DomainDiffConfiguration;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.ticketing.TicketingService;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionManagerBean implements TransactionManager {

    private static final String DOMAIN_MODIFICATION_PROCESS = "Domain Modification Transaction (Unified Workflow)";

    private ProcessDAO processDAO;
    private TicketingService ticketingService;
    private DomainDAO domainDAO;

    public TransactionManagerBean(ProcessDAO processDAO, DomainDAO domainDAO, TicketingService ticketingService) {
        this.processDAO = processDAO;
        this.ticketingService = ticketingService;
        this.domainDAO = domainDAO;
    }

    public Transaction getTransaction(long id) throws NoSuchTransactionException {
        ProcessInstance processInstances = processDAO.getProcessInstance(id);
        if (processInstances == null) throw new NoSuchTransactionException(id);
        Transaction transaction = new Transaction(processInstances);
        return transaction;
    }

    public Transaction createDomainCreationTransaction(Domain domain) {
        return null;
    }

    public Transaction createDomainModificationTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        td.setTicketID(ticketingService.generateID());
        td.setDomainChange((ObjectChange) ChangeDetector.diff(td.getCurrentDomain(), domain, DomainDiffConfiguration.getInstance()));
        ProcessInstance pi = processDAO.newProcessInstance(DOMAIN_MODIFICATION_PROCESS);
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.getContextInstance().setVariable("TRACK_DATA", new TrackData());
        return new Transaction(pi);
    }

    public List<Transaction> findAll() {
        return null;
    }

    public List<Transaction> find(TransactionCriteria criteria) {
        return null;
    }

    public List<Transaction> findTransactions(String domainName) {
        List<ProcessInstance> processInstances = processDAO.findAllProcessInstances(domainName);
        List<Transaction> result = new ArrayList<Transaction>();
        for (ProcessInstance pi : processInstances) result.add(new Transaction(pi));
        return result;
    }

    public List<Transaction> findTransactions(Set<String> domainNames) {
        List<Transaction> ret = new ArrayList<Transaction>();
        if (domainNames != null) {
            for (String domainName : domainNames) {
                ret.addAll(findTransactions(domainName));
            }
        }
        return ret;
    }
}