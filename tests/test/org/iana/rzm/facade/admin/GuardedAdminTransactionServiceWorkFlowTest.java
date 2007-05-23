package org.iana.rzm.facade.admin;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.springframework.context.ApplicationContext;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.system.trans.TransactionStateVO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "GuardedAdminTransactionServiceTest"})
public class GuardedAdminTransactionServiceWorkFlowTest {

    ApplicationContext appCtx;
    AdminTransactionService gAdminTransactionServ;

    UserManager userManager;
    DomainManager domainManager;
    ProcessDAO processDAO;
    RZMUser user, wrongUser, domainUser;

    Domain domain;

    Long transactionID;

    private final static String DOMAIN_NAME = "gatstestdomain.org";
    private final static String PROCESS_NAME = "Domain Modification Transaction (Unified Workflow)";

    @BeforeClass
    public void init() {
        appCtx = SpringApplicationContext.getInstance().getContext();
        gAdminTransactionServ = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        userManager = (UserManager) appCtx.getBean("userManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainManager = (DomainManager) appCtx.getBean("domainManager");

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

        user = new RZMUser();
        user.setLoginName("gatsadminuser");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@some.com");
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        this.userManager.create(user);


        wrongUser = new RZMUser();
        wrongUser.setLoginName("gatswronguser");
        wrongUser.setFirstName("firstName");
        wrongUser.setLastName("lastName");
        wrongUser.setEmail("email@some.com");
        wrongUser.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        this.userManager.create(wrongUser);

        domainUser = new RZMUser();
        domainUser.setLoginName("gatsdomainuser");
        domainUser.setFirstName("firstName");
        domainUser.setLastName("lastName");
        domainUser.setEmail("email@some.com");
        domainUser.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME, true, true));
        this.userManager.create(domainUser);

        domain = createTestDomain(DOMAIN_NAME);
        domainManager.create(domain);

    }

    @Test (expectedExceptions = {AccessDeniedException.class})
    public void testCreateTransactionByWrongUser() throws Exception {
        try {
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(wrongUser)).getAuthUser();
            gAdminTransactionServ.setUser(testAuthUser);
            gAdminTransactionServ.createDomainModificationTransaction(ToVOConverter.toDomainVO(domain));
        } catch (AccessDeniedException e) {
            gAdminTransactionServ.close();
            throw e;
        }
    }

    @Test
    public void testRejectCONTACT_CONFIRMATION() throws Exception {
        createDomainModificationProcess();
        rejectPENDING_CONTACT_CONFIRMATION();
    }

    @Test (dependsOnMethods = "testRejectCONTACT_CONFIRMATION")
    public void testAcceptCONTACT_CONFIRMATION() throws Exception {
        createDomainModificationProcess();
        acceptPENDING_CONTACT_CONFIRMATION();
    }
