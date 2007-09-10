package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.trans.TransactionStateVO;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.SystemRole;
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
    private ProcessDAO processDAO;
    private RZMUser user, userAC, userTC;
    private Domain domain;
    private Long transactionID;

    private final static String DOMAIN_NAME = "resenddomain.org";
    private final static String PROCESS_NAME = "Domain Modification Transaction (Unified Workflow)";
    private final static String NOTIFICATION_COMMENT = "Test";

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        ats = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        userManager = (UserManager) appCtx.getBean("userManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

        user = new RZMUser();
        user.setLoginName("resendadminuser");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        this.userManager.create(user);

        userAC = new RZMUser();
        userAC.setLoginName("resendacuser");
        userAC.setFirstName("ACuser");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME, true, true));
        userManager.create(userAC);

        userTC = new RZMUser();
        userTC.setLoginName("resendtcuser");
        userTC.setFirstName("TCuser");
        userTC.setLastName("lastName");
        userTC.setEmail("email@some.com");
        userTC.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAME, true, true));
        userManager.create(userTC);

        domain = createTestDomain(DOMAIN_NAME);
        domainManager.create(domain);

    }

    private Domain createTestDomain(String domainName) {
        Domain newDomain = new Domain(domainName);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        newDomain.setAdminContact(new Contact("resendacuser"));
        newDomain.setTechContact(new Contact("resendtcuser"));
        newDomain.setEnableEmails(true);
        return newDomain;
    }

    public void testResendContactConfirmationNotification() throws Exception {
        createDomainModificationProcess();
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CREATION);
        ats.transitTransaction(transactionID, "go-on");
        assertTransactionState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);

        List<NotificationVO> notifications = ats.getNotifications(transactionID);

        assert notifications != null;
        assert notifications.size() == 2;

        for (NotificationVO notif : notifications)
            ats.resendNotification(notif.getAddressees(), notif.getObjId(), NOTIFICATION_COMMENT);
    }

    private void createDomainModificationProcess() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        ats.setUser(testAuthUser);

        Domain domain = createTestDomain(DOMAIN_NAME);
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = ats.createDomainModificationTransaction(ToVOConverter.toDomainVO(domain));
        transactionID = transactionVO.getTransactionID();

        transactionVO = ats.getTransaction(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
        assert transactionVO.getName().equals(PROCESS_NAME);
    }

    private void assertTransactionState(Long transactionID, TransactionStateVO.Name transactionStateVOName) throws Exception {
        TransactionVO transactionVO = ats.getTransaction(transactionID);
        assert transactionVO != null;
        assert transactionStateVOName.equals(transactionVO.getState().getName()) :
                "unexpected transaction state: " + transactionVO.getState().getName();
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
