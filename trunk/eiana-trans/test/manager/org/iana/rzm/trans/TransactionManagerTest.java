package org.iana.rzm.trans;

import org.iana.criteria.Equal;
import org.iana.criteria.In;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Lukasz Zuchowski
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"transactionManager", "eiana-trans"})
public class TransactionManagerTest extends TransactionalSpringContextTests {
    protected TransactionManager transactionManagerBean;
    protected UserManager userManager;
    protected DomainManager domainManager;
    protected ProcessDAO processDAO;
    private Set<Long> domain1TransIds = new HashSet<Long>();
    private Set<Long> domain2TransIds = new HashSet<Long>();

    public TransactionManagerTest() {
        super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
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
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
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
        } finally {
            processDAO.close();
        }
    }

    private void createTestTransactionsAndUsers() throws InvalidDomainNameException, NoModificationException, CloneNotSupportedException {
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
        Transaction transaction = transactionManagerBean.createDomainModificationTransaction(domain);
        transaction.setTicketID(ticketId);
        return transaction;
    }

    @Test
    public void testFindTransactionsByDomain() throws Exception {
        try {
            List<Transaction> dbDomain1Trans = transactionManagerBean.findTransactions("tmtestdomain1");

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transactionManagerBean.findTransactions("tmtestdomain2");

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByDomain")
    public void testFindTransactionsByUser() throws Exception {
        try {
            List<Transaction> dbDomain1Trans = transactionManagerBean.findTransactions(
                    userManager.get("user-sys1tm"));

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transactionManagerBean.findTransactions(
                    userManager.get("user-sys2tm"));

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByUser")
    public void testFindTransactionsByUserAndDomain() throws Exception {
        try {
            List<Transaction> dbDomain1Trans = transactionManagerBean.findTransactions(
                    userManager.get("user-sys1tm"), "tmtestdomain1");

            Set<Long> dbDomain1TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain1Trans) dbDomain1TransIds.add(tr.getTransactionID());

            assert domain1TransIds.equals(dbDomain1TransIds);

            List<Transaction> dbDomain2Trans = transactionManagerBean.findTransactions(
                    userManager.get("user-sys2tm"), "tmtestdomain2");

            Set<Long> dbDomain2TransIds = new HashSet<Long>();
            for (Transaction tr : dbDomain2Trans) dbDomain2TransIds.add(tr.getTransactionID());

            assert domain2TransIds.equals(dbDomain2TransIds);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionsByUserAndDomain")
    public void testFindTransactionByCriteria() {
        try {
            List<Transaction> transactions1 = transactionManagerBean.find(
                    new In("currentDomain.name.name", new HashSet(Arrays.asList("tmtestdomain1", "tmtestdomain2"))));
            assert transactions1 != null;
            assert transactions1.size() == 6;
            Set<Long> transIds1 = new HashSet<Long>();
            for (Transaction tr : transactions1) transIds1.add(tr.getTransactionID());
            Set<Long> allTransIds = new HashSet<Long>(domain1TransIds);
            allTransIds.addAll(domain2TransIds);
            assert allTransIds.equals(transIds1);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionByCriteria")
    public void testFindTransactionByCriteriaOffsetLimit() {
        try {
            List<Transaction> transactions1 = transactionManagerBean.find(
                    new In("currentDomain.name.name", new HashSet(Arrays.asList("tmtestdomain1", "tmtestdomain2"))),
                    0, 3);
            assert transactions1 != null;
            assert transactions1.size() == 3;
            Set<Long> transIds1 = new HashSet<Long>();
            for (Transaction tr : transactions1) transIds1.add(tr.getTransactionID());

            List<Transaction> transactions2 = transactionManagerBean.find(
                    new In("currentDomain.name.name", new HashSet(Arrays.asList("tmtestdomain1", "tmtestdomain2"))),
                    3, 3);
            assert transactions2 != null;
            assert transactions2.size() == 3;
            Set<Long> transIds2 = new HashSet<Long>();
            for (Transaction tr : transactions2) transIds2.add(tr.getTransactionID());

            assert Collections.disjoint(transIds1, transIds2);
            transIds1.addAll(transIds2);
            Set<Long> allTransIds = new HashSet<Long>(domain1TransIds);
            allTransIds.addAll(domain2TransIds);
            assert allTransIds.equals(transIds1);
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testFindTransactionByCriteriaOffsetLimit")
    public void testCountTransactionByCriteria() {
        try {
            int count1 = transactionManagerBean.count(
                    new Equal("currentDomain.name.name", "tmtestdomain1"));
            assert count1 == 3;

            int count2 = transactionManagerBean.count(
                    new In("currentDomain.name.name", new HashSet(Arrays.asList("tmtestdomain1", "tmtestdomain2"))));
            assert count2 == 6;
        } finally {
            processDAO.close();
        }
    }
}
