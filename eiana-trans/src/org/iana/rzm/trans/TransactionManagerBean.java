package org.iana.rzm.trans;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.ticketing.TicketingService;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionManagerBean implements TransactionManager {

    private static final String DOMAIN_MODIFICATION_PROCESS = "Domain Modification Transaction (Unified Workflow)";

    private JbpmContext context;
    private ProcessDAO processDAO;
    private TicketingService ticketingService;
    private DomainDAO domainDAO;

    public TransactionManagerBean(ProcessDAO processDAO, DomainDAO domainDAO, TicketingService ticketingService) {
        this.processDAO = processDAO;
        this.ticketingService = ticketingService;
        this.domainDAO = domainDAO;
    }

    public Transaction getTransaction(long id) throws NoSuchTransactionException {
        GraphSession graphSession = context.getGraphSession();
        ProcessInstance processInstances = graphSession.loadProcessInstance(id);
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
        td.setActions(createActions(domain));
        ProcessInstance pi = new ProcessInstance(context.getGraphSession().findLatestProcessDefinition(DOMAIN_MODIFICATION_PROCESS));
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

    public List<Transaction> findAllProcessInstances(String domainName) {
        List<ProcessInstance> processInstances = processDAO.findAllProcessInstances(domainName);
        List<Transaction> result = new ArrayList<Transaction>();
        for (ProcessInstance pi : processInstances) result.add(new Transaction(pi));
        return result;
    }

    private List<TransactionAction> createActions(Domain domain) {
        List<TransactionAction> resultList = new ArrayList<TransactionAction>();
        CheckTool.checkNull(domain, "new domain");
        Domain originDomain = domainDAO.get(domain.getName());
        CheckTool.checkNull(originDomain, "origin domain");

        TransactionActionsListBuilder.addSupportingOrganizationAction(domain, originDomain, resultList);
        TransactionActionsListBuilder.addAdministrationContactsActions(domain, originDomain, resultList);
        TransactionActionsListBuilder.addTechnicalContactsActions(domain, originDomain, resultList);

        TransactionActionsListBuilder.addWhoisServerAction(domain, originDomain, resultList);

        TransactionActionsListBuilder.addRegisterURLAction(domain, originDomain, resultList);

        TransactionActionsListBuilder.addNSAction(domain, originDomain, resultList);

        return resultList;
    }

    //TEMPORARY METHOD, should be deleted later, when JbpmContext & spring problem will be reslowed.
    public void setJBPMContext(JbpmContext ctx) {
        this.context = ctx;
    }

}
