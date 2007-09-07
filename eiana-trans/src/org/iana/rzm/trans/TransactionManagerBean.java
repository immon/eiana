package org.iana.rzm.trans;

import org.iana.criteria.Criterion;
import org.iana.objectdiff.ChangeDetector;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.dao.ProcessCriteria;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.technicalcheck.TechnicalCheckHelper;
import org.iana.rzm.user.RZMUser;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
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
    private DiffConfiguration diffConfiguration;
    private NotificationManager notificationManager;
    private NotificationSender notificationSender;

    public TransactionManagerBean(ProcessDAO processDAO, DomainDAO domainDAO, TicketingService ticketingService,
                                  DiffConfiguration diff, NotificationManager notificationManager,
                                  NotificationSender notificationSender) {
        this.processDAO = processDAO;
        this.ticketingService = ticketingService;
        this.domainDAO = domainDAO;
        this.diffConfiguration = diff;
        this.notificationManager = notificationManager;
        this.notificationSender = notificationSender;
    }

    public Transaction getTransaction(long id) throws NoSuchTransactionException {
        ProcessInstance pi = processDAO.getProcessInstance(id);
        if (pi == null || !DOMAIN_MODIFICATION_PROCESS.equals(pi.getProcessDefinition().getName()))
            throw new NoSuchTransactionException(id);
        Transaction transaction = new Transaction(pi);
        return transaction;
    }

    public Transaction createDomainCreationTransaction(Domain domain, boolean performTechnicalCheck) {
        if (performTechnicalCheck) {
            TechnicalCheckHelper.check(domain, notificationManager, notificationSender);
        }
        return createDomainCreationTransaction(domain);
    }

    public Transaction createDomainCreationTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        //todo: new RT ticket (simplified version)
        try {
            long ticketId = ticketingService.createTicket(domain.getName(), "");
            td.setTicketID(ticketId);
        } catch (TicketingException e) {
        }
        ObjectChange domainChange = (ObjectChange) ChangeDetector.diff(new Domain(domain.getName()), domain, diffConfiguration);
        td.setDomainChange(domainChange);
        ProcessInstance pi = processDAO.newProcessInstance(DOMAIN_MODIFICATION_PROCESS);
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.signal();
        return new Transaction(pi);
    }

    public Transaction createDomainModificationTransaction(Domain modifiedDomain) throws NoModificationException {
        return createTransaction(modifiedDomain, null);
    }

    public Transaction createDomainModificationTransaction(Domain modifiedDomain, String submitterEmail) throws NoModificationException {
        return createTransaction(modifiedDomain, submitterEmail);
    }

    public Transaction createDomainModificationTransaction(Domain modifiedDomain, boolean performTechnicalCheck) throws NoModificationException {
        if (performTechnicalCheck) {
            TechnicalCheckHelper.check(modifiedDomain, notificationManager, notificationSender);
        }
        return createTransaction(modifiedDomain, null);
    }

    public Transaction createDomainModificationTransaction(Domain modifiedDomain, String submitterEmail,
                                                           boolean performTechnicalCheck) throws NoModificationException {
        if (performTechnicalCheck) {
            TechnicalCheckHelper.check(modifiedDomain, submitterEmail, notificationManager, notificationSender);
        }
        return createTransaction(modifiedDomain, submitterEmail);
    }

    private Transaction createTransaction(Domain domain, String submitterEmail) throws NoModificationException {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainDAO.get(domain.getName()));
        //todo: new RT ticket (simplified version)
        try {
            long ticketId = ticketingService.createTicket(domain.getName(), "");
            td.setTicketID(ticketId);
        } catch (TicketingException e) {
        }
        ObjectChange domainChange = (ObjectChange) ChangeDetector.diff(td.getCurrentDomain(), domain, diffConfiguration);
        if (domainChange == null) throw new NoModificationException(domain.getName());
        td.setDomainChange(domainChange);
        td.setSubmitterEmail(submitterEmail);
        ProcessInstance pi = processDAO.newProcessInstance(DOMAIN_MODIFICATION_PROCESS);
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.signal();
        return new Transaction(pi);
    }

    public List<Transaction> findAll() {
        List<ProcessInstance> processInstances = processDAO.findAll();
        return toTransactions(processInstances);
    }

    public List<Transaction> find(Criterion criteria) {
        List<ProcessInstance> processInstances = processDAO.find(criteria);
        return toTransactions(processInstances);
    }

    public List<Transaction> find(Criterion criteria, int offset, int limit) {
        List<ProcessInstance> processInstances = processDAO.find(criteria, offset, limit);
        return toTransactions(processInstances);
    }

    public int count(Criterion criteria) {
        return processDAO.count(criteria);    
    }

    public List<Transaction> find(TransactionCriteria criteria) {
        ProcessCriteria processCriteria = TransactionToProcessCriteriaConverter.convert(criteria);
        List<ProcessInstance> processInstances = processDAO.find(processCriteria);
        return toTransactions(processInstances);
    }

    public List<Transaction> findTransactions(String domainName) {
        List<ProcessInstance> processInstances = processDAO.findAllProcessInstances(domainName);
        return toTransactions(processInstances);
    }

    public List<Transaction> findOpenTransactions(String domainName) {
        List<ProcessInstance> processInstances = processDAO.findOpenProcessInstances(domainName);
        return toTransactions(processInstances);
    }

    public List<Transaction> findOpenTransactions(Set<String> domainNames) {
        List<Transaction> ret = new ArrayList<Transaction>();
        if (domainNames != null) {
            for (String domainName : domainNames) {
                ret.addAll(findOpenTransactions(domainName));
            }
        }
        return ret;
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

    public List<Transaction> findTransactions(RZMUser user) {
        List<ProcessInstance> processInstances = processDAO.findAllProcessInstances(user);
        return toTransactions(processInstances);
    }

    public List<Transaction> findTransactions(RZMUser user, String domainName) {
        List<ProcessInstance> processInstances = processDAO.findAllProcessInstances(user, domainName);
        return toTransactions(processInstances);
    }

    public void deleteTransaction(Transaction transaction) throws NoSuchTransactionException {
        ProcessInstance pi = processDAO.getProcessInstance(transaction.getTransactionID());
        if (pi == null) throw new NoSuchTransactionException(transaction.getTransactionID());
        Domain domain = transaction.getCurrentDomain();
        processDAO.delete(pi);
        if (Domain.Status.NEW.equals(domain.getStatus())) domainDAO.delete(domain);
    }

    public void deleteTransaction(Long transactionId) throws NoSuchTransactionException {
        ProcessInstance pi = processDAO.getProcessInstance(transactionId);
        if (pi == null) throw new NoSuchTransactionException(transactionId);
        processDAO.delete(pi);
    }

    private List<Transaction> toTransactions(List<ProcessInstance> processInstances) {
        List<Transaction> result = new ArrayList<Transaction>();
        for (ProcessInstance pi : processInstances)
            if (DOMAIN_MODIFICATION_PROCESS.equals(pi.getProcessDefinition().getName()))
                result.add(new Transaction(pi));
        return result;
    }
}
