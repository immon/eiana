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
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 * @author: Jakub Laszkiewicz
 */

@Test(sequential = true, groups = {"test", "TransitTransactionToStateTest"})
public class TransitTransactionToStateTest {

    static List<String> states = new ArrayList<String>();

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

        fillStates();

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

    @Test(expectedExceptions = StateUnreachableException.class)
    public void testTransitTransactionToWrongState() throws Exception {

        try {
            createDomainModificationProcess();

            TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
            assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CREATION);

            gAdminTransactionServ.updateTransaction(transactionID, 0L, "WRONG_STATE", false);

        } catch (FacadeTransactionException e) {
            assert e.getMessage().equals("no such state: WRONG_STATE");
            throw e;
        }
    }

    @Test
    public void testTransitTransactionToState() throws Exception {
        createDomainModificationProcess();

        TransactionVO transactionVO = gAdminTransactionServ.getTransaction(transactionID);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CREATION);

        for (String state : states) {
            if (!state.equals("PENDING_IANA_CONFIRMATION") && !state.equals("PENDING_TECH_CHECK") && !state.equals("PENDING_SUPP_TECH_CHECK") && !state.equals("PENDING_DATABASE_INSERTION")) {
                gAdminTransactionServ.updateTransaction(transactionID, 0L, state, false);
                transactionVO = gAdminTransactionServ.getTransaction(transactionID);
                assert state.equals(transactionVO.getState().getName().name()) :
                        "unexpected state: " + transactionVO.getState().getName().name() +
                                ", expected: " + state;
            }
        }
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

    private Domain createTestDomain(String domainName) {
        Domain newDomain = new Domain(domainName);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
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

    private static void fillStates() {
        states.add("PENDING_TECH_CHECK");
        states.add("PENDING_TECH_CHECK_REMEDY");
        states.add("PENDING_CONTACT_CONFIRMATION");
        states.add("PENDING_SOENDORSEMENT");
        states.add("PENDING_IMPACTED_PARTIES");
        states.add("PENDING_MANUAL_REVIEW");
        states.add("PENDING_EXT_APPROVAL");
        states.add("PENDING_EVALUATION");
        states.add("PENDING_IANA_CHECK");
        states.add("PENDING_SUPP_TECH_CHECK");
        states.add("PENDING_SUPP_TECH_CHECK_REMEDY");
        states.add("PENDING_USDOC_APPROVAL");
        states.add("PENDING_ZONE_INSERTION");
        states.add("PENDING_ZONE_PUBLICATION");
        states.add("PENDING_DATABASE_INSERTION");
        states.add("COMPLETED");
        states.add("WITHDRAWN");
        states.add("REJECTED");
        states.add("ADMIN_CLOSED");
        states.add("EXCEPTION");
    }
}
