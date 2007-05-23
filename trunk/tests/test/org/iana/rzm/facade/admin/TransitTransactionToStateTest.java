package org.iana.rzm.facade.admin;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.springframework.context.ApplicationContext;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.system.trans.TransactionStateVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "TransitTransactionToStateTest"})
public class TransitTransactionToStateTest {

    ApplicationContext appCtx;
    AdminTransactionService gAdminTransactionServ;

    UserManager userManager;
    DomainManager domainManager;
    ProcessDAO processDAO;
    RZMUser user;

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

        domain = createTestDomain(DOMAIN_NAME);
        domainManager.create(domain);

    }

    @Test (expectedExceptions = NoSuchStateException.class)
    public void testTransitTransactionToWrongState() throws Exception {

        try {
            createDomainModificationProcess();

            TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
            assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);

            gAdminTransactionServ.transitTransactionToState(transactionID, "WRONG_STATE");

        } catch (NoSuchStateException e) {
            assert e.getStateName().equals("WRONG_STATE");
            throw e;
        }
    }

    @Test (expectedExceptions = StateUnreachableException.class)
    public void testTransitTransactionToUnreachableState() throws Exception {
        try {
            createDomainModificationProcess();
            TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
            assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
            gAdminTransactionServ.transitTransactionToState(transactionID, TransactionStateVO.Name.PENDING_ZONE_PUBLICATION);
        } catch(StateUnreachableException e) {
            assert e.getStateName().equals("PENDING_ZONE_PUBLICATION");
            throw e;
        }
    }

    @Test
    public void testTransitTransactionToStateWithNSChange() throws Exception {
        createDomainModificationProcessNSChange();

        TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);


        gAdminTransactionServ.transitTransactionToState(transactionID, TransactionStateVO.Name.PENDING_ZONE_PUBLICATION);
        transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_ZONE_PUBLICATION);

        gAdminTransactionServ.transitTransactionToState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
        transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
    }

    @Test
    public void testTransitTransactionToState() throws Exception {
        createDomainModificationProcess();

        TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);


        gAdminTransactionServ.transitTransactionToState(transactionID, TransactionStateVO.Name.PENDING_USDOC_APPROVAL);
        transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_USDOC_APPROVAL);

        gAdminTransactionServ.transitTransactionToState(transactionID, TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
        transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);
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

    private void createDomainModificationProcessNSChange() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminTransactionServ.setUser(testAuthUser);

        Domain domain = createTestDomain(DOMAIN_NAME);
        domain.addNameServer(new Host("ns1.ultrans.net"));
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = gAdminTransactionServ.createDomainModificationTransaction(ToVOConverter.toDomainVO(domain));
        transactionID = transactionVO.getTransactionID();

        transactionVO = gAdminTransactionServ.getTransaction(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
        assert transactionVO.getName().equals(PROCESS_NAME);
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
    }
}
