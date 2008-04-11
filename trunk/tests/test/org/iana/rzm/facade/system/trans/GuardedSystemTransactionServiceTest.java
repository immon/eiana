package org.iana.rzm.facade.system.trans;

import org.iana.criteria.*;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"facade-system", "GuardedSystemTransactionService"})
public class GuardedSystemTransactionServiceTest {
    //private PlatformTransactionManager txMgr;
    //private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private TransactionManager transactionManager;
    //private AuthenticationService authenticationService;
    private UserManager userManager;
    private TransactionService gsts;
    private AdminTransactionService ats;
    private DomainManager domainManager;
    private IDomainVO domain, domain1;
    private ProcessDAO processDAO;
    private TransactionVO transaction, transaction1;
    private AuthenticatedUser userAc, userTc, userAc1, userIANA;

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
        userIANA = createUser("iana", AdminRole.AdminType.IANA);
        gsts = (TransactionService) appCtx.getBean("GuardedSystemTransactionService");
        ats = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        domain = createDomain("gsts");
        domain1 = createDomain("gstss");
    }

    @Test
    public void testCreateTransaction() throws Exception {
        gsts.setUser(userAc);
        domain.setRegistryUrl("http://www.registry.url");
        transaction = gsts.createTransactions(domain, false).get(0);
        assert transaction != null;

        ats.setUser(userIANA);
        ats.updateTransaction(transaction.getObjId(), 0l, false, null);
        transaction.setTicketID(0l);

        TransactionVO loadedTransaction = gsts.get(transaction.getTransactionID());
        assert loadedTransaction != null;
//        assert transaction.equals(loadedTransaction);
        assert compareTransactionVOs(transaction, loadedTransaction);

        gsts.setUser(userAc1);
        domain1.setRegistryUrl("http://www.registry.url");
        transaction1 = gsts.createTransactions(domain1, false).get(0);
        assert transaction1 != null;
        ats.updateTransaction(transaction1.getObjId(), 0l, false, null);
        transaction1.setTicketID(0l);

        loadedTransaction = gsts.get(transaction1.getTransactionID());
        assert loadedTransaction != null;
//        assert transaction1.equals(loadedTransaction);
        assert compareTransactionVOs(transaction1, loadedTransaction);
    }

    @Test(dependsOnMethods = "testCreateTransaction")
    public void testFindOpenTransactionsByCriterion() throws Exception {
        gsts.setUser(userAc);
        Set<String> userDomains = new HashSet<String>();
        userDomains.add("gsts");
        And openTransactionForUser = new And(
                new IsNull(TransactionCriteriaFields.END),
                new In(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, userDomains)
            );
        Criterion crit = new SortCriterion(openTransactionForUser, new Order("currentDomain.name.name"));
        List<TransactionVO> foundTransactions = gsts.find(crit);
        assert foundTransactions != null;
        assert foundTransactions.size() == 1;
        //assert transaction.equals(foundTransactions.iterator().next());
        assert compareTransactionVOs(transaction, foundTransactions.iterator().next());
    }

    @Test(dependsOnMethods = "testFindOpenTransactionsByCriterion")
    public void testFindOpenTransactions() throws Exception {
        gsts.setUser(userAc);
        Set<String> userDomains = new HashSet<String>();
        userDomains.add("gsts");
        And openTransactionForUser = new And(
                new IsNull(TransactionCriteriaFields.END),
                new In(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, userDomains)
            );
        List<TransactionVO> foundTransactions = gsts.find(openTransactionForUser);
        assert foundTransactions != null;
        assert foundTransactions.size() == 1 : "found " + foundTransactions.size();
//        assert transaction.equals(foundTransactions.iterator().next());
        assert compareTransactionVOs(transaction, foundTransactions.iterator().next());
    }

    @Test(dependsOnMethods = "testFindOpenTransactions")
    public void testAcceptTransaction() throws Exception {
        gsts.setUser(userAc);

        transaction = gsts.get(transaction.getTransactionID());
        gsts.setUser(userIANA);
        transaction = gsts.get(transaction.getTransactionID());
        List<String> tokens = transaction.getTokens(SystemRoleVO.SystemType.AC);
        assert tokens.size() == 1;

        gsts.acceptTransaction(transaction.getTransactionID(), tokens.get(0));
        transaction = gsts.get(transaction.getTransactionID());
        assert transaction != null;
        assert transaction.getState() != null;
        assert transaction.getState().getName() != null;
        assert "PENDING_CONTACT_CONFIRMATION".equals(transaction.getState().getName().toString());

        gsts.setUser(userTc);

        tokens = transaction.getTokens(SystemRoleVO.SystemType.TC);
        assert tokens.size() == 1;

        gsts.acceptTransaction(transaction.getTransactionID(), tokens.get(0));
        transaction = gsts.get(transaction.getTransactionID());
        assert transaction != null;
        assert transaction.getState() != null;
        assert transaction.getState().getName() != null;
        assert "PENDING_MANUAL_REVIEW".equals(transaction.getState().getName().toString());
//        assert "PENDING_IMPACTED_PARTIES".equals(transaction.getState().getName().toString()); todo
    }

    @Test(dependsOnMethods = "testAcceptTransaction")
    public void testTransitTransaction() throws InfrastructureException, NoObjectFoundException {
        gsts.setUser(userTc);
        gsts.transitTransaction(transaction.getTransactionID(), "close");
    }

    @Test(dependsOnMethods = "testTransitTransaction")
    public void testRejectTransaction() throws Exception {
        domain.setRegistryUrl("http://www.registry.url.new");
        TransactionVO transactionToReject = gsts.createTransactions(domain, false).get(0);
        gsts.setUser(userTc);
        transactionToReject = gsts.get(transactionToReject.getTransactionID());
        List<String> tokens = transactionToReject.getTokens(SystemRoleVO.SystemType.TC);
        assert tokens.size() > 0;
        gsts.rejectTransaction(transactionToReject.getTransactionID(), tokens.iterator().next());
        transactionToReject = gsts.get(transactionToReject.getTransactionID());
        assert transactionToReject != null;
        assert transactionToReject.getState() != null;
        assert transactionToReject.getState().getName() != null;
        assert "REJECTED".equals(transactionToReject.getState().getName().toString());
    }

    @Test(dependsOnMethods = "testRejectTransaction")
    public void testFindTransactionByDomainByCriterion() throws Exception {
        gsts.setUser(userTc);
        //todo
        Set domainNames = new HashSet();
        domainNames.add("gsts");
        List<TransactionVO> found = gsts.find(new In("domain.name.name", domainNames));
        assert found != null;
//        assert found.size() == 2;

        domainNames.add("nonexistent");
        found = gsts.find(new In("domain.name.name", domainNames));
        assert found != null;
//        assert found.size() == 0;
    }

    /*
    @Test(threadPoolSize = 10, invocationCount = 600)
    public void testSetUserSimultaneously() throws Exception {
        System.out.println(">>>> thread id = " + Thread.currentThread().getObjId());
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getConcurrentContext();
        SystemTransactionService svc = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        svc.setUser(userTc);
    }
    */

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        processDAO.deleteAll();
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    private AuthenticatedUser createUser(String name, SystemRole.SystemType roleType, String roleName) {
        RZMUser user = new RZMUser();
        user.setEmail(name + roleName + "@no-mail.org");
        user.setFirstName(name + roleName + " first name");
        user.setLastName(name + roleName + " last name");
        user.setLoginName(name + roleName);
        user.setOrganization(name + roleName + " organization");
        user.setPassword(name + roleName);
        user.setSecurID(false);
        user.addRole(new SystemRole(roleType, roleName, true, false));
        userManager.create(user);
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user));
        return testAuthUser.getAuthUser();
    }

    private AuthenticatedUser createUser(String name, AdminRole.AdminType roleType) {
        RZMUser user = new RZMUser();
        user.setEmail(name + "@no-mail.org");
        user.setFirstName(name + " first name");
        user.setLastName(name + " last name");
        user.setLoginName(name);
        user.setOrganization(name + " organization");
        user.setPassword(name);
        user.setSecurID(false);
        user.addRole(new AdminRole(roleType));
        userManager.create(user);
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
        contact.setAddress(setupAddress(new Address(), "contact", countryCode));
        contact.setEmail(prefix + "@no-mail." + domainName);
        contact.setFaxNumber("+1234567890");
        contact.setPhoneNumber("+1234567891");
        return contact;
    }

    private Host setupFirstHost() throws InvalidIPAddressException, InvalidDomainNameException {
        Host host = new Host("ns1.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    private Host setupSecondHost() throws InvalidIPAddressException, InvalidDomainNameException {
        Host host = new Host("ns2.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("21.2.3.5"));
        host.addIPAddress(IPAddress.createIPv6Address("2235:5678::90AB"));
        return host;
    }

    private Domain setupDomain(String name) throws MalformedURLException, InvalidDomainNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        Domain domain = new Domain(name);
        domain.setRegistryUrl("http://www.registry." + name);
        domain.setSpecialInstructions(name + " special instructions");
//        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(setupContact(new Contact(), "supporting-org", name, "US"));
        domain.setWhoisServer("whois." + name);
        domain.setAdminContact(setupContact(new Contact(), "admin", name, "US"));
        domain.addNameServer(setupFirstHost());
        domain.addNameServer(setupSecondHost());
        domain.setTechContact(setupContact(new Contact(), "tech", name, "US"));
        return domain;
    }

    private IDomainVO createDomain(String name) throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = setupDomain(name);
        domainManager.create(domain);
        return DomainToVOConverter.toDomainVO(domain);
    }

    private boolean compareTransactionVOs(TransactionVO first, TransactionVO second) {

        if (first.getDomainActions() != null ? !first.getDomainActions().equals(second.getDomainActions()) : second.getDomainActions() != null)
            return false;
        if (first.getDomainName() != null ? !first.getDomainName().equals(second.getDomainName()) : second.getDomainName() != null)
            return false;
        if (first.getEnd() != null ? !first.getEnd().equals(second.getEnd()) : second.getEnd() != null) return false;
        if (first.getName() != null ? !first.getName().equals(second.getName()) : second.getName() != null)
            return false;
        if (first.getStart() != null ? !first.getStart().equals(second.getStart()) : second.getStart() != null)
            return false;
        if (first.getState() != null ? !first.getState().equals(second.getState()) : second.getState() != null)
            return false;
        if (first.getTicketID() != null ? !first.getTicketID().equals(second.getTicketID()) : second.getTicketID() != null)
            return false;
        if (first.getTransactionID() != null ? !first.getTransactionID().equals(second.getTransactionID()) : second.getTransactionID() != null)
            return false;

        return true;
    }
}
