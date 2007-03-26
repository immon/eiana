/**
 * org.iana.rzm.trans.TransactionManagerTest
 * (C) Research and Academic Computer Network - NASK
 * lukaszz, 2007-03-13, 11:17:42
 */
package org.iana.rzm.trans;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.domain.*;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.jbpm.JbpmConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

@Test(sequential=true, groups = {"transactionManager", "eiana-trans"})
public class TransactionManagerTest {

    TransactionManager transManager;
    DomainDAO domainDAO;
    JbpmConfiguration jbpmConfig;
    HibernateTransactionManager hibernateTransactionManager;    
    ProcessDAO processDAO;

    @BeforeClass
    public void init() throws InvalidIPAddressException, NameServerAlreadyExistsException, FileNotFoundException {
        ApplicationContext ctx = SpringApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        processDAO.deploy(DefinedTestProcess.getDefinition());
        domainDAO = (DomainDAO) ctx.getBean("domainDAOTarget");
        transManager = (TransactionManager) ctx.getBean("transactionManagerBean");//new TransactionManagerBean(JbpmTestContextFactory.getJbpmContext(),transDAO,new FakeTicketingService(),domainDAO);
        hibernateTransactionManager = (HibernateTransactionManager) ctx.getBean("transactionManager");
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

    }

    @Test
    public void test() throws Exception {

        TransactionStatus ts = hibernateTransactionManager.getTransaction(new DefaultTransactionDefinition());
        Domain domainFromDAO = domainDAO.get("trans-manager.org");
        Domain modifiedDomain = (Domain) domainFromDAO.clone();
        hibernateTransactionManager.commit(ts);
        modifiedDomain.setWhoisServer("new.whois.server");
        Transaction t = transManager.createDomainModificationTransaction(modifiedDomain);
        ObjectChange domainChange =  t.getDomainChange();
        assert domainChange != null && domainChange.getFieldChanges().size() == 1 && domainChange.getFieldChanges().containsKey("whoisServer");
/*
        todo: new test
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
        Transaction t = transManager.createDomainModificationTransaction(modifiedDomain);
        TestChangeVisitor tcv = new TestChangeVisitor();
        ObjectChange domainChange =  t.getDomainChange();
        assert names.contains(TransactionAction.Name.MODIFY_CONTACT);
        assert names.contains(TransactionAction.Name.MODIFY_NAMESERVER);
        List<PrimitiveValue> pv = tcv.valueVisitor.getPrimit();
        assert tcv.chaneges.contains(new Removal("address",new PrimitiveValue("123.123.123.124"))); 
        assert pv.contains(new PrimitiveValue("122.122.122.123"));
*/
        //tcv.printVisitedchanges();

    }
}
