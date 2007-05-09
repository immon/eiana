package org.iana.rzm.facade.system.trans;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system", "GuardedSystemTransactionService"})
public class GuardedSystemTransactionServiceTest {
    //private PlatformTransactionManager txMgr;
    //private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private TransactionManager transactionManager;
    //private AuthenticationService authenticationService;
    private UserManager userManager;
    private SystemTransactionService gsts;
    private DomainManager domainManager;
    private IDomainVO domain, domain1;
    private ProcessDAO processDAO;
    private TransactionVO transaction, transaction1;
    private AuthenticatedUser userAc, userTc, userAc1;
    private List<String> userLoginNames = new ArrayList<String>();
    private List<Long> transactionIds = new ArrayList<Long>();
    private List<String> domainNames = new ArrayList<String>();

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        transactionManager = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        userManager = (UserManager) appCtx.getBean("userManager");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
        userAc = createUser("ac", SystemRole.SystemType.AC, "gsts");
        userTc = createUser("tc", SystemRole.SystemType.TC, "gsts");
        userAc1 = createUser("ac1", SystemRole.SystemType.AC, "gstss");
        gsts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        domain = createDomain("gsts");
        domain1 = createDomain("gstss");
    }

    @Test
    public void testCreateTransaction() throws Exception {
        gsts.setUser(userAc);
        domain.setRegistryUrl("http://www.registry.url");
        transaction = gsts.createTransactions(domain, false).get(0);
        transactionIds.add(transaction.getTransactionID());
        assert transaction != null;

        TransactionVO loadedTransaction = gsts.getTransaction(transaction.getTransactionID());
        assert loadedTransaction != null;
        assert transaction.equals(loadedTransaction);

        gsts.setUser(userAc1);
        domain1.setRegistryUrl("http://www.registry.url");
        transaction1 = gsts.createTransactions(domain1, false).get(0);
        transactionIds.add(transaction1.getTransactionID());
        assert transaction1 != null;

        loadedTransaction = gsts.getTransaction(transaction1.getTransactionID());
        assert loadedTransaction != null;
        assert transaction1.equals(loadedTransaction);
    }

    @Test(dependsOnMethods = "testCreateTransaction")
    public void testFindOpenTransactions() throws Exception {
        gsts.setUser(userAc);
        List<TransactionVO> foundTransactions = gsts.findOpenTransactions();
        assert foundTransactions != null;
        assert foundTransactions.size() == 1;
        assert transaction.equals(foundTransactions.iterator().next());
    }

    @Test(dependsOnMethods = "testFindOpenTransactions",
            expectedExceptions = UnsupportedOperationException.class)
    public void testPerformTransactionTechnicalCheck() throws InfrastructureException, TechnicalCheckException {
        gsts.setUser(userAc);
        gsts.performTransactionTechnicalCheck(domain);
    }

    @Test(dependsOnMethods = "testPerformTransactionTechnicalCheck")
    public void testAcceptTransaction() throws Exception {
        gsts.setUser(userAc);
        gsts.acceptTransaction(transaction.getTransactionID());
        transaction = gsts.getTransaction(transaction.getTransactionID());
        assert transaction != null;
        assert transaction.getState() != null;
        assert transaction.getState().getName() != null;
        assert "PENDING_CONTACT_CONFIRMATION".equals(transaction.getState().getName().toString());
        
        gsts.setUser(userTc);
        gsts.acceptTransaction(transaction.getTransactionID());
        transaction = gsts.getTransaction(transaction.getTransactionID());
        assert transaction != null;
        assert transaction.getState() != null;
        assert transaction.getState().getName() != null;
        assert "PENDING_IMPACTED_PARTIES".equals(transaction.getState().getName().toString());
    }

    @Test(dependsOnMethods = "testAcceptTransaction",
            expectedExceptions = AccessDeniedException.class)
    public void testTransitTransaction() throws InfrastructureException, NoObjectFoundException {
        gsts.setUser(userTc);
        gsts.transitTransaction(transaction.getTransactionID(), "close");
    }

    @Test(dependsOnMethods = "testTransitTransaction")
    public void testRejectTransaction() throws Exception {
        domain.setRegistryUrl("http://www.registry.url.new");
        TransactionVO transactionToReject = gsts.createTransactions(domain, false).get(0);
        transactionIds.add(transactionToReject.getTransactionID());
        gsts.setUser(userTc);
        gsts.rejectTransaction(transactionToReject.getTransactionID());
        transactionToReject = gsts.getTransaction(transactionToReject.getTransactionID());
        assert transactionToReject != null;
        assert transactionToReject.getState() != null;
        assert transactionToReject.getState().getName() != null;
        assert "REJECTED".equals(transactionToReject.getState().getName().toString());
    }

    @Test(dependsOnMethods = "testRejectTransaction")
    public void testFindTransactionByDomain() throws Exception {
        gsts.setUser(userTc);

        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.addDomainName("gsts");
        List<TransactionVO> found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 2;

        criteria = new TransactionCriteriaVO();
        criteria.addDomainName("nonexistent");
        found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 0;
    }

    @Test(dependsOnMethods = "testFindTransactionByDomain")
    public void testFindTransactionByState() throws Exception {
        gsts.setUser(userTc);

        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.addState("REJECTED");
        List<TransactionVO> found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 1;

        criteria = new TransactionCriteriaVO();
        criteria.addState("nonexistent");
        found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 0;

        gsts.setUser(userAc1);

        criteria = new TransactionCriteriaVO();
        criteria.addState("PENDING_CONTACT_CONFIRMATION");
        found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 1;
    }

    @Test(dependsOnMethods = "testFindTransactionByState")
    public void testFindTransactionByTicketId() throws Exception {
        gsts.setUser(userTc);

        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.addTickedId(0L);
        List<TransactionVO> found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 2;
        criteria = new TransactionCriteriaVO();
        criteria.addTickedId(10L);
        found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 0;
    }

    @Test(dependsOnMethods = "testFindTransactionByTicketId")
    public void testFindTransactionByProcessName() throws Exception {
        gsts.setUser(userTc);

        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.addProcessName(DefinedTestProcess.getProcessName());
        List<TransactionVO> found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 2;
        criteria = new TransactionCriteriaVO();
        criteria.addProcessName("nonexistent");
        found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 0;
    }

    @Test(dependsOnMethods = "testFindTransactionByProcessName")
    public void testFindTransactionByStartDate() throws Exception {
        gsts.setUser(userTc);

        TransactionCriteriaVO criteria = new TransactionCriteriaVO();
        criteria.setStartedBefore(new Date());
        List<TransactionVO> found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 2;
        criteria = new TransactionCriteriaVO();
        criteria.setStartedAfter(new Date());
        found = gsts.findTransactions(criteria);
        assert found != null;
        assert found.size() == 0;
    }

    @AfterClass
    public void cleanUp() throws Exception {
        gsts.close();
        for (Long id : transactionIds)
            try {
                transactionManager.deleteTransaction(id);
            } finally {
                processDAO.close();
            }

        for (String name : userLoginNames)
            userManager.delete(name);

        for (String name : domainNames)
            domainManager.delete(name);
    }

    private AuthenticatedUser createUser(String name, SystemRole.SystemType roleType, String roleName) {
        RZMUser user = new RZMUser();
        user.setEmail(name + roleName + "@no-mail.org");
        user.setFirstName(name + roleName + " first name");
        user.setLastName(name + roleName + " last name");
        user.setLoginName(name + roleName);
        user.setOrganization(name + roleName + " organization");
        user.setPassword(new MD5Password(name + roleName));
        user.setSecurID(false);
        user.addRole(new SystemRole(roleType, roleName, true, false));
        userManager.create(user);
        userLoginNames.add(user.getLoginName());
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user));
        return testAuthUser.getAuthUser();
    }

    private Address setupAddress(Address address, String prefix, String countryCode) {
        address.setTextAddress(prefix + " text address");
        address.setCountryCode(countryCode);
        return address;
    }

    private Contact setupContact(Contact contact, String prefix, String domainName, String countryCode) {
        contact.setName(prefix + " name");
        contact.addAddress(setupAddress(new Address(), "contact", countryCode));
        contact.addEmail(prefix + "@no-mail." + domainName);
        contact.addFaxNumber("+1234567890");
        contact.addPhoneNumber("+1234567891");
        return contact;
    }

    private Host setupHost(Host host) throws InvalidIPAddressException, InvalidNameException {
        host.setName(host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    private Domain setupDomain(String name) throws MalformedURLException, InvalidNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        Domain domain = new Domain(name);
        domain.setRegistryUrl("http://www.registry." + name);
        domain.setSpecialInstructions(name + " special instructions");
        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(setupContact(new Contact(), "supporting-org", name, "US"));
        domain.setWhoisServer("whois." + name);
        domain.addAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupHost(new Host("ns1." + name)));
        domain.addNameServer(setupHost(new Host("ns2." + name)));
        domain.addTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }

    private IDomainVO createDomain(String name) throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = setupDomain(name);
        domainManager.create(domain);
        domainNames.add(name);
        return ToVOConverter.toDomainVO(domain);
    }
}
