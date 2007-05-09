package org.iana.rzm.facade.system.trans;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

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

        appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        gsts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

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
        domainManager.create(domain);

        domain.setRegistryUrl("newregurl.org");

        domainVO = ToVOConverter.toDomainVO(domain);


        domainNS = createDomain(DOMAIN_NAMENS);
        domainManager.create(domainNS);

        Host nameServer = new Host("gstsnewnameserver");
        nameServer.addIPAddress("192.168.0.1");
        domainNS.addNameServer(nameServer);

        domainVONS = ToVOConverter.toDomainVO(domainNS);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

    }

    private static final String [][] REJECT_CONTACT_CONFIRMATIONLog = {
        {"gstsignaluser", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test
    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        rejectPENDING_CONTACT_CONFIRMATION(userAC, transId);
        checkStateLog(userAC, transId, REJECT_CONTACT_CONFIRMATIONLog);
    }

    private static final String [][] CLOSE_CONTACT_CONFIRMATIONLog = {
        {"gstsignaliana", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testREJECT_CONTACT_CONFIRMATION"})
    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        closePENDING_CONTACT_CONFIRMATION(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_CONTACT_CONFIRMATIONLog);
    }

    private static final String [][] ACCEPT_CONTAC_CONFIRMATIONLog = {
        {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testCLOSE_CONTACT_CONFIRMATION"})
    public void testACCEPT_CONTAC_CONFIRMATION() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        checkStateLog(userAC, transId, ACCEPT_CONTAC_CONFIRMATIONLog);
    }

    private static final String [][] REJECT_IMPACTED_PARTIESLog = {
        {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
        {"gstsignaluser", "PENDING_IMPACTED_PARTIES"}
    };

    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
    public void testREJECT_IMPACTED_PARTIES() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        rejectIMPACTED_PARTIES(userAC, transId);
        checkStateLog(userAC, transId, REJECT_IMPACTED_PARTIESLog);
    }

    private static final String [][] CLOSE_IMPACTED_PARTIESLog = {
        {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
        {"gstsignaliana", "PENDING_IMPACTED_PARTIES"}
    };

    @Test(dependsOnMethods = {"testREJECT_IMPACTED_PARTIES"})
    public void testCLOSE_IMPACTED_PARTIES() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        closeIMPACTED_PARTIES(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_IMPACTED_PARTIESLog);
    }

    private static final String [][] REJECT_EXT_APPROVALLog = {
        {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
        {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
        {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
        {"gstsignaliana", "PENDING_EXT_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testCLOSE_IMPACTED_PARTIES"})
    public void testREJECT_EXT_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        rejectEXT_APPROVAL(userIANA, transId);
        checkStateLog(userAC, transId, REJECT_EXT_APPROVALLog);
    }

    private static final String [][] CLOSE_EXT_APPROVALLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testREJECT_EXT_APPROVAL"})
    public void testCLOSE_EXT_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        closeEXT_APPROVAL(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_EXT_APPROVALLog);
    }

    private static final String [][] REJECT_USDOC_APPROVALLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testCLOSE_EXT_APPROVAL"})
    public void testREJECT_USDOC_APPROVAL() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        rejectUSDOC_APPROVAL(userUSDoC, transId);
        checkStateLog(userAC, transId, REJECT_USDOC_APPROVALLog);
    }

    private static final String [][] workFlowNoNSChangeLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testREJECT_USDOC_APPROVAL"})
    public void testWorkFlowNoNSChange() throws Exception {
        Long transId = createTransaction(domainVO, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        acceptUSDOC_APPROVALnoNSChange(userUSDoC, transId);
        checkStateLog(userAC, transId, workFlowNoNSChangeLog);
    }

    private static final String [][] workFlowWithNSChangeLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"gstsignaliana", "PENDING_ZONE_INSERTION"},
            {"gstsignaliana", "PENDING_ZONE_PUBLICATION"},
    };

    @Test(dependsOnMethods = {"testWorkFlowNoNSChange"})
    public void testWorkFlowWithNSChange() throws Exception {
        Long transId = createTransaction(domainVONS, userAC).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        acceptUSDOC_APPROVAL(userUSDoC, transId);
        acceptZONE_INSERTION(userIANA, transId);
        acceptZONE_PUBLICATION(userIANA, transId);
        checkStateLog(userAC, transId, workFlowWithNSChangeLog);
    }

    @AfterClass
    public void cleanUp() {
        List<ProcessInstance> processInstances = processDAO.findAll();
        for (ProcessInstance processInstance : processInstances)
            processDAO.delete(processInstance);
        processDAO.close();

        userManager.delete(userAC);
        userManager.delete(userTC);
        userManager.delete(userIANA);
        userManager.delete(userUSDoC);
        domainManager.delete(DOMAIN_NAME);
        domainManager.delete(DOMAIN_NAMENS);
    }
}
