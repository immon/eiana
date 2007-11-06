package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.ContactVO;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"facade-system", "DomainCreationTransactionWorkFlowTest"})
public class DomainCreationTransactionWorkFlowTest extends CommonGuardedSystemTransaction {
    private final static String DOMAIN_NAME_BASE = "createtranstest";

    private RZMUser userAC;
    private RZMUser userIANA;
    private RZMUser userUSDoC;
    private int domainCounter = 0;

    @BeforeClass
    public void init() {
        userAC = new RZMUser();
        userAC.setLoginName("gstsignaluser");
        userAC.setFirstName("ACuser");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        for (int i = 0; i < 7; i++) {
            userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME_BASE + i, true, true));
            userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME_BASE + i, true, true));
        }
        userManager.create(userAC);

        RZMUser userTC = new RZMUser();
        userTC.setLoginName("gstsignalseconduser");
        userTC.setFirstName("TCuser");
        userTC.setLastName("lastName");
        userTC.setEmail("email@some.com");
        for (int i = 0; i < 7; i++) {
            userTC.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAME_BASE + i, true, true));
            userTC.addRole(new SystemRole(SystemRole.SystemType.TC, DOMAIN_NAME_BASE + i, true, true));
        }
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

        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
    }

    private static final String[][] REJECT_CONTACT_CONFIRMATIONLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"default-iana", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test
    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        rejectPENDING_CONTACT_CONFIRMATION(userIANA, transId);
        checkStateLog(userAC, transId, REJECT_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] CLOSE_CONTACT_CONFIRMATIONLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"gstsignaliana", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testREJECT_CONTACT_CONFIRMATION"})
    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        closePENDING_CONTACT_CONFIRMATION(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_CONTAC_CONFIRMATIONLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"default-iana", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testCLOSE_CONTACT_CONFIRMATION"})
    public void testACCEPT_CONTAC_CONFIRMATION() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        checkStateLog(userAC, transId, ACCEPT_CONTAC_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_MANUAL_REVIEWLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"default-iana", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"}
    };

    @Test(dependsOnMethods = {"testACCEPT_CONTAC_CONFIRMATION"})
    public void testACCEPT_MANUAL_REVIEW() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        acceptMANUAL_REVIEW(userIANA, transId);
        checkStateLog(userIANA, transId, ACCEPT_MANUAL_REVIEWLog);
    }

    private static final String[][] ACCEPT_IANA_CHECKLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"default-iana", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignaliana", "PENDING_SUPP_TECH_CHECK"}
    };

    @Test(dependsOnMethods = {"testACCEPT_MANUAL_REVIEW"})
    public void testACCEPT_IANA_CHECK() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        checkStateLog(userIANA, transId, ACCEPT_IANA_CHECKLog);
    }

    private static final String[][] REJECT_USDOC_APPROVALLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"default-iana", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignaliana", "PENDING_SUPP_TECH_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testACCEPT_IANA_CHECK"})
    public void testREJECT_USDOC_APPROVAL() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        rejectUSDOC_APPROVAL(userUSDoC, transId);
        checkStateLog(userIANA, transId, REJECT_USDOC_APPROVALLog);
    }

    private static final String[][] workFlowWithNSChangeLog = {
            {"default-iana", "PENDING_CREATION"},
            {"default-iana", "PENDING_TECH_CHECK"},
            {"default-iana", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_MANUAL_REVIEW"},
            {"gstsignaliana", "PENDING_IANA_CHECK"},
            {"gstsignaliana", "PENDING_SUPP_TECH_CHECK"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"gstsignaliana", "PENDING_ZONE_INSERTION"},
            {"gstsignaliana", "PENDING_ZONE_PUBLICATION"},
            {"gstsignaliana", "PENDING_ZONE_TESTING"},
            {"gstsignaliana", "PENDING_DATABASE_INSERTION"}
    };

    @Test(dependsOnMethods = {"testREJECT_USDOC_APPROVAL"})
    public void testSuccessfulCreation() throws Exception {
        DomainVO domain = getNextDomain();
        Long transId = createTransaction(domain, userIANA).getTransactionID();
        acceptPENDING_CREATION(transId);
        acceptPENDING_CONTACT_CONFIRMATION(userAC, transId, 2);
        acceptMANUAL_REVIEW(userIANA, transId);
        acceptIANA_CHECK(userIANA, transId);
        acceptUSDOC_APPROVAL(userUSDoC, transId);
        acceptZONE_INSERTION(userIANA, transId);
        acceptZONE_PUBLICATION(userIANA, transId);
        checkStateLog(userAC, transId, workFlowWithNSChangeLog);

        try {
            setUser(userAC);
            IDomainVO retrievedDomain = gsds.getDomain(domain.getName());
            assert retrievedDomain != null;
            assert retrievedDomain.getAdminContact() != null;
            assertEquals(domain.getAdminContact(), retrievedDomain.getAdminContact());
            assert domain.getBreakpoints() != null ? domain.getBreakpoints().equals(retrievedDomain.getBreakpoints()) :
                    retrievedDomain.getBreakpoints() == null;
            assert domain.getName() != null ? domain.getName().equals(retrievedDomain.getName()) : retrievedDomain.getName() == null;
            assert domain.getNameServers() != null;
            assert domain.getNameServers().size() == 1;
            assertEquals(domain.getNameServers().iterator().next(), retrievedDomain.getNameServers().iterator().next());
            assert domain.getRegistryUrl() != null ? domain.getRegistryUrl().equals(retrievedDomain.getRegistryUrl()) :
                    retrievedDomain.getRegistryUrl() == null;
            assert domain.getSpecialInstructions() != null ? domain.getSpecialInstructions().equals(retrievedDomain.getSpecialInstructions()) :
                    retrievedDomain.getSpecialInstructions() == null;
            assert domain.getStatus() != null ? domain.getStatus().equals(retrievedDomain.getStatus()) : retrievedDomain.getStatus() == null;
            assert domain.getSupportingOrg() != null;
            assertEquals(domain.getSupportingOrg(), retrievedDomain.getSupportingOrg());
            assert retrievedDomain.getTechContact() != null;
            assertEquals(domain.getTechContact(), retrievedDomain.getTechContact());
            assert domain.getWhoisServer() != null ? domain.getWhoisServer().equals(retrievedDomain.getWhoisServer()) :
                    retrievedDomain.getWhoisServer() == null;
        } finally {
            closeServices();
        }
    }

    @Test(dependsOnMethods = {"testSuccessfulCreation"}, expectedExceptions = NoDomainSystemUsersException.class)
    public void testCreationFailed() throws Exception {
        createTransaction(getNextDomain(), userIANA);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
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

    private DomainVO getNextDomain() {
        Domain domain = createDomain(DOMAIN_NAME_BASE + domainCounter++);
        Host host = new Host("ns1." + domain.getName());
        host.addIPAddress("4.3.2.1");
        domain.addNameServer(host);
        return DomainToVOConverter.toDomainVO(domain);
    }

    private void assertEquals(ContactVO c1, ContactVO c2) {
        assert c1.getAddress() != null ? c1.getAddress().equals(c2.getAddress()) : c2.getAddress() == null;
        assert c1.getEmail() != null ? c1.getEmail().equals(c2.getEmail()) : c2.getEmail() == null;
        assert c1.getFaxNumber() != null ? c1.getFaxNumber().equals(c2.getFaxNumber()) : c2.getFaxNumber() == null;
        assert c1.getName() != null ? c1.getName().equals(c2.getName()) : c2.getName() == null;
        assert c1.getOrganization() != null ? c1.getOrganization().equals(c2.getOrganization()) : c2.getOrganization() == null;
        assert c1.getPhoneNumber() != null ? c1.getPhoneNumber().equals(c2.getPhoneNumber()) : c2.getPhoneNumber() == null;
    }

    private void assertEquals(HostVO h1, HostVO h2) {
        assert h1.getAddresses() != null ? h1.getAddresses().equals(h2.getAddresses()) : h2.getAddresses() == null;
        assert h1.getName() != null ? h1.getName().equals(h2.getName()) : h2.getName() == null;
    }

    private TransactionVO createTransaction(DomainVO domainVO, RZMUser user) throws Exception {
        setUser(user);  //iana
        TransactionVO transaction = ats.createCreationTransaction(domainVO);
        closeServices();
        return transaction;
    }
}
