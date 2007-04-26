package org.iana.rzm.trans;

import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lukasz Zuchowski
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"transactionManager", "eiana-trans"})
public class TransactionManagerTest {

    TransactionManager transManager;
    DomainDAO domainDAO;
    ProcessDAO processDAO;
    private UserDAO userDAO;
    private Set<Long> domain1TransIds = new HashSet<Long>();
    private Set<Long> domain2TransIds = new HashSet<Long>();
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();

    @BeforeClass
    public void init() throws Exception, FileNotFoundException {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        domainDAO = (DomainDAO) ctx.getBean("domainDAOTarget");
        userDAO = (UserDAO) ctx.getBean("userDAO");
        txMgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
        transManager = (TransactionManager) ctx.getBean("transactionManagerBean");//new TransactionManagerBean(JbpmTestContextFactory.getJbpmContext(),transDAO,new FakeTicketingService(),domainDAO);

        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());

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

            createTestTransactionsAndUsers();
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            for (Long pid : domain1TransIds) {
                ProcessInstance pi = processDAO.getProcessInstance(pid);
                if (pi != null ) processDAO.delete(pi);
            }
            for (Long pid : domain2TransIds) {
                ProcessInstance pi = processDAO.getProcessInstance(pid); 
                if (pi != null ) processDAO.delete(pi);
            }
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            RZMUser user = userDAO.get("user-sys1tm");
            if (user != null) userDAO.delete(user);
            user = userDAO.get("user-sys2tm");
            if (user != null) userDAO.delete(user);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            Domain domain = domainDAO.get("tmtestdomain1");
            domainDAO.delete(domain);
            domain = domainDAO.get("tmtestdomain2");
            domainDAO.delete(domain);
            domain = domainDAO.get("trans-manager.org");
            domainDAO.delete(domain);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

/*
    @Test
    public void test() throws Exception {
        TransactionStatus ts = hibernateTransactionManager.getTransaction(new DefaultTransactionDefinition());
        Domain domainFromDAO = domainDAO.get("trans-manager.org");
        Domain modifiedDomain = (Domain) domainFromDAO.clone();
        hibernateTransactionManager.commit(ts);
        modifiedDomain.setWhoisServer("new.whois.server");
        Transaction t = transManager.createDomainModificationTransaction(modifiedDomain);
        ObjectChange domainChange = t.getDomainChange();
        assert domainChange != null && domainChange.getFieldChanges().size() == 1 && domainChange.getFieldChanges().containsKey("whoisServer");
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
        //tcv.printVisitedchanges();

    }
*/

    private void createTestTransactionsAndUsers() throws InvalidNameException {
        Domain domain1 = new Domain("tmtestdomain1");
        domainDAO.create(domain1);

        Transaction tr = createTransaction(1L, domain1);
        domain1TransIds.add(tr.getTransactionID());
        tr = createTransaction(2L, domain1);
        domain1TransIds.add(tr.getTransactionID());
        tr = createTransaction(3L, domain1);
        domain1TransIds.add(tr.getTransactionID());

        Domain domain2 = new Domain("tmtestdomain2");
        domainDAO.create(domain2);

        tr = createTransaction(11L, domain2);
        domain2TransIds.add(tr.getTransactionID());
        tr = createTransaction(12L, domain2);
        domain2TransIds.add(tr.getTransactionID());
        tr = createTransaction(13L, domain2);
        domain2TransIds.add(tr.getTransactionID());

        userDAO.create(UserManagementTestUtil.createUser("sys1tm",
                UserManagementTestUtil.createSystemRole("tmtestdomain1", true, true,
                        SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys2tm",
                UserManagementTestUtil.createSystemRole("tmtestdomain2", true, true,
                        SystemRole.SystemType.AC)));
    }

    private Transaction createTransaction(final Long ticketId, final Domain domain) {
        try {
            ProcessInstance pi = processDAO.newProcessInstance(DefinedTestProcess.getProcessName());
            TransactionData td = new TransactionData();
            td.setTicketID(ticketId);
            td.setCurrentDomain(domain);
            pi.getContextInstance().setVariable("TRANSACTION_DATA", td);
            Transaction transaction = new Transaction(pi);
            processDAO.save(pi);
            return transaction;
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testFindTransactionsByDomain() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            List<Transaction> dbDomain1Trans = transManager.findTransactions("tmtestdomain1");

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transManager.findTransactions("tmtestdomain2");

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByDomain")
    public void testFindTransactionsByUser() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            List<Transaction> dbDomain1Trans = transManager.findTransactions(userDAO.get("user-sys1tm"));

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transManager.findTransactions(userDAO.get("user-sys2tm"));

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByUser")
    public void testFindTransactionsByUserAndDomain() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            List<Transaction> dbDomain1Trans = transManager.findTransactions(userDAO.get("user-sys1tm"), "tmtestdomain1");

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transManager.findTransactions(userDAO.get("user-sys2tm"), "tmtestdomain2");

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }
}
