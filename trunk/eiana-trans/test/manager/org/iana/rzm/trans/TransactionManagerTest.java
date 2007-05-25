package org.iana.rzm.trans;

import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;
import org.jbpm.graph.exe.ProcessInstance;

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
    private PlatformTransactionManager txManager;
    private TransactionDefinition txDefinition = new DefaultTransactionDefinition();
    private TransactionManager transactionManager;
    private UserManager userManager;
    private DomainManager domainManager;
    private ProcessDAO processDAO;
    private Set<Long> domain1TransIds = new HashSet<Long>();
    private Set<Long> domain2TransIds = new HashSet<Long>();

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext ctx = SpringTransApplicationContext.getInstance().getContext();
        txManager = (PlatformTransactionManager) ctx.getBean("transactionManager");
        domainManager = (DomainManager) ctx.getBean("domainManager");
        userManager = (UserManager) ctx.getBean("userManager");
        transactionManager = (TransactionManager) ctx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) ctx.getBean("processDAO");
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
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
            domainManager.create(domainCreated);

            createTestTransactionsAndUsers();

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterTest
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            List<ProcessInstance> pis = processDAO.findAll();
            for (ProcessInstance pi : pis) {
                processDAO.delete(pi);
            }

            List<RZMUser> users = userManager.findAll();
            for (RZMUser user : users) {
                userManager.delete(user);
            }

            List<Domain> domains = domainManager.findAll();
            for (Domain domain : domains) {
                domainManager.delete(domain);
            }

            txManager.commit(txStatus);
        } catch (Exception e) {
//            if (!txStatus.isCompleted())
//                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    private void createTestTransactionsAndUsers() throws InvalidNameException, NoModificationException, CloneNotSupportedException {
        Domain domain1 = new Domain("tmtestdomain1");
        domainManager.create(domain1);


        Domain domain1Cloned = domain1.clone();
        domain1Cloned.setRegistryUrl("http://www.registry.url.new");
        Transaction tr = createTransaction(1L, domain1Cloned);
        domain1TransIds.add(tr.getTransactionID());
        tr = createTransaction(2L, domain1Cloned);
        domain1TransIds.add(tr.getTransactionID());
        tr = createTransaction(3L, domain1Cloned);
        domain1TransIds.add(tr.getTransactionID());

        Domain domain2 = new Domain("tmtestdomain2");
        domainManager.create(domain2);

        Domain domain2Cloned = domain2.clone();
        domain2Cloned.setRegistryUrl("http://www.registry.url.new");
        tr = createTransaction(11L, domain2Cloned);
        domain2TransIds.add(tr.getTransactionID());
        tr = createTransaction(12L, domain2Cloned);
        domain2TransIds.add(tr.getTransactionID());
        tr = createTransaction(13L, domain2Cloned);
        domain2TransIds.add(tr.getTransactionID());

        userManager.create(UserManagementTestUtil.createUser("sys1tm",
                UserManagementTestUtil.createSystemRole("tmtestdomain1", true, true,
                        SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("sys2tm",
                UserManagementTestUtil.createSystemRole("tmtestdomain2", true, true,
                        SystemRole.SystemType.AC)));
    }

    private Transaction createTransaction(final Long ticketId, final Domain domain) throws NoModificationException {
        Transaction transaction = transactionManager.createDomainModificationTransaction(domain);
        transaction.setTicketID(ticketId);
        return transaction;
    }

    @Test
    public void testFindTransactionsByDomain() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            List<Transaction> dbDomain1Trans = transactionManager.findTransactions("tmtestdomain1");

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transactionManager.findTransactions("tmtestdomain2");

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByDomain")
    public void testFindTransactionsByUser() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            List<Transaction> dbDomain1Trans = transactionManager.findTransactions(
                    userManager.get("user-sys1tm"));

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transactionManager.findTransactions(
                    userManager.get("user-sys2tm"));

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByUser")
    public void testFindTransactionsByUserAndDomain() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            List<Transaction> dbDomain1Trans = transactionManager.findTransactions(
                    userManager.get("user-sys1tm"), "tmtestdomain1");

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transactionManager.findTransactions(
                    userManager.get("user-sys2tm"), "tmtestdomain2");

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    /*
    @Test
    public void testCreateDomainCreationTransaction() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            userManager.create(UserManagementTestUtil.createUser("sys1tm",
                    UserManagementTestUtil.createSystemRole("createdomain", true, true,
                            SystemRole.SystemType.AC)));
            userManager.create(UserManagementTestUtil.createUser("sys2tm",
                    UserManagementTestUtil.createSystemRole("createdomain", true, true,
                            SystemRole.SystemType.TC)));

            Domain domain = new Domain("createdomain");
            domain.setSupportingOrg(createContact("createdomain-supp"));
            domain.addTechContact(createContact("createdomain-tech"));
            domain.addAdminContact(createContact("createdomain-admin"));
            Host host = new Host("ns1.createdomain");
            host.addIPAddress("4.3.2.1");
            domain.addNameServer(host);
            host = new Host("ns2.createdomain");
            host.addIPAddress("4.3.2.2");
            domain.addNameServer(host);
            domain.setRegistryUrl("registry.createdomain");
            domain.setWhoisServer("whois.createdomain");

            Domain newDomain = new Domain(domain.getName());
            domainManager.create(newDomain);

            transactionManager.createDomainCreationTransaction(domain);

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    private Contact createContact(String prefix) {
        Contact contact = new Contact(prefix, prefix + "org");
        contact.addAddress(new Address(prefix + "addr", "US"));
        contact.addEmail(prefix + "@no-mail.org");
        contact.addFaxNumber("+1234567890");
        contact.addPhoneNumber("+1234567890");
        return contact;
    }
    */
}
