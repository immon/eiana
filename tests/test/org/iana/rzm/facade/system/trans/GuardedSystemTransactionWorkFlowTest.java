package org.iana.rzm.facade.system.trans;

import org.iana.criteria.And;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.Notification;
import org.iana.notifications.NotificationCriteriaFields;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: Piotr Tkaczyk
 * @author: JaKub Laszkiewicz
 */

@Test(sequential = true, groups = {"facade-system", "GuardedSystemTransactionWorkFlowTest"})
public class GuardedSystemTransactionWorkFlowTest extends CommonGuardedSystemTransaction {

    RZMUser userAC, userTC, userIANA, userUSDoC;
    DomainVO domainVONS, domainVO;
    Domain domainNS, domain;

    final static String DOMAIN_NAME = "gstsignaltest.org";
    final static String DOMAIN_NAMENS = "gstsignaltestns.org";

    @BeforeClass
    public void init() {
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

        Host nameServer = new Host("ns1.gstsnewnameserver");
        nameServer.addIPAddress("81.50.50.10");
        domainNS.addNameServer(nameServer);
        nameServer = new Host("ns2.gstsnewnameserver");
        nameServer.addIPAddress("82.50.50.10");
        domainNS.addNameServer(nameServer);

        domainVONS = DomainToVOConverter.toDomainVO(domainNS);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

    }

    private static final String[][] REJECT_CONTACT_CONFIRMATIONLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test
    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        rejectPENDING_CONTACT_CONFIRMATION(userAC, transId);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userAC, transId, REJECT_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] CLOSE_CONTACT_CONFIRMATIONLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testREJECT_CONTACT_CONFIRMATION"})
    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        closePENDING_CONTACT_CONFIRMATION(userIANA, transId);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userAC, transId, CLOSE_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_CONTAC_CONFIRMATIONLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testCLOSE_CONTACT_CONFIRMATION"})
    public void testACCEPT_CONTAC_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        checkStateLog(userAC, transId, ACCEPT_CONTAC_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_MANUAL_REVIEWLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"}
    };

    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
    public void testACCEPT_MANUAL_REVIEW() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        checkStateLog(userIANA, transId, ACCEPT_MANUAL_REVIEWLog);
    }

////    todo
////    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
////    public void testREJECT_IMPACTED_PARTIES() throws Exception {
////        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
////        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
////        rejectIMPACTED_PARTIES(userAC, transId);
////        checkStateLog(userAC, transId, REJECT_IMPACTED_PARTIESLog);
////    }
//
//    private static final String [][] CLOSE_IMPACTED_PARTIESLog = {
//        {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
//        {"gstsignaliana", "PENDING_IMPACTED_PARTIES"}
//    };
////    todo
////    @Test(dependsOnMethods = {"testREJECT_IMPACTED_PARTIES"})
////    public void testCLOSE_IMPACTED_PARTIES() throws Exception {
////        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
////        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
////        closeIMPACTED_PARTIES(userIANA, transId);
////        checkStateLog(userAC, transId, CLOSE_IMPACTED_PARTIESLog);
////    }
//
//    private static final String [][] REJECT_EXT_APPROVALLog = {
//        {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
////        {"gstsignaluser", "PENDING_IMPACTED_PARTIES"}, todo
//        {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
//        {"gstsignaliana", "PENDING_EXT_APPROVAL"}
//    };
//
//    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
//    public void testREJECT_EXT_APPROVAL() throws Exception {
//        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
//        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
////        acceptIMPACTED_PARTIES(userAC, transId);  todo
//        normalIANA_CONFIRMATION(userIANA, transId);
//        rejectEXT_APPROVAL(userIANA, transId);
//        checkStateLog(userAC, transId, REJECT_EXT_APPROVALLog);
//    }
//
//    private static final String [][] CLOSE_EXT_APPROVALLog = {
//            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
////            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},  todo
//            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
//            {"gstsignaliana", "PENDING_EXT_APPROVAL"}
//    };
//
//    @Test(dependsOnMethods = {"testREJECT_EXT_APPROVAL"})
//    public void testCLOSE_EXT_APPROVAL() throws Exception {
//        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
//        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
////        acceptIMPACTED_PARTIES(userAC, transId);    todo
//        normalIANA_CONFIRMATION(userIANA, transId);
//        closeEXT_APPROVAL(userIANA, transId);
//        checkStateLog(userAC, transId, CLOSE_EXT_APPROVALLog);
//    }

    private static final String[][] ACCEPT_IANA_CHECKLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignaliana", "PENDING_SUPP_TECH_CHECK"}
    };

    @Test(dependsOnMethods = {"testACCEPT_MANUAL_REVIEW"})
    public void testACCEPT_IANA_CHECK() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 1);
        checkStateLog(userIANA, transId, ACCEPT_IANA_CHECKLog);
    }

    private static final String[][] REJECT_USDOC_APPROVALLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignaliana", "PENDING_SUPP_TECH_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testACCEPT_IANA_CHECK"})
    public void testREJECT_USDOC_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 1);
        rejectUSDOC_APPROVAL(userUSDoC, transId);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userIANA, transId, REJECT_USDOC_APPROVALLog);
    }

    private static final String[][] workFlowNoNSChangeLog = {
            {"default-iana", "PENDING_CREATION"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"SYSTEM", "PENDING_DATABASE_INSERTION"}
    };

    @Test(dependsOnMethods = {"testREJECT_USDOC_APPROVAL"})
    public void testWorkFlowNoNSChange() throws Exception {
        Long transId = createTransaction(domainVO, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 3);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 3);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 1);
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 0);
        checkStateLog(userIANA, transId, workFlowNoNSChangeLog);
    }

    private static final String[][] workFlowWithNSChangeLog = {
            {"default-iana", "PENDING_CREATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignaliana", "PENDING_SUPP_TECH_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"gstsignaliana", "PENDING_ZONE_INSERTION"},
            {"gstsignaliana", "PENDING_ZONE_PUBLICATION"},
            {"SYSTEM", "PENDING_ZONE_TESTING"},
            {"SYSTEM", "PENDING_DATABASE_INSERTION"}
    };

    @Test(dependsOnMethods = {"testWorkFlowNoNSChange"})
    public void testWorkFlowWithNSChange() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CREATION(transId);
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 1);
        acceptUSDOC_APPROVAL(userUSDoC, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 0);
        acceptZONE_INSERTION(userIANA, transId);
        acceptZONE_PUBLICATION(userIANA, transId);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userIANA, transId, workFlowWithNSChangeLog);
    }

    private void assertPersistentNotifications(Long transId, int count) {
        assertPersistentNotifications(transId, null, count);
    }

    private void assertPersistentNotifications(Long transId, String type, int count) {
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transId));
        criteria.add(new Equal(NotificationCriteriaFields.PERSISTENT, true));
        if (type != null)
            criteria.add(new Equal(NotificationCriteriaFields.TYPE, type));
        List<Notification> notifications = notificationManagerBean.find(new And(criteria));
        assert notifications != null : "notifications list is null";
        assert notifications.size() == count : "unexpected notifications count: " + notifications.size();
        for (Notification notif : notifications) {
            if (type != null)
                assert type.equals(notif.getType()) :
                        "unexpected notification type: " + notif.getType();
            assert notif.isPersistent() : "notification is not persistent";
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
        for (EmailAddressee emailAddressee : emailAddresseeDAO.findAll()) {
            notificationManagerBean.deleteNotificationsByAddresse(emailAddressee);
            emailAddresseeDAO.delete(emailAddressee);
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
