package org.iana.rzm.trans.epp;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.epp.EPPClient;
import org.iana.epp.internal.verisign.VerisignEPPClient;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.dao.EmailAddresseeDAO;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.CreateTicketException;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * This test is excluded because it requires an access to the EPP test system.
 *
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"excluded"})
public class EPPChangesTest extends TransactionalSpringContextTests {
    protected ProcessDAO processDAO;
    protected UserManager userManager;
    protected DomainManager domainManager;
    protected HostManager hostManager;
    protected NotificationManager NotificationManagerBean;
    protected TransactionManager transactionManagerBean;
    protected TransactionService GuardedSystemTransactionService;
    protected AdminTransactionService GuardedAdminTransactionServiceBean;
    protected EmailAddresseeDAO emailAddresseeDAO;

    private RZMUser userAC, userTC, userIANA, userUSDoC;
    private DomainVO domainVONS, domainVO;
    private Domain domainNS, domain;

    private static final String DOMAIN_NAME = "org";
    private static final String DOMAIN_NAMENS = "com";

    private Long transId;

    private EPPClient eppClient;

    public EPPChangesTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        userAC = new RZMUser();
        userAC.setLoginName("gstsignaluser");
        userAC.setFirstName("ACuser");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME, true, true));
        userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAMENS, true, true));
        userManager.create(userAC);

        userTC = new RZMUser();
        userTC.setLoginName("gstsignalseconduser");
        userTC.setFirstName("TCuser");
        userTC.setLastName("lastName");
        userTC.setEmail("email@some.com");
        userTC.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAME, true, true));
        userTC.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAMENS, true, true));
        userManager.create(userTC);

        userIANA = new RZMUser();
        userIANA.setLoginName("gstsignaliana");
        userIANA.setFirstName("IANAuser");
        userIANA.setLastName("lastName");
        userIANA.setEmail("email@some.com");
        userIANA.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(userIANA);

        userUSDoC = new RZMUser();
        userUSDoC.setLoginName("gstsignalusdoc");
        userUSDoC.setFirstName("USDoCuser");
        userUSDoC.setLastName("lastName");
        userUSDoC.setEmail("email@some.com");
        userUSDoC.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        userManager.create(userUSDoC);

        domain = createDomain(DOMAIN_NAME);
        domain.addNameServer(setupFirstHost("pr1"));
        domain.addNameServer(setupSecondHost("pr2"));
        domain.setEnableEmails(true);
        domainManager.create(domain);

        domain.setRegistryUrl("newregurl.org");
        domain.getAdminContact().setEmail("admin@new-email.org");

        domainVO = DomainToVOConverter.toDomainVO(domain);

        domainNS = createDomain(DOMAIN_NAMENS);
        domainNS.addNameServer(setupFirstHost("pr3"));
        domainNS.addNameServer(setupSecondHost("pr4"));
        domainNS.setEnableEmails(true);

        domainManager.create(domainNS);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

        eppClient = VerisignEPPClient.getEPPClient("../conf/epp/epp.rootzone.config");
    }

    public static final String ADD_HOST_EXPECTED_RSP_1 = ".*Change Request Id:.*";
    public static final String ADD_HOST_EXPECTED_RSP_2 =
            ".*Host Create.*" +
                    "Name:.*NS1.GSTSNEWNAMESERVER.ORG.*" +
                    "Add:.*81.50.50.10.*" +
                    "Domain Update.*" +
                    "Name:.*COM.*" +
                    "Add:.*NS1.GSTSNEWNAMESERVER.ORG.*";
    public static final String EXPECTED_POLL_MESSAGE = "The DoC has approved this Change Request.";

    public void testAddHost() throws Exception {
        try {
            Host nameServer = new Host("ns1.gstsnewnameserver.org");
            nameServer.addIPAddress("81.50.50.10");
            domainNS.addNameServer(nameServer);
            domainVONS = DomainToVOConverter.toDomainVO(domainNS);
            transId = createTransaction(domainVONS, userAC).getTransactionID();
            Transaction trans = transactionManagerBean.getTransaction(transId);
            EPPChangeRequest eppChangeRequest = new EPPChangeRequest(trans, hostManager, eppClient);
            String rsp = eppChangeRequest.send();
            assert rsp != null;
            rsp = rsp.replaceAll("\\s", " ");
            assert rsp.matches(ADD_HOST_EXPECTED_RSP_1 + trans.getTicketID() + ".*");
            assert rsp.matches(ADD_HOST_EXPECTED_RSP_2);
            EPPPollRequest eppPollRequest = new EPPPollRequest(trans.getTicketID(), eppClient);
            rsp = eppPollRequest.send();
            assert rsp != null;
            assert rsp.equals(EXPECTED_POLL_MESSAGE);
        } finally {
            processDAO.close();
        }
    }

    public static final String UPDATE_HOST_ADD_IP_EXPECTED_RSP_1 = ".*Change Request Id:.*";
    public static final String UPDATE_HOST_ADD_IP_EXPECTED_RSP_2 =
            ".*Host Update.*" +
                    "Name:.*PR4.NS2.SOME.ORG.*" +
                    "Add:.*21.42.42.31.*";

    @Test(dependsOnMethods = "testAddHost")
    public void testUpdateHostAddIP() throws Exception {
        try {
            Host nameServer = domainNS.getNameServer("pr4.ns2.some.org");
            nameServer.addIPAddress("21.42.42.31");
            domainVONS = DomainToVOConverter.toDomainVO(domainNS);
            transId = createTransaction(domainVONS, userAC).getTransactionID();
            Transaction trans = transactionManagerBean.getTransaction(transId);
            EPPChangeRequest eppChangeRequest = new EPPChangeRequest(trans, hostManager, eppClient);
            String rsp = eppChangeRequest.send();
            assert rsp != null;
            assert rsp.length() > 0;
            rsp = rsp.replaceAll("\\s", " ");
            assert rsp.matches(UPDATE_HOST_ADD_IP_EXPECTED_RSP_1 + trans.getTicketID() + ".*");
            EPPPollRequest eppPollRequest = new EPPPollRequest(trans.getTicketID(), eppClient);
            rsp = eppPollRequest.send();
            assert rsp != null;
            assert rsp.equals(EXPECTED_POLL_MESSAGE);
        } finally {
            processDAO.close();
        }
    }

    public static final String DELETE_HOST_EXPECTED_RSP_1 = ".*Change Request Id:.*";
    public static final String DELETE_HOST_EXPECTED_RSP_2 =
            ".*Host Update.*" +
                    "Name:.*PR3.NS1.SOME.ORG.*" +
                    "Remove:.*11.2.3.4.*";

    @Test(dependsOnMethods = "testUpdateHostAddIP")
    public void testDeleteHost() throws Exception {
        try {
            domainNS.removeNameServer("pr4.ns1.some.org");
            domainVONS = DomainToVOConverter.toDomainVO(domainNS);
            transId = createTransaction(domainVONS, userAC).getTransactionID();
            Transaction trans = transactionManagerBean.getTransaction(transId);
            EPPChangeRequest eppChangeRequest = new EPPChangeRequest(trans, hostManager, eppClient);
            String rsp = eppChangeRequest.send();
            assert rsp != null;
            assert rsp.length() > 0;
            rsp = rsp.replaceAll("\\s", " ");
            assert rsp.matches(DELETE_HOST_EXPECTED_RSP_1 + trans.getTicketID() + ".*");
            EPPPollRequest eppPollRequest = new EPPPollRequest(trans.getTicketID(), eppClient);
            rsp = eppPollRequest.send();
            assert rsp != null;
            assert rsp.equals(EXPECTED_POLL_MESSAGE);
        } finally {
            processDAO.close();
        }
    }

    public static final String UPDATE_HOST_REMOVE_IP_EXPECTED_RSP_1 = ".*Change Request Id:.*";
    public static final String UPDATE_HOST_REMOVE_IP_EXPECTED_RSP_2 =
            ".*Host Update.*" +
                    "Name:.*PR3.NS1.SOME.ORG.*" +
                    "Remove:.*11.2.3.4.*";

    @Test(dependsOnMethods = "testDeleteHost")
    public void testUpdateHostRemoveIP() throws Exception {
        try {
            Host nameServer = domainNS.getNameServer("pr3.ns1.some.org");
            nameServer.removeIPAddress("11.2.3.4");
            domainVONS = DomainToVOConverter.toDomainVO(domainNS);
            transId = createTransaction(domainVONS, userAC).getTransactionID();
            Transaction trans = transactionManagerBean.getTransaction(transId);
            EPPChangeRequest eppChangeRequest = new EPPChangeRequest(trans, hostManager, eppClient);
            String rsp = eppChangeRequest.send();
            assert rsp != null;
            assert rsp.length() > 0;
            rsp = rsp.replaceAll("\\s", " ");
            assert rsp.matches(UPDATE_HOST_REMOVE_IP_EXPECTED_RSP_1 + trans.getTicketID() + ".*");
            EPPPollRequest eppPollRequest = new EPPPollRequest(trans.getTicketID(), eppClient);
            rsp = eppPollRequest.send();
            assert rsp != null;
            assert rsp.equals(EXPECTED_POLL_MESSAGE);
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (EmailAddressee emailAddressee : emailAddresseeDAO.findAll()) {
            NotificationManagerBean.deleteNotificationsByAddresse(emailAddressee);
            emailAddresseeDAO.delete(emailAddressee);
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    protected Domain createDomain(String name) {
        Domain newDomain = new Domain(name);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        Contact tech = new Contact("tech");
        tech.setEmail("tech@" + name + ".org");
        newDomain.setTechContact(tech);
        Contact admin = new Contact("admin");
        admin.setEmail("admin@" + name + ".org");
        newDomain.setAdminContact(admin);
        return newDomain;
    }

    protected Host setupFirstHost(String prefix) throws InvalidIPAddressException, InvalidDomainNameException {
        Host host = new Host(prefix + ".ns1.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("11.2.3.4"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        return host;
    }

    protected Host setupSecondHost(String prefix) throws InvalidIPAddressException, InvalidDomainNameException {
        Host host = new Host(prefix + ".ns2.some.org");
        host.addIPAddress(IPAddress.createIPv4Address("21.2.3.5"));
        host.addIPAddress(IPAddress.createIPv6Address("2235:5678::90AB"));
        return host;
    }

    protected TransactionVO createTransaction(IDomainVO domainVO, RZMUser user) throws Exception {
        try {
            setUser(user);
            List<TransactionVO> transaction = GuardedSystemTransactionService.createTransactions(domainVO, false);
            assert transaction != null;
            assert transaction.size() > 0;
            for (TransactionVO trans : transaction)
                trans.setTicketID(new Date().getTime());
            return transaction.iterator().next();
        } catch (CreateTicketException e) {
            // ignored
            return GuardedSystemTransactionService.get(e.getTransactionId());
        }
    }

    protected void setUser(RZMUser user) throws Exception {
        AuthenticatedUser authUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        GuardedSystemTransactionService.setUser(authUser);
        GuardedAdminTransactionServiceBean.setUser(authUser);
    }
}
