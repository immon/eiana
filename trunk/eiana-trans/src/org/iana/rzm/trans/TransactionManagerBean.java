package org.iana.rzm.trans;

import org.iana.criteria.Criterion;
import org.iana.epp.EPPClient;
import org.iana.objectdiff.ChangeDetector;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import static org.iana.rzm.common.validators.CheckTool.checkNull;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 */
public class TransactionManagerBean implements TransactionManager {

    private static final String DOMAIN_MODIFICATION_PROCESS = "Domain Modification Transaction (Unified Workflow)";

    private ProcessDAO processDAO;

    private DomainManager domainManager;

    private DiffConfiguration diffConfiguration;

    private EPPClient eppClient;

    public TransactionManagerBean(ProcessDAO processDAO, DomainManager domainManager, DiffConfiguration diffConfiguration, EPPClient eppClient) {
        checkNull(processDAO, "process dao");
        checkNull(domainManager, "domain manager");
        checkNull(diffConfiguration, "diff configuration");
        checkNull(eppClient, "epp client");
        this.processDAO = processDAO;
        this.domainManager = domainManager;
        this.diffConfiguration = diffConfiguration;
        this.eppClient = eppClient;
    }

    public Transaction getTransaction(long id) throws NoSuchTransactionException {
        ProcessInstance pi = processDAO.getProcessInstance(id);
        if (pi == null || !DOMAIN_MODIFICATION_PROCESS.equals(pi.getProcessDefinition().getName()))
            throw new NoSuchTransactionException(id);
        return new Transaction(pi, processDAO);
    }


    public String getTransactionToken(long transId, String name) throws NoSuchTransactionException {
        Transaction trans = getTransaction(transId);
        if (trans.getContactConfirmations() == null) return null;
        for (Identity id : trans.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) id;
            if (name.equals(cid.getName())) {
                return cid.getToken();
            }
        }
        throw new IllegalArgumentException("no name to confirm found: " + name);
    }


    public String getTransactionToken(long transId, SystemRole.SystemType role) throws NoSuchTransactionException {
        Transaction trans = getTransaction(transId);
        if (trans.getContactConfirmations() == null) return null;
        for (Identity id : trans.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) id;
            if (cid.getType() == role) {
                return cid.getToken();
            }
        }
        throw new IllegalArgumentException("no role to confirm found: " + role);
   }

    public Transaction createDomainCreationTransaction(Domain domain) {
        TransactionData td = new TransactionData();
        td.setCurrentDomain(domainManager.get(domain.getName()));
        ObjectChange domainChange = (ObjectChange) ChangeDetector.diff(new Domain(domain.getName()), domain, diffConfiguration);
        td.setDomainChange(domainChange);
        ProcessInstance pi = processDAO.newProcessInstance(DOMAIN_MODIFICATION_PROCESS, td);
        processDAO.signal(pi);
        return new Transaction(pi, processDAO);
    }

    public Transaction createDomainModificationTransaction(Domain modifiedDomain, String creator) throws NoModificationException {
        return createTransaction(modifiedDomain, null, creator);
    }

    public Transaction createDomainModificationTransaction(Domain modifiedDomain, String submitterEmail, String creator) throws NoModificationException {
        return createTransaction(modifiedDomain, submitterEmail, creator);
    }

    private Transaction createTransaction(Domain domain, String submitterEmail, String creator) throws NoModificationException {
        TransactionData td = new TransactionData();

        Domain currentDomain = domainManager.get(domain.getName());
        td.setCurrentDomain(currentDomain);

        ObjectChange domainChange = (ObjectChange) ChangeDetector.diff(currentDomain, domain, diffConfiguration);
        if (domainChange == null) throw new NoModificationException(domain.getName());
        td.setDomainChange(domainChange);

        Set<String> nameServers = td.getAddedOrUpdatedNameServers();
        List<Domain> impactedDomains = domainManager.findDelegatedTo(nameServers);
        impactedDomains.remove(currentDomain);
        td.setImpactedDomains(new HashSet<Domain>(impactedDomains));

        td.setSubmitterEmail(submitterEmail);

        td.getTrackData().setCreated(new Timestamp(System.currentTimeMillis()));
        td.getTrackData().setCreatedBy(creator);

        ProcessInstance pi = processDAO.newProcessInstance(DOMAIN_MODIFICATION_PROCESS, td);
        processDAO.signal(pi);

        return new Transaction(pi, processDAO);
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
        if (Domain.Status.NEW == domain.getStatus()) domainManager.delete(domain);
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
                result.add(new Transaction(pi, processDAO));
        return result;
    }
}
