package org.iana.rzm.facade.admin;

import org.iana.dao.DataAccessObject;
import org.iana.notifications.PNotification;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.admin.trans.notifications.AdminNotificationService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.Identity;
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

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class ResendNotificationTest {
    private AdminNotificationService nts;
    private TransactionService gts;
    private AdminTransactionService ats;
    private UserManager userManager;
    private DomainManager domainManager;
    private DataAccessObject<PNotification> notificationManager;
    protected TransactionService sts;
    private ProcessDAO processDAO;
    private RZMUser iana, usdoc;
    private int domainCounter = 0;

    private final static String DOMAIN_NAME = "resenddomain";
    private final static String PROCESS_NAME = "Domain Modification Transaction (Unified Workflow)";
    private final static String NOTIFICATION_COMMENT = "Test";

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        gts = (TransactionService) appCtx.getBean("GuardedSystemTransactionService");
        ats = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        sts = (TransactionService) appCtx.getBean("GuardedSystemTransactionService");
        nts = (AdminNotificationService) appCtx.getBean("notificationService");

        userManager = (UserManager) appCtx.getBean("userManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        notificationManager = (DataAccessObject<PNotification>) appCtx.getBean("notificationDAO");

        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }

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

    public void testResendContactOutstandingConfirmationNotification() throws Exception {
        AuthenticatedUser au = new TestAuthenticatedUser(UserConverter.convert(iana)).getAuthUser();
        ats.setUser(au);
        Long transactionID = createDomainModificationProcess();
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);

        String token = getToken(transactionID, SystemRole.SystemType.AC);
        sts.setUser(au);
        sts.acceptTransaction(transactionID, token);

        nts.resendNotification(transactionID, NotificationVO.Type.CONTACT_CONFIRMATION, NOTIFICATION_COMMENT, null);
    }

    public void testResendUSDoCOutstandingConfirmationNotification() throws Exception {
        AuthenticatedUser au = new TestAuthenticatedUser(UserConverter.convert(iana)).getAuthUser();
        ats.setUser(au);
        Long transactionID = createDomainModificationProcess();
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
        ats.transitTransaction(transactionID, "admin-accept");
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_MANUAL_REVIEW);
        ats.transitTransaction(transactionID, "admin-accept");
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_IANA_CHECK);
        ats.transitTransaction(transactionID, "admin-accept");
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_USDOC_APPROVAL);

        nts.resendNotification(transactionID, NotificationVO.Type.USDOC_CONFIRMATION, NOTIFICATION_COMMENT, null);
    }

    private Long createDomainModificationProcess() throws Exception {
        AuthenticatedUser au = new TestAuthenticatedUser(UserConverter.convert(iana)).getAuthUser();
        gts.setUser(au);

        String domainName = DOMAIN_NAME + domainCounter;

        Domain domain = createTestDomain(domainName);
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = gts.createTransactions(DomainToVOConverter.toDomainVO(domain), false).get(0);
        Long transactionID = transactionVO.getTransactionID();

        transactionVO = ats.get(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(domainName);
        assert transactionVO.getName().equals(PROCESS_NAME);

        domainCounter++;

        return transactionID;
    }

    private void assertTransactionState(Long transactionID, TransactionStateVO.Name transactionStateVOName) throws Exception {
        TransactionVO transactionVO = ats.get(transactionID);
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
        for (PNotification notification : notificationManager.find())
            notificationManager.delete(notification);
    }
}
