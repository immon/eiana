package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author: Piotr Tkaczyk
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
        domainManager.create(domain);

        domain.setRegistryUrl("newregurl.org");

        domainVO = ToVOConverter.toDomainVO(domain);


        domainNS = createDomain(DOMAIN_NAMENS);
        domain.addNameServer(setupFirstHost("pr3"));
        domain.addNameServer(setupSecondHost("pr4"));
        domainManager.create(domainNS);

        Host nameServer = new Host("ns1.gstsnewnameserver");
        nameServer.addIPAddress("81.50.50.10");
        domainNS.addNameServer(nameServer);
        nameServer = new Host("ns2.gstsnewnameserver");
        nameServer.addIPAddress("82.50.50.10");
        domainNS.addNameServer(nameServer);

        domainVONS = ToVOConverter.toDomainVO(domainNS);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

    }

    private static final String[][] REJECT_CONTACT_CONFIRMATIONLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test
    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        rejectPENDING_CONTACT_CONFIRMATION(userAC, transId);
        checkStateLog(userAC, transId, REJECT_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] CLOSE_CONTACT_CONFIRMATIONLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testREJECT_CONTACT_CONFIRMATION"})
    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        closePENDING_CONTACT_CONFIRMATION(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_CONTAC_CONFIRMATIONLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"}
    };

    @Test(dependsOnMethods = {"testCLOSE_CONTACT_CONFIRMATION"})
    public void testACCEPT_CONTAC_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        checkStateLog(userAC, transId, ACCEPT_CONTAC_CONFIRMATIONLog);
    }

    private static final String[][] REJECT_IMPACTED_PARTIESLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
//        {"gstsignaluser", "PENDING_IMPACTED_PARTIES", todo
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"}
    };
//    todo
//    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
//    public void testREJECT_IMPACTED_PARTIES() throws Exception {
//        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
//        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        rejectIMPACTED_PARTIES(userAC, transId);
//        checkStateLog(userAC, transId, REJECT_IMPACTED_PARTIESLog);
//    }

    private static final String[][] CLOSE_IMPACTED_PARTIESLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IMPACTED_PARTIES"}
    };
//    todo
//    @Test(dependsOnMethods = {"testREJECT_IMPACTED_PARTIES"})
//    public void testCLOSE_IMPACTED_PARTIES() throws Exception {
//        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
//        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        closeIMPACTED_PARTIES(userIANA, transId);
//        checkStateLog(userAC, transId, CLOSE_IMPACTED_PARTIESLog);
//    }

    private static final String[][] REJECT_EXT_APPROVALLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
//        {"gstsignaluser", "PENDING_IMPACTED_PARTIES", todo
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
    public void testREJECT_EXT_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        acceptIMPACTED_PARTIES(userAC, transId);  todo
        normalIANA_CONFIRMATION(userIANA, transId);
        rejectEXT_APPROVAL(userIANA, transId);
        checkStateLog(userAC, transId, REJECT_EXT_APPROVALLog);
    }

    private static final String[][] CLOSE_EXT_APPROVALLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
//            {"gstsignaluser", "PENDING_IMPACTED_PARTIES",  todo
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testREJECT_EXT_APPROVAL"})
    public void testCLOSE_EXT_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        acceptIMPACTED_PARTIES(userAC, transId);    todo
        normalIANA_CONFIRMATION(userIANA, transId);
        closeEXT_APPROVAL(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_EXT_APPROVALLog);
    }

    private static final String[][] REJECT_USDOC_APPROVALLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
//            {"gstsignaluser", "PENDING_IMPACTED_PARTIES", todo
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testCLOSE_EXT_APPROVAL"})
    public void testREJECT_USDOC_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        acceptIMPACTED_PARTIES(userAC, transId); todo
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        rejectUSDOC_APPROVAL(userUSDoC, transId);
        checkStateLog(userAC, transId, REJECT_USDOC_APPROVALLog);
    }

    private static final String[][] workFlowNoNSChangeLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
//            {"gstsignaluser", "PENDING_IMPACTED_PARTIES",  todo
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"SYSTEM", "USDOC_APPROVED"},
            {"SYSTEM", "PENDING_DATABASE_INSERTION"}
    };

    @Test(dependsOnMethods = {"testREJECT_USDOC_APPROVAL"})
    public void testWorkFlowNoNSChange() throws Exception {
        Long transId = createTransaction(domainVO, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        acceptIMPACTED_PARTIES(userAC, transId);  todo
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);
        checkStateLog(userAC, transId, workFlowNoNSChangeLog);
    }

    private static final String[][] workFlowWithNSChangeLog = {
            {"AC/TC", "PENDING_CONTACT_CONFIRMATION"},
            {"SYSTEM", "PENDING_TECH_CHECK"},
            {"SYSTEM", "DECISION_PENDING_IMPACTED_PARTIES"},
//            {"gstsignaluser", "PENDING_IMPACTED_PARTIES", todo
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"SYSTEM", "USDOC_APPROVED"},
            {"gstsignaliana", "PENDING_ZONE_INSERTION"},
            {"gstsignaliana", "PENDING_ZONE_PUBLICATION"},
            {"SYSTEM", "PENDING_DATABASE_INSERTION"}
    };

    @Test(dependsOnMethods = {"testWorkFlowNoNSChange"})
    public void testWorkFlowWithNSChange() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
//        acceptIMPACTED_PARTIES(userAC, transId); todo
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        acceptUSDOC_APPROVAL(userUSDoC, transId);
        acceptZONE_INSERTION(userIANA, transId);
        acceptZONE_PUBLICATION(userIANA, transId);
        checkStateLog(userAC, transId, workFlowWithNSChangeLog);
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
