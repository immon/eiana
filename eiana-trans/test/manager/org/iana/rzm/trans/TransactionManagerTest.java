/**
 * org.iana.rzm.trans.TransactionManagerTest
 * (C) Research and Academic Computer Network - NASK
 * lukaszz, 2007-03-13, 11:17:42
 */
package org.iana.rzm.trans;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.domain.*;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.PrimitiveValue;
import org.iana.rzm.trans.change.Removal;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


public class TransactionManagerTest {

    TransactionManager transManager;
    DomainDAO domainDAO;
    JbpmConfiguration jbpmConfig;
    HibernateTransactionManager hibernateTransactionManager;    

    @BeforeClass
    public void init() throws InvalidIPAddressException, NameServerAlreadyExistsException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("eiana-trans-spring.xml");
        domainDAO = (DomainDAO) ctx.getBean("domainDAOTarget");
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
        Contact someContact = new Contact("test Contact");
        modifiedDomain.addAdminContact(someContact);
        Transaction t = transManager.modify(modifiedDomain);
        TestChangeVisitor tcv = new TestChangeVisitor();
        List<TransactionAction> taList =  t.getActions();
        List<TransactionAction.Name> names = new ArrayList<TransactionAction.Name>();
        for(TransactionAction ta : taList){
            System.out.println("====================");
            System.out.println(ta.getName().toString());
            names.add(ta.getName());
            for(Change ch :ta.getChange()){
                ch.accept(tcv);                    
            }
            System.out.println("====================");
        }
        assert names.contains(TransactionAction.Name.MODIFY_CONTACT);
        assert names.contains(TransactionAction.Name.MODIFY_NAMESERVER);
        List<PrimitiveValue> pv = tcv.valueVisitor.getPrimit();
        assert tcv.chaneges.contains(new Removal("address",new PrimitiveValue("123.123.123.124"))); 
        assert pv.contains(new PrimitiveValue("122.122.122.123"));
        //tcv.printVisitedchanges();

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
