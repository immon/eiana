package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.dao.TransactionDAO;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.TrackData;
import org.iana.ticketing.TicketingService;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionManagerBean implements TransactionManager {
    private JbpmContext context;
    private TransactionDAO dao;
    private TicketingService ticketingService;

    public TransactionManagerBean(JbpmContext context, TransactionDAO dao, TicketingService ticketingService) {
        this.context = context;
        this.dao = dao;
        this.ticketingService = ticketingService;
    }

    public Transaction get(long id) throws NoSuchTransactionException {
        GraphSession graphSession = context.getGraphSession();
        ProcessInstance processInstances = graphSession.loadProcessInstance(id);
        if (processInstances == null) throw new NoSuchTransactionException(id);
        Transaction transaction = new Transaction(processInstances);
        return transaction;
    }

    public Transaction create(Domain domain) {
        TransactionData td = new TransactionData();
        td.setActions(createActions(domain));
        ProcessInstance pi = new ProcessInstance(context.getGraphSession().findLatestProcessDefinition("process trans test"));
        td.setTicketID(ticketingService.generateID());
        td.setActions(createActions(domain));
        pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
        pi.getContextInstance().setVariable("TRACK_DATA", new TrackData());
        return new Transaction(pi);
    }

    public Transaction modify(Domain domain) {
        return null;
    }

    public List<Transaction> findAll() {
        return null;
    }

    public List<Transaction> find(TransactionCriteria criteria) {
        return null;
    }

    public List<Transaction> findAllProcessInstances(String domainName) {
        List<ProcessInstance> processInstances = dao.findAllProcessInstances(domainName);
        List<Transaction> result = new ArrayList<Transaction>();
        for (ProcessInstance pi : processInstances) result.add(new Transaction(pi));
        return result;
    }

    private List<TransactionAction> createActions(Domain domain) {
        List<TransactionAction> resultList = new ArrayList<TransactionAction>();
        CheckTool.checkNull(domain, "new domain");
        DomainDAO dao = (DomainDAO) new ClassPathXmlApplicationContext("eiana-domains-spring.xml").getBean("domainDAO");
        Domain originDomain = dao.get(domain.getName());
        CheckTool.checkNull(originDomain, "origin domain");

        TransactionActionsListBuilder.addSupportingOrganizationAction(domain, originDomain, resultList);
        TransactionActionsListBuilder.addAdministrationContactsActions(domain, originDomain, resultList);
        TransactionActionsListBuilder.addTechnicalContactsActions(domain, originDomain, resultList);

        TransactionActionsListBuilder.addWhoisServerAction(domain, originDomain, resultList);
        TransactionActionsListBuilder.addRegisterURLAction(domain, originDomain, resultList);

        return resultList;
    }

}