//    todo
//    @Test (dependsOnMethods = "testAcceptCONTACT_CONFIRMATION")
//    public void testRejectIMPACTED_PARTIES() throws Exception {
//        createDomainModificationProcess();
//        acceptPENDING_CONTACT_CONFIRMATION();
//        rejectPENDING_IMPACTED_PARTIES();
//    }
//
//    @Test (dependsOnMethods = "testRejectIMPACTED_PARTIES")
//    public void testAcceptIMPACTED_PARTIES() throws Exception {
//        createDomainModificationProcess();
//        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES();
//    }

    @Test (dependsOnMethods = "testAcceptCONTACT_CONFIRMATION")
    public void testNormalPENDING_IANA_CONFIRMATION() throws Exception {
        createDomainModificationProcess();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
    }

    @Test (dependsOnMethods = "testNormalPENDING_IANA_CONFIRMATION")
    public void testRejectPENDING_EXT_APPROVAL() throws Exception {
        createDomainModificationProcess();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        rejectPENDING_EXT_APPROVAL();
    }

    @Test (dependsOnMethods = "testRejectPENDING_EXT_APPROVAL")
    public void testAcceptPENDING_EXT_APPROVAL() throws Exception {
        createDomainModificationProcess();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        acceptPENDING_EXT_APPROVAL();
    }

    @Test (dependsOnMethods = "testAcceptPENDING_EXT_APPROVAL")
    public void testRejectPENDING_USDOC_APPROVAL() throws Exception {
        createDomainModificationProcess();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        acceptPENDING_EXT_APPROVAL();
        rejectPENDING_USDOC_APPROVAL();
    }

    @Test (dependsOnMethods = "testRejectPENDING_USDOC_APPROVAL")
    public void testAcceptPENDING_USDOC_APPROVALNSCHANGE() throws Exception {
        createDomainModificationProcessNSChage();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        acceptPENDING_EXT_APPROVAL();
        acceptPENDING_USDOC_APPROVALNSCHANGE();
    }

    @Test (dependsOnMethods = "testAcceptPENDING_USDOC_APPROVALNSCHANGE")
    public void testAcceptPENDING_USDOC_APPROVAL() throws Exception {
        createDomainModificationProcess();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        acceptPENDING_EXT_APPROVAL();
        acceptPENDING_USDOC_APPROVAL();
    }

    @Test (dependsOnMethods = "testAcceptPENDING_USDOC_APPROVAL")
    public void testAcceptPENDING_ZONE_INSERTION() throws Exception {
        createDomainModificationProcessNSChage();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        acceptPENDING_EXT_APPROVAL();
        acceptPENDING_USDOC_APPROVALNSCHANGE();
        acceptPENDING_ZONE_INSERTION();
    }

    @Test (dependsOnMethods = "testAcceptPENDING_ZONE_INSERTION")
    public void testAcceptPENDING_ZONE_PUBLICATION() throws Exception {
        createDomainModificationProcessNSChage();
        acceptPENDING_CONTACT_CONFIRMATION();
//        acceptPENDING_IMPACTED_PARTIES(); todo
        normalPENDING_IANA_CONFIRMATION();
        acceptPENDING_EXT_APPROVAL();
        acceptPENDING_USDOC_APPROVALNSCHANGE();
        acceptPENDING_ZONE_INSERTION();
        acceptPENDING_ZONE_PUBLICATION();
    }





    private void createDomainModificationProcess() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminTransactionServ.setUser(testAuthUser);

        Domain domain = createTestDomain(DOMAIN_NAME);
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = gAdminTransactionServ.createDomainModificationTransaction(ToVOConverter.toDomainVO(domain));
        transactionID = transactionVO.getTransactionID();

        transactionVO = gAdminTransactionServ.getTransaction(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
        assert transactionVO.getName().equals(PROCESS_NAME);
    }

    private void createDomainModificationProcessNSChage() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminTransactionServ.setUser(testAuthUser);

        Domain domain = createTestDomain(DOMAIN_NAME);
        Host newHost = new Host("ns123.ultra.net");
        newHost.addIPAddress("81.10.10.50");
        domain.addNameServer(newHost);

        TransactionVO transactionVO = gAdminTransactionServ.createDomainModificationTransaction(ToVOConverter.toDomainVO(domain));
        transactionID = transactionVO.getTransactionID();

        transactionVO = gAdminTransactionServ.getTransaction(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
        assert transactionVO.getName().equals(PROCESS_NAME);
    }


    private void rejectPENDING_CONTACT_CONFIRMATION() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
        gAdminTransactionServ.rejectTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.REJECTED);
    }

    private void acceptPENDING_CONTACT_CONFIRMATION() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
        gAdminTransactionServ.acceptTransaction(transactionID);
//        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_IMPACTED_PARTIES); todo
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_IANA_CONFIRMATION);
    }

    private void rejectPENDING_IMPACTED_PARTIES() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_IMPACTED_PARTIES);
        gAdminTransactionServ.rejectTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.REJECTED);
    }

    private void acceptPENDING_IMPACTED_PARTIES() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_IMPACTED_PARTIES);
        gAdminTransactionServ.acceptTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_IANA_CONFIRMATION);
    }

    private void normalPENDING_IANA_CONFIRMATION() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_IANA_CONFIRMATION);
        gAdminTransactionServ.transitTransaction(transactionID, "normal");
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_EXT_APPROVAL);
    }

    private void rejectPENDING_EXT_APPROVAL() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_EXT_APPROVAL);
        gAdminTransactionServ.rejectTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.REJECTED);
    }

    private void acceptPENDING_EXT_APPROVAL() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_EXT_APPROVAL);
        gAdminTransactionServ.acceptTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_USDOC_APPROVAL);
    }

    private void rejectPENDING_USDOC_APPROVAL() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_USDOC_APPROVAL);
        gAdminTransactionServ.rejectTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.REJECTED);
    }

    private void acceptPENDING_USDOC_APPROVAL() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_USDOC_APPROVAL);
        gAdminTransactionServ.acceptTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.COMPLETED);
    }

    private void acceptPENDING_USDOC_APPROVALNSCHANGE() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_USDOC_APPROVAL);
        gAdminTransactionServ.acceptTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_ZONE_INSERTION);
    }

    private void acceptPENDING_ZONE_INSERTION() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_ZONE_INSERTION);
        gAdminTransactionServ.acceptTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_ZONE_PUBLICATION);
    }

    private void acceptPENDING_ZONE_PUBLICATION() throws Exception {
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.PENDING_ZONE_PUBLICATION);
        gAdminTransactionServ.acceptTransaction(transactionID);
        assert isTransactionInDesiredState(transactionID, TransactionStateVO.Name.COMPLETED);
    }

    private boolean isTransactionInDesiredState(Long transactionID, TransactionStateVO.Name transactionStateVOName) throws Exception {
        TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
                                                  
        assert transactionVO != null;
        return transactionVO.getState().getName().equals(transactionStateVOName);
    }

    private Domain createTestDomain(String domainName) {
        Domain newDomain = new Domain(domainName);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
    }

    @AfterClass
    public void cleanUp() {
        
        List<ProcessInstance> processInstances = processDAO.findAll();
        for (ProcessInstance processInstance : processInstances)
            processDAO.delete(processInstance);
        processDAO.close();

        domainManager.delete(DOMAIN_NAME);

        userManager.delete(user);
        userManager.delete(wrongUser);
        userManager.delete(domainUser);
    }
}
