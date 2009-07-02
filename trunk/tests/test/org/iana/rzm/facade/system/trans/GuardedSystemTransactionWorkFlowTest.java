package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;


/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */

@Test(sequential = true, groups = {"facade-system", "GuardedSystemTransactionWorkFlowTest"})
public class GuardedSystemTransactionWorkFlowTest extends CommonGuardedSystemTransaction {

    RZMUser userAC, userTC, userIANA, userUSDoC;
    DomainVO domainVONS, domainVO;
    Domain domainNS, domain;

    final static String DOMAIN_NAME = "org";
    final static String DOMAIN_NAMENS = "com";

    protected void initTestData() {
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

        Domain newDomain = domain.clone();

        newDomain.setRegistryUrl("newregurl.org");
        newDomain.getAdminContact().setEmail("admin@new-email.org");

        domainVO = DomainToVOConverter.toDomainVO(newDomain);


        domainNS = createDomain(DOMAIN_NAMENS);
        domainNS.addNameServer(setupFirstHost("pr3"));
        domainNS.addNameServer(setupSecondHost("pr4"));
        domainNS.setEnableEmails(true);
        domainManager.create(domainNS);

        Domain newDomainNS = domainNS.clone();

        Host nameServer = new Host("ns1.gstsnewnameserver");
        nameServer.addIPAddress("81.50.50.10");
        newDomainNS.addNameServer(nameServer);
        nameServer = new Host("ns2.gstsnewnameserver");
        nameServer.addIPAddress("82.50.50.10");
        newDomainNS.addNameServer(nameServer);

        domainVONS = DomainToVOConverter.toDomainVO(newDomainNS);

    }

    private static final String[][] REJECT_CONTACT_CONFIRMATIONLog = {
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test
    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC, "submitter@not.exist").getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        rejectPENDING_CONTACT_CONFIRMATION(userAC, transId);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userAC, transId, REJECT_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] CLOSE_CONTACT_CONFIRMATIONLog = {
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testREJECT_CONTACT_CONFIRMATION"})
    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        closePENDING_CONTACT_CONFIRMATION(userIANA, transId);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userAC, transId, CLOSE_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_CONTAC_CONFIRMATIONLog = {
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testCLOSE_CONTACT_CONFIRMATION"})
    public void testACCEPT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        checkStateLog(userAC, transId, ACCEPT_CONTAC_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_MANUAL_REVIEWLog = {
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"}
    };

    @Test(dependsOnMethods = {"testACCEPT_CONTACT_CONFIRMATION"})
    public void testACCEPT_MANUAL_REVIEW() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 2);
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
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"SYSTEM", "PENDING_SUPP_TECH_CHECK"}
    };

    @Test(dependsOnMethods = {"testACCEPT_MANUAL_REVIEW"})
    public void testACCEPT_IANA_CHECK() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation-nschange", 2);
        checkStateLog(userIANA, transId, ACCEPT_IANA_CHECKLog);
    }

    private static final String[][] REJECT_USDOC_APPROVALLog = {
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"SYSTEM", "PENDING_SUPP_TECH_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testACCEPT_IANA_CHECK"})
    public void testREJECT_USDOC_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation-nschange", 2);
        rejectUSDOC_APPROVAL(userUSDoC, transId);
        assertPersistentNotifications(transId, 2);
        checkStateLog(userIANA, transId, REJECT_USDOC_APPROVALLog);
    }

    private static final String[][] workFlowNoNSChangeLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"SYSTEM", "PENDING_DATABASE_INSERTION"}
    };

    @Test(dependsOnMethods = {"testREJECT_USDOC_APPROVAL"})
    public void testWorkFlowNoNSChange() throws Exception {
        Long transId = createTransaction(domainVO, userAC, "submitter@email.com").getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 3);
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 3);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 1);
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation", 0);
        checkStateLog(userIANA, transId, workFlowNoNSChangeLog);
    }

    private static final String[][] workFlowWithNSChangeLog = {
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"SYSTEM", "PENDING_SUPP_TECH_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testWorkFlowNoNSChange"})
    public void testWorkFlowWithNSChange() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        assertPersistentNotifications(transId, "contact-confirmation", 2);
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 2);
        assertPersistentNotifications(transId, "contact-confirmation", 0);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation-nschange", 2);
        acceptUSDOC_APPROVAL(userUSDoC, transId);
        assertPersistentNotifications(transId, "usdoc-confirmation-nschange", 0);
        assertPersistentNotifications(transId, 0);
        checkStateLog(userIANA, transId, workFlowWithNSChangeLog);
    }

    private void assertPersistentNotifications(Long transId, int count) throws Exception {
        assertPersistentNotifications(transId, null, count);
    }

    @Test(expectedExceptions = {AccessDeniedException.class})
    public void testAccessDeniedWhenAcceptByWrongUser() throws Exception {
        TransactionVO transactionVO = createTransactions(domainVO, false).get(0);
        setUser(userTC);
        transactionVO = GuardedSystemTransactionService.get(transactionVO.getTransactionID());
        List<String> tokens = transactionVO.getTokens(SystemRoleVO.SystemType.AC);
        GuardedSystemTransactionService.acceptTransaction(transactionVO.getTransactionID(), tokens.get(0));
    }

    @Test(expectedExceptions = {AccessDeniedException.class})
    public void testAccessDeniedWhenRejectByWrongUser() throws Exception {
        TransactionVO transactionToReject = createTransactions(domainVO, false).get(0);
        setUser(userTC);
        transactionToReject = GuardedSystemTransactionService.get(transactionToReject.getTransactionID());
        List<String> tokens = transactionToReject.getTokens(SystemRoleVO.SystemType.AC);
        GuardedSystemTransactionService.rejectTransaction(transactionToReject.getTransactionID(), tokens.get(0));
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransaction() {
        processDAO.deleteAll();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
/*
        for (EmailAddressee emailAddressee : emailAddresseeDAO.findAll()) {
            notificationDAO.deleteNotificationsByAddresse(emailAddressee);
            emailAddresseeDAO.delete(emailAddressee);
        }
*/
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        for (Domain domain : domainManager.findAll())
//            domainManager.delete(domain.getName());
    }
}
