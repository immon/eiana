package org.iana.rzm.facade.system.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.user.*;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.springframework.context.ApplicationContext;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"facade-system", "GuardedSystemTransactionWorkFlowTest"})
public class GuardedSystemTransactionWorkFlowTest {

    ApplicationContext appCtx;
    SystemTransactionService gsts;

    ProcessDAO processDAO;
    UserManager userManager;
    DomainManager domainManager;
    RZMUser userAC, userTC, userIANA, userUSDoC;
    DomainVO domainVONS, domainVO;
    Domain domainNS, domain;

    private TransactionVO transaction;

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

//    @Test
//    public void testREJECT_CONTACT_CONFIRMATION() throws Exception {
//        createTransaction(domainVONS);
//        rejectPENDING_CONTACT_CONFIRMATION();
//    }
//
//    @Test
//    public void testCLOSE_CONTACT_CONFIRMATION() throws Exception {
//        createTransaction(domainVONS);
//        closePENDING_CONTACT_CONFIRMATION();
//    }
//
//    @Test
//    public void testACCEPT_CONTAC_CONFIRMATION() throws Exception {
//        createTransaction(domainVONS);
//        acceptPENDING_CONTACT_CONFIRMATION();
//    }

//    @Test
//    public void testREJECT_IMPACTED_PARTIES() throws Exception {
//        createTransaction(domainVONS);
//        acceptPENDING_CONTACT_CONFIRMATION();
//        rejectIMPACTED_PARTIES();
//    }

    @Test
    public void testWorkFlowNoNSChange() throws Exception {
        createTransaction(domainVO);
        acceptPENDING_CONTACT_CONFIRMATION();
        acceptIMPACTED_PARTIES();
        normalIANA_CONFIRMATION();
        acceptEXT_APPROVAL();
        acceptUSDOC_APPROVALnoNSChange();
    }

    @Test
    public void testWorkFlowWithNSChange() throws Exception {
        createTransaction(domainVONS);
        acceptPENDING_CONTACT_CONFIRMATION();
        acceptIMPACTED_PARTIES();
        normalIANA_CONFIRMATION();
        acceptEXT_APPROVAL();
        acceptUSDOC_APPROVAL();
        acceptZONE_INSERTION();
        acceptZONE_PUBLICATION();
    }

    private void acceptZONE_PUBLICATION() throws Exception {
        setGSTSAuthUser(userIANA);
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("COMPLETED");
        gsts.close();
    }

    private void acceptZONE_INSERTION() throws Exception {
        setGSTSAuthUser(userIANA);
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("PENDING_ZONE_PUBLICATION");
        gsts.close();
    }

    private void acceptUSDOC_APPROVAL() throws Exception {
        setGSTSAuthUser(userUSDoC);
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("PENDING_ZONE_INSERTION");
        gsts.close();
    }

    private void acceptUSDOC_APPROVALnoNSChange() throws Exception {
        setGSTSAuthUser(userUSDoC);
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("COMPLETED");
        gsts.close();
    }

    private void acceptEXT_APPROVAL() throws Exception {
        setGSTSAuthUser(userAC);
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("PENDING_USDOC_APPROVAL");
        gsts.close();
    }

    private void normalIANA_CONFIRMATION() throws Exception {
        setGSTSAuthUser(userIANA);
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION");
        gsts.transitTransaction(transaction.getTransactionID(), "normal");
        assert isTransactionInDesiredState("PENDING_EXT_APPROVAL");
        gsts.close();
    }

    private void acceptIMPACTED_PARTIES() throws Exception {
        setGSTSAuthUser(userAC);
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("PENDING_IANA_CONFIRMATION");
        gsts.close();
    }

    private void rejectIMPACTED_PARTIES() throws Exception {
        setGSTSAuthUser(userAC);
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES");
        gsts.rejectTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("REJECTED");
        gsts.close();
    }

    private void rejectPENDING_CONTACT_CONFIRMATION() throws Exception {
        setGSTSAuthUser(userAC);
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION");
        gsts.rejectTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("REJECTED");
        gsts.close();
    }

    private void closePENDING_CONTACT_CONFIRMATION()  throws Exception {
        setGSTSAuthUser(userIANA);
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION");
        gsts.transitTransaction(transaction.getTransactionID(), "close");
        assert isTransactionInDesiredState("ADMIN_CLOSED");
        gsts.close();
    }

    private void acceptPENDING_CONTACT_CONFIRMATION()  throws Exception {
        setGSTSAuthUser(userAC);
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION");
        gsts.close();
        setGSTSAuthUser(userTC);
        assert isTransactionInDesiredState("PENDING_CONTACT_CONFIRMATION");
        gsts.acceptTransaction(transaction.getTransactionID());
        assert isTransactionInDesiredState("PENDING_IMPACTED_PARTIES");
        gsts.close();
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

    private Domain createDomain(String name) {
        Domain newDomain = new Domain(name);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
    }

    private void setGSTSAuthUser(RZMUser user) {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gsts.setUser(testAuthUser);
    }

    private void createTransaction(IDomainVO domainVO) throws Exception {
//        domainManager.delete(domain);
//        domainManager.create(domain);
        setGSTSAuthUser(userAC);
        transaction = gsts.createTransaction(domainVO);
        gsts.close();
    }

    private boolean isTransactionInDesiredState(String stateName) throws Exception {
        TransactionVO retTransactionVO = gsts.getTransaction(transaction.getTransactionID());
        return retTransactionVO.getState().getName().toString().equals(stateName);
    }
}
