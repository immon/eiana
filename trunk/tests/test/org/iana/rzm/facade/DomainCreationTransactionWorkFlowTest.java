package org.iana.rzm.facade;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.admin.AdminTransactionService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.*;
import org.iana.rzm.facade.system.trans.NoDomainSystemUsersException;
import org.iana.rzm.facade.system.trans.SystemTransactionService;
import org.iana.rzm.facade.system.trans.TransactionStateLogEntryVO;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.conf.DefinedTestProcess;
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
@Test(sequential = true, groups = {"facade-system", "DomainCreationTransactionWorkFlowTest"})
public class DomainCreationTransactionWorkFlowTest {
    private final static String DOMAIN_NAME_BASE = "createtranstest";

    private ProcessDAO processDAO;
    private UserManager userManager;
    private DomainManager domainManager;

    private AdminTransactionService gats;
    private SystemTransactionService gsts;
    private SystemDomainService gsds;

    private RZMUser userAC, userTC, userIANA, userUSDoC;
    int domainCounter = 0;

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        gats = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        gsts = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        gsds = (SystemDomainService) appCtx.getBean("GuardedSystemDomainService");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

        userAC = new RZMUser();
        userAC.setLoginName("gstsignaluser");
        userAC.setFirstName("ACuser");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        for (int i = 0; i < 9; i++) {
            userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME_BASE + i, true, true));
            userAC.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME_BASE + i, true, true));
        }
        userManager.create(userAC);

        userTC = new RZMUser();
        userTC.setLoginName("gstsignalseconduser");
        userTC.setFirstName("TCuser");
        userTC.setLastName("lastName");
        userTC.setEmail("email@some.com");
        for (int i = 0; i < 9; i++) {
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
            processDAO.deploy(DefinedTestProcess.getDefinition(DefinedTestProcess.DOMAIN_CREATION));
        } finally {
            processDAO.close();
        }
    }

    private static final String[][] REJECT_CONTACT_CONFIRMATIONLog = {
            {"gstsignaluser", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test
    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        rejectPENDING_CONTACT_CONFIRMATION(userAC, transId);
        checkStateLog(userAC, transId, REJECT_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] CLOSE_CONTACT_CONFIRMATIONLog = {
            {"gstsignaliana", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testREJECT_CONTACT_CONFIRMATION"})
    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        closePENDING_CONTACT_CONFIRMATION(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_CONTACT_CONFIRMATIONLog);
    }

    private static final String[][] ACCEPT_CONTAC_CONFIRMATIONLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"}
    };

    @Test(dependsOnMethods = {"testCLOSE_CONTACT_CONFIRMATION"})
    public void testACCEPT_CONTACT_CONFIRMATION() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        checkStateLog(userAC, transId, ACCEPT_CONTAC_CONFIRMATIONLog);
    }

    private static final String[][] REJECT_IMPACTED_PARTIESLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"}
    };

    @Test(dependsOnMethods = {"testACCEPT_CONTACT_CONFIRMATION"})
    public void testREJECT_IMPACTED_PARTIES() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        rejectIMPACTED_PARTIES(userAC, transId);
        checkStateLog(userAC, transId, REJECT_IMPACTED_PARTIESLog);
    }

    private static final String[][] CLOSE_IMPACTED_PARTIESLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaliana", "PENDING_IMPACTED_PARTIES"}
    };

    @Test(dependsOnMethods = {"testREJECT_IMPACTED_PARTIES"})
    public void testCLOSE_IMPACTED_PARTIES() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        closeIMPACTED_PARTIES(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_IMPACTED_PARTIESLog);
    }

    private static final String[][] REJECT_EXT_APPROVALLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testCLOSE_IMPACTED_PARTIES"})
    public void testREJECT_EXT_APPROVAL() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        rejectEXT_APPROVAL(userIANA, transId);
        checkStateLog(userAC, transId, REJECT_EXT_APPROVALLog);
    }

    private static final String[][] CLOSE_EXT_APPROVALLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testREJECT_EXT_APPROVAL"})
    public void testCLOSE_EXT_APPROVAL() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        closeEXT_APPROVAL(userIANA, transId);
        checkStateLog(userAC, transId, CLOSE_EXT_APPROVALLog);
    }

    private static final String[][] REJECT_USDOC_APPROVALLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"}
    };

    @Test(dependsOnMethods = {"testCLOSE_EXT_APPROVAL"})
    public void testREJECT_USDOC_APPROVAL() throws Exception {
        Long transId = createTransaction(getNextDomain(), userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        rejectUSDOC_APPROVAL(userUSDoC, transId);
        checkStateLog(userAC, transId, REJECT_USDOC_APPROVALLog);
    }

    private static final String[][] workFlowWithNSChangeLog = {
            {"gstsignalseconduser", "PENDING_CONTACT_CONFIRMATION"},
            {"gstsignaluser", "PENDING_IMPACTED_PARTIES"},
            {"gstsignaliana", "PENDING_IANA_CONFIRMATION"},
            {"gstsignaliana", "PENDING_EXT_APPROVAL"},
            {"gstsignalusdoc", "PENDING_USDOC_APPROVAL"},
            {"gstsignaliana", "PENDING_ZONE_INSERTION"},
            {"gstsignaliana", "PENDING_ZONE_PUBLICATION"},
    };

    @Test(dependsOnMethods = {"testREJECT_USDOC_APPROVAL"})
    public void testSuccessfulCreation() throws Exception {
        DomainVO domain = getNextDomain();
        Long transId = createTransaction(domain, userIANA).getTransactionID();
        acceptPENDING_CONTACT_CONFIRMATION(userAC, userTC, transId);
        acceptIMPACTED_PARTIES(userAC, transId);
        normalIANA_CONFIRMATION(userIANA, transId);
        acceptEXT_APPROVAL(userIANA, transId);
        acceptUSDOC_APPROVAL(userUSDoC, transId);
        acceptZONE_INSERTION(userIANA, transId);
        acceptZONE_PUBLICATION(userIANA, transId);
        checkStateLog(userAC, transId, workFlowWithNSChangeLog);

        try {
            setGSDSAuthUser(userAC);
            IDomainVO retrievedDomain = gsds.getDomain(domain.getName());
            assert retrievedDomain != null;
            assert retrievedDomain.getAdminContacts() != null;
            assert retrievedDomain.getAdminContacts().size() == 1;
            assertEquals(domain.getAdminContacts().iterator().next(), retrievedDomain.getAdminContacts().iterator().next());
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
            assert domain.getState() != null ? domain.getState().equals(retrievedDomain.getState()) : retrievedDomain.getState() == null;
            assert domain.getStatus() != null ? domain.getStatus().equals(retrievedDomain.getStatus()) : retrievedDomain.getStatus() == null;
            assert domain.getSupportingOrg() != null;
            assertEquals(domain.getSupportingOrg(), retrievedDomain.getSupportingOrg());
            assert retrievedDomain.getTechContacts() != null;
            assert retrievedDomain.getTechContacts().size() == 1;
            assertEquals(domain.getTechContacts().iterator().next(), retrievedDomain.getTechContacts().iterator().next());
            assert domain.getWhoisServer() != null ? domain.getWhoisServer().equals(retrievedDomain.getWhoisServer()) :
                    retrievedDomain.getWhoisServer() == null;
        } finally {
            gsds.close();
        }
    }

    @Test(dependsOnMethods = {"testSuccessfulCreation"}, expectedExceptions = NoDomainSystemUsersException.class)
    public void testCreationFailed() throws Exception {
        createTransaction(getNextDomain(), userIANA);
    }

    @AfterClass (alwaysRun = true)
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
        return ToVOConverter.toDomainVO(createDomain(DOMAIN_NAME_BASE + domainCounter++));
    }

    private Domain createDomain(String name) {
        Domain domain = new Domain(name);
        domain.setSupportingOrg(createContact(name + "-supp"));
        domain.addTechContact(createContact(name + "-tech"));
        domain.addAdminContact(createContact(name + "-admin"));
        Host host = new Host("ns1." + name);
        host.addIPAddress("4.3.2.1");
        domain.addNameServer(host);
        domain.setRegistryUrl("registry." + name);
        domain.setWhoisServer("whois." + name);
        return domain;
    }

    private Contact createContact(String prefix) {
        Contact contact = new Contact(prefix, prefix + "org");
        contact.addAddress(new Address(prefix + "addr", "US"));
        contact.addEmail(prefix + "@no-mail.org");
        contact.addFaxNumber("+1234567890");
        contact.addPhoneNumber("+1234567890");
        return contact;
    }

    private void assertEquals(ContactVO c1, ContactVO c2) {
        assert c1.getAddresses().equals(c2.getAddresses());
        assert c1.getEmails().equals(c2.getEmails());
        assert c1.getFaxNumbers().equals(c2.getFaxNumbers());
        assert c1.getName().equals(c2.getName());
        assert c1.getOrganization().equals(c2.getOrganization());
        assert c1.getPhoneNumbers().equals(c2.getPhoneNumbers());
    }

    private void assertEquals(HostVO h1, HostVO h2) {
        assert h1.getAddresses().equals(h2.getAddresses());
        assert h1.getName().equals(h2.getName());
    }

    private void acceptZONE_PUBLICATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);     //iana
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("COMPLETED", transId);
        gsts.close();
    }

    private void acceptZONE_INSERTION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //iana
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION", transId);
        gsts.close();
    }

    private void acceptUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);   //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION", transId);
        gsts.close();
    }

    private void acceptEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.close();
    }

    private void normalIANA_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION", transId);
        gsts.transitTransaction(transId, "normal");
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.close();
    }

    private void acceptIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION", transId);
        gsts.close();
    }

    private void rejectPENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    private void closeEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        gsts.close();
    }

    private void rejectEXT_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    private void rejectUSDOC_APPROVAL(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //USDoC
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    private void rejectIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.rejectTransaction(transId);
        assert isTransactionInDesiredState("REJECTED", transId);
        gsts.close();
    }

    private void closeIMPACTED_PARTIES(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user); //userAC
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        gsts.close();
    }

    private void closePENDING_CONTACT_CONFIRMATION(RZMUser user, long transId) throws Exception {
        setGSTSAuthUser(user);  //iana
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.transitTransaction(transId, "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED", transId);
        gsts.close();
    }

    private void acceptPENDING_CONTACT_CONFIRMATION(RZMUser firstUser, RZMUser secondUser, long transId) throws Exception {
        setGSTSAuthUser(firstUser); //userAC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.close();
        setGSTSAuthUser(secondUser); //userTC
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION", transId);
        gsts.acceptTransaction(transId);
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES", transId);
        gsts.close();
    }

    private void setGSTSAuthUser(RZMUser user) {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gsts.setUser(testAuthUser);
    }

    private void setGATSAuthUser(RZMUser user) {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gats.setUser(testAuthUser);
    }

    private void setGSDSAuthUser(RZMUser user) {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gsds.setUser(testAuthUser);
    }

    private TransactionVO createTransaction(DomainVO domainVO, RZMUser user) throws Exception {
        setGATSAuthUser(user);  //iana
        TransactionVO transaction = gats.createDomainCreationTransaction(domainVO);
        gsts.close();
        return transaction;
    }

    private boolean isTransactionInDesiredState(String stateName, long transId) throws Exception {
        TransactionVO retTransactionVO = gsts.getTransaction(transId);
        return retTransactionVO.getState().getName().toString().equals(stateName);
    }

    private void checkStateLog(RZMUser user, Long transId, String[][] usersStates) throws Exception {
        setGSTSAuthUser(user);
        TransactionVO trans = gsts.getTransaction(transId);
        List<TransactionStateLogEntryVO> log = trans.getStateLog();
        assert log != null;
        assert log.size() == usersStates.length;
        int i = 0;
        for (TransactionStateLogEntryVO entry : log) {
            assert usersStates[i][0].equals(entry.getUserName()) :
                    "unexpected user in log entry: " + i + ", " + usersStates[i][0];
            assert usersStates[i][1].equals(entry.getState().getName().toString()) :
                    "unexpected state in log entry: " + i + ", " + usersStates[i][1];
            i++;
        }
    }
}
