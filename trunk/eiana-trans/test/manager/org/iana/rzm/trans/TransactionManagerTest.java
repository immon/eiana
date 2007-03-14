/**
 * org.iana.rzm.trans.TransactionManagerTest
 * (C) Research and Academic Computer Network - NASK
 * lukaszz, 2007-03-13, 11:17:42
 */
package org.iana.rzm.trans;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.iana.rzm.trans.dao.TransactionDAO;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.domain.*;

import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.hibernate.SessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmConfiguration;


import java.util.List;
import java.util.ArrayList;


public class TransactionManagerTest {

    TransactionManager transManager;
    DomainDAO domainDAO;
    TransactionDAO transDAO;
    SessionFactory sessionFactory;
    JbpmConfiguration jbpmConfig;
    HibernateTransactionManager hibernateTransactionManager;    

    @BeforeClass
    public void init() throws InvalidIPAddressException, NameServerAlreadyExistsException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("eiana-trans-spring.xml");
        domainDAO = (DomainDAO) ctx.getBean("domainDAOTarget");
        transDAO = (TransactionDAO) ctx.getBean("transactionDAO");
        transManager = (TransactionManager) ctx.getBean("transactionManagerBean");//new TransactionManagerBean(JbpmTestContextFactory.getJbpmContext(),transDAO,new FakeTicketingService(),domainDAO);
        hibernateTransactionManager = (HibernateTransactionManager) ctx.getBean("transactionManager");
        transManager.setJBPMContext(JbpmTestContextFactory.getJbpmContext());
        Domain domainCreated = new Domain("trans-manager.org");
        List<Host> nameServersList = new ArrayList<Host>();
        Host host = new Host("ns.nask.pl");
        List<IPAddress> ipsList = new ArrayList<IPAddress>();
        ipsList.add(IPAddress.createIPAddress("123.123.123.123"));
        ipsList.add(IPAddress.createIPAddress("123.123.123.124"));
        host.setAddresses(ipsList);
        nameServersList.add(host);
        domainCreated.setNameServers(nameServersList);
        domainDAO.create(domainCreated);
        deployProcessDefinition();

    }

    @Test(groups = {"transactionManager,eiana-trans"})
    public void test() throws Exception {

        TransactionStatus ts = hibernateTransactionManager.getTransaction(new DefaultTransactionDefinition());
        Domain domainFromDAO = domainDAO.get("trans-manager.org");
        Domain modifiedDomain = (Domain) domainFromDAO.clone(); 
        hibernateTransactionManager.commit(ts);
        List<Host> nameServersList = new ArrayList<Host>();
        Host modHost = new Host("ns.nask.pl");
        List<IPAddress> modIPsList = new ArrayList<IPAddress>();
        modIPsList.add(IPAddress.createIPAddress("122.122.122.123"));
        modIPsList.add(IPAddress.createIPAddress("122.122.122.124"));
        modHost.setAddresses(modIPsList);        
        nameServersList.add(modHost);
        modifiedDomain.setNameServers(nameServersList);
        Transaction t = transManager.modify(modifiedDomain);
        List<TransactionAction> taList =  t.getActions();
        for(TransactionAction ta : taList){
            System.out.println(ta.getName().toString());
            for(Change ch :ta.getChange()){
                System.out.println(ch.toString());                
            }
        }

    }

     private void deployProcessDefinition() {
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                "<process-definition name='process trans test'>" +
                        "  <start-state name='PENDING_IANA_CONFIRMATION'>" +
                        "    <transition to='first' />" +
                        "  </start-state>" +
                        "   <task-node name='first'>" +
                            "    <task name='doSmth'></task>" +
                            "    <transition name='ok' to='COMPLETED' />" +
                            "    <transition name='reject' to='REJECTED' />" +
                            "  </task-node>" +
                        "  <end-state name='COMPLETED' />" +
                        "  <end-state name='REJECTED' />" +
                        "</process-definition>"
        );

        JbpmContext jbpmContext = JbpmTestContextFactory.getJbpmContext();
        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }
    }
}
