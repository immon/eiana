package org.iana.rzm.trans;

import org.hibernate.Query;
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

    public TransactionManagerBean(JbpmContext context,TicketingService ticketingService) {
        this.context = context;
        this.dao = dao;
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
        pi.getContextInstance().setVariable("TRANSACTION_DATA",td);
        pi.getContextInstance().setVariable("TRACK_DATA",new TrackData());
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

    public List<ProcessInstance> findAllProcessInstances(String domainName) {
        return dao.findAllProcessInstances(domainName);
    }

    private List<TransactionAction> createActions(Domain domain) {
           List<TransactionAction> resultList = new ArrayList<TransactionAction>();
           CheckTool.checkNull(domain, "new domain");
           DomainDAO dao = (DomainDAO) new ClassPathXmlApplicationContext("eiana-domains-spring.xml").getBean("domainDAO");
           Domain originDomain = dao.get(domain.getName());
           CheckTool.checkNull(originDomain, "origin domain");

           TransactionActionsListBuilder.addWhoisServerAction(domain, originDomain, resultList);
           TransactionActionsListBuilder.addRegisterURLAction(domain, originDomain, resultList);
           TransactionActionsListBuilder.addSupportingOrganizationAction(domain, originDomain, resultList);
           TransactionActionsListBuilder.addAdministrationContactsActions(domain,originDomain,resultList);
           TransactionActionsListBuilder.addTechnicalContactsActions(domain,originDomain,resultList);

           return resultList;
       }
    
}
