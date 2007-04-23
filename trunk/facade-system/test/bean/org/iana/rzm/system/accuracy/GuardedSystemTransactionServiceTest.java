package org.iana.rzm.system.accuracy;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.*;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.trans.SystemTransactionService;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.system.conf.SpringSystemApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.MD5Password;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.dao.UserDAO;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.jbpm.graph.exe.ProcessInstance;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true)
public class GuardedSystemTransactionServiceTest {
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private SystemTransactionService gsts;
    private UserDAO userDAO;
    private DomainDAO domainDAO;
    private IDomainVO domain;
    private ProcessDAO processDAO;
    private TransactionVO transaction;
    private TransactionVO transactionToReject;
    private UserVO userAc, userTc;

    @BeforeClass
    public void init() throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        processDAO = (ProcessDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("processDAO");
        userDAO = (UserDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("userDAO");
        domainDAO = (DomainDAO) SpringSystemApplicationContext.getInstance().getContext().getBean("domainDAO");
        txMgr = (PlatformTransactionManager) SpringSystemApplicationContext.getInstance().getContext().getBean("transactionManager");
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        userAc = createUser("ac", SystemRole.SystemType.AC);
        userTc = createUser("tc", SystemRole.SystemType.TC);
        gsts = (SystemTransactionService) SpringSystemApplicationContext.getInstance().getContext().getBean("guardedSystemTransactionService");
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(userAc);
        gsts.setUser(testAuthUser.getAuthUser());
        domain = createDomain();
        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test
    public void testCreateTransaction() throws InfrastructureException, NoObjectFoundException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        transaction = gsts.createTransaction(domain);
        assert transaction != null;
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        TransactionVO loadedTransaction = gsts.getTransaction(transaction.getTransactionID());
        assert loadedTransaction != null;
        assert transaction.equals(loadedTransaction);
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = "testCreateTransaction")
    public void testFindOpenTransactions() throws InfrastructureException, NoObjectFoundException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        List<TransactionVO> foundTransactions = gsts.findOpenTransactions();
        assert foundTransactions != null;
        assert foundTransactions.size() == 1;
        assert transaction.equals(foundTransactions.iterator().next());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = "testFindOpenTransactions",
            expectedExceptions = UnsupportedOperationException.class)
    public void testPerformTransactionTechnicalCheck() throws InfrastructureException, TechnicalCheckException {
        gsts.performTransactionTechnicalCheck(domain);
    }

    @Test(dependsOnMethods = "testPerformTransactionTechnicalCheck",
            expectedExceptions = UnsupportedOperationException.class)
    public void testGetPossibleTransactionSplits() throws InfrastructureException {
        gsts.getPossibleTransactionSplits(domain);
    }

    @Test(dependsOnMethods = "testGetPossibleTransactionSplits")
    public void testAcceptTransaction() throws InfrastructureException, NoObjectFoundException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        gsts.acceptTransaction(transaction.getTransactionID());
        transaction = gsts.getTransaction(transaction.getTransactionID());
        assert transaction != null;
        assert transaction.getState() != null;
        assert transaction.getState().getName() != null;
        assert "PENDING_CONTACT_CONFIRMATION".equals(transaction.getState().getName().toString());
        TestAuthenticatedUser testAuthUser = new TestAuthenticatedUser(userTc);
        gsts.setUser(testAuthUser.getAuthUser());
        gsts.acceptTransaction(transaction.getTransactionID());
        transaction = gsts.getTransaction(transaction.getTransactionID());
        assert transaction != null;
        assert transaction.getState() != null;
        assert transaction.getState().getName() != null;
        assert "PENDING_IMPACTED_PARTIES".equals(transaction.getState().getName().toString());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test(dependsOnMethods = "testAcceptTransaction",
            expectedExceptions = AccessDeniedException.class)
    public void testTransitTransaction() throws InfrastructureException, NoObjectFoundException {
        try {
            gsts.transitTransaction(transaction.getTransactionID(), "close");
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testTransitTransaction")
    public void testRejectTransaction() throws InfrastructureException, NoObjectFoundException {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        transactionToReject = gsts.createTransaction(domain);
        gsts.rejectTransaction(transactionToReject.getTransactionID());
        transactionToReject = gsts.getTransaction(transactionToReject.getTransactionID());
        assert transactionToReject != null;
        assert transactionToReject.getState() != null;
        assert transactionToReject.getState().getName() != null;
        assert "REJECTED".equals(transactionToReject.getState().getName().toString());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @AfterClass
    public void cleanUp() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        gsts.close();
        ProcessInstance pi = processDAO.getProcessInstance(transaction.getTransactionID());
        if (pi != null) processDAO.delete(pi);
        pi = processDAO.getProcessInstance(transactionToReject.getTransactionID());
        if (pi != null) processDAO.delete(pi);
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        RZMUser user = userDAO.get(userAc.getObjId());
        if (user != null) userDAO.delete(user);
        user = userDAO.get(userTc.getObjId());
        if (user != null) userDAO.delete(user);
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        Domain dom = domainDAO.get(domain.getObjId());
        domainDAO.delete(dom);
        txMgr.commit(txStatus);
    }

    private UserVO createUser(String name, SystemRole.SystemType role) {
        RZMUser user = new RZMUser();
        user.setEmail(name + "gsts@no-mail.org");
        user.setFirstName(name + "gsts first name");
        user.setLastName(name + "gsts last name");
        user.setLoginName(name + "gsts");
        user.setOrganization(name + "gsts organization");
        user.setPassword(new MD5Password(name + "gsts"));
        user.setSecurID(false);
        user.addRole(new SystemRole(role, "gsts", true, false));
        userDAO.create(user);
        return UserConverter.convert(user);
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

    private IDomainVO createDomain() throws MalformedURLException, NameServerAlreadyExistsException, InvalidIPAddressException {
        Domain domain = setupDomain("gsts");
        domainDAO.create(domain);
        return ToVOConverter.toDomainVO(domain);
    }
}
