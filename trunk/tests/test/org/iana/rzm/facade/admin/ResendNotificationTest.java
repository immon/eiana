package org.iana.rzm.facade.admin;

import org.iana.rzm.auth.Identity;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.trans.SystemTransactionService;
import org.iana.rzm.facade.system.trans.TransactionStateVO;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = "excluded")
public class ResendNotificationTest {
    private AdminTransactionService ats;
    private UserManager userManager;
    private DomainManager domainManager;
    protected SystemTransactionService sts;
    private ProcessDAO processDAO;
    private RZMUser iana, usdoc;
    private int domainCounter = 0;

    private final static String DOMAIN_NAME = "resenddomain";
    private final static String PROCESS_NAME = "Domain Modification Transaction (Unified Workflow)";
    private final static String NOTIFICATION_COMMENT = "Test";

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        ats = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        sts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");

        userManager = (UserManager) appCtx.getBean("userManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

        iana = new RZMUser();
        iana.setLoginName("resendianauser");
        iana.setFirstName("firstName");
        iana.setLastName("lastName");
        iana.setEmail("email@some.com");
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        this.userManager.create(iana);

        usdoc = new RZMUser();
        usdoc.setLoginName("resendusdocnuser");
        usdoc.setFirstName("firstName");
        usdoc.setLastName("lastName");
        usdoc.setEmail("email@some.com");
        usdoc.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        this.userManager.create(usdoc);
    }

    private Domain createTestDomain(String domainName) {
        RZMUser userAC = new RZMUser();
        userAC.setLoginName("resendacuser");
        userAC.setFirstName("ACuser");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        userAC.addRole(new SystemRole(SystemRole.SystemType.AC, domainName, true, true));
        userManager.create(userAC);

        RZMUser userTC = new RZMUser();
        userTC.setLoginName("resendtcuser");
        userTC.setFirstName("TCuser");
        userTC.setLastName("lastName");
        userTC.setEmail("email@some.com");
        userTC.addRole(new SystemRole(SystemRole.SystemType.TC, domainName, true, true));
        userManager.create(userTC);

        Domain domain = new Domain(domainName);
        Contact supp = new Contact("resendsupp");
        supp.setEmail("resendsupp@some.com");
        domain.setSupportingOrg(supp);
        Contact ac = new Contact("resendac");
        ac.setEmail("resendac@some.com");
        domain.setAdminContact(ac);
        Contact tc = new Contact("resendtc");
        tc.setEmail("resendtc@some.com");
        domain.setTechContact(tc);
        domain.setEnableEmails(true);
        domainManager.create(domain);
        return domain;
    }

    public void testResendContactConfirmationNotification() throws Exception {
        ats.setUser(new TestAuthenticatedUser(UserConverter.convert(iana)).getAuthUser());
        Long transactionID = createDomainModificationProcess();
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CREATION);
        ats.transitTransaction(transactionID, "go-on");
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);

        List<NotificationVO> notifications = ats.getNotifications(transactionID);

        assert notifications != null;
        assert notifications.size() == 2;

        for (NotificationVO notif : notifications)
            ats.resendNotification(notif.getAddressees(), notif.getObjId(), NOTIFICATION_COMMENT);
    }

    //@Test(dependsOnMethods = "testResendContactConfirmationNotification")
    public void testResendContactOutstandingConfirmationNotification() throws Exception {
        AuthenticatedUser au = new TestAuthenticatedUser(UserConverter.convert(iana)).getAuthUser();
        ats.setUser(au);
        Long transactionID = createDomainModificationProcess();
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CREATION);
        ats.transitTransaction(transactionID, "go-on");
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);

        String token = getToken(transactionID, SystemRole.SystemType.AC);
        sts.setUser(au);
        sts.acceptTransaction(transactionID, token);

        ats.resendNotification(transactionID, NotificationVO.Type.CONTACT_CONFIRMATION, NOTIFICATION_COMMENT);
    }

    private Long createDomainModificationProcess() throws Exception {
        String domainName = DOMAIN_NAME + domainCounter;

        Domain domain = createTestDomain(domainName);
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = ats.createDomainModificationTransaction(ToVOConverter.toDomainVO(domain));
        Long transactionID = transactionVO.getTransactionID();

        transactionVO = ats.getTransaction(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(domainName);
        assert transactionVO.getName().equals(PROCESS_NAME);

        domainCounter++;

        return transactionID;
    }

    private void assertTransactionState(Long transactionID, TransactionStateVO.Name transactionStateVOName) throws Exception {
        TransactionVO transactionVO = ats.getTransaction(transactionID);
        assert transactionVO != null;
        assert transactionStateVOName.equals(transactionVO.getState().getName()) :
                "unexpected transaction state: " + transactionVO.getState().getName();
    }

    private String getToken(long transID, SystemRole.SystemType role) {
        try {
            ProcessInstance pi = processDAO.getProcessInstance(transID);
            TransactionData td = (TransactionData) pi.getContextInstance().getVariable("TRANSACTION_DATA");
            if (td.getContactConfirmations() == null) return null;
            for (Identity id : td.getContactConfirmations().getUsersAbleToAccept()) {
                ContactIdentity cid = (ContactIdentity) id;
                if (cid.getType() == role) {
                    return cid.getToken();
                }
            }
            throw new IllegalArgumentException("no role to confirm found: " + role);
        } finally {
            processDAO.close();
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
