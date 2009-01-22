package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */

@Test(sequential = true, groups = {"test", "TransitTransactionToStateTest"})
public class TransitTransactionToStateTest {

    static Map<String, String> states = new HashMap<String, String>();

    ApplicationContext appCtx;
    AdminTransactionService gAdminTransactionServ;

    UserManager userManager;
    DomainManager domainManager;
    ProcessDAO processDAO;
    RZMUser user;

    Long transactionID;

    private final static String DOMAIN_NAME_1 = "gatstestdomain-1.org";
    private final static String DOMAIN_NAME_2 = "gatstestdomain-2.org";
    private final static String PROCESS_NAME = "Domain Modification Transaction (Unified Workflow)";

    @BeforeClass
    public void init() {
        try {
            appCtx = SpringApplicationContext.getInstance().getContext();
            gAdminTransactionServ = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
            userManager = (UserManager) appCtx.getBean("userManager");
            processDAO = (ProcessDAO) appCtx.getBean("processDAO");
            domainManager = (DomainManager) appCtx.getBean("domainManager");

            fillStates();

            user = new RZMUser();
            user.setLoginName("gatsadminuser");
            user.setFirstName("firstName");
            user.setLastName("lastName");
            user.setEmail("email@some.com");
            user.addRole(new AdminRole(AdminRole.AdminType.IANA));
            this.userManager.create(user);

            Domain domain = createTestDomain(DOMAIN_NAME_1);
            domainManager.create(domain);
            domain = createTestDomain(DOMAIN_NAME_2);
            domainManager.create(domain);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Test(expectedExceptions = FacadeTransactionException.class)
    public void testTransitTransactionToWrongState() throws Exception {

        try {
            createDomainModificationProcess(DOMAIN_NAME_1);

            TransactionVO transactionVO = gAdminTransactionServ.get(transactionID);
            assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION);

            gAdminTransactionServ.transitTransactionToState(transactionID, "WRONG_STATE");

        } catch (FacadeTransactionException e) {
            assert e.getMessage().equals("no such state: WRONG_STATE");
            throw e;
        }
    }

    @Test
    public void testTransitTransactionToState() throws Exception {
        for (String state : states.keySet()) {
            processDAO.deleteAll();
            createDomainModificationProcess(DOMAIN_NAME_2);
            gAdminTransactionServ.transitTransactionToState(transactionID, state);
            TransactionVO transactionVO = gAdminTransactionServ.get(transactionID);
            String expectedState = states.get(state);
            assert expectedState != null;
            assert expectedState.equals(transactionVO.getState().getName().name()) :
                    "unexpected state: " + transactionVO.getState().getName().name() +
                            ", expected: " + expectedState;
        }
    }

    private void createDomainModificationProcess(String domainName) throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminTransactionServ.setUser(testAuthUser);

        Domain domain = createTestDomain(domainName);
        domain.setRegistryUrl("newregurl" + new Random().nextInt());

        List<TransactionVO> transactionList = gAdminTransactionServ.createTransactions(DomainToVOConverter.toDomainVO(domain), false);
        assert transactionList.size() == 1;
        TransactionVO transactionVO = transactionList.get(0);
        transactionID = transactionVO.getTransactionID();

        transactionVO = gAdminTransactionServ.get(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(domainName);
        assert transactionVO.getName().equals(PROCESS_NAME);
    }

    private Domain createTestDomain(String domainName) {
        Domain newDomain = new Domain(domainName);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        processDAO.deleteAll();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

    private static void fillStates() {
        states.put("PENDING_CREATION", "PENDING_CREATION");
        states.put("PENDING_TECH_CHECK", "PENDING_CONTACT_CONFIRMATION");
        states.put("PENDING_TECH_CHECK_REMEDY", "PENDING_TECH_CHECK_REMEDY");
        states.put("PENDING_CONTACT_CONFIRMATION", "PENDING_CONTACT_CONFIRMATION");
        states.put("PENDING_SOENDORSEMENT", "PENDING_SOENDORSEMENT");
        states.put("PENDING_IMPACTED_PARTIES", "PENDING_IMPACTED_PARTIES");
        states.put("PENDING_MANUAL_REVIEW", "PENDING_MANUAL_REVIEW");
        states.put("PENDING_EXT_APPROVAL", "PENDING_EXT_APPROVAL");
        states.put("PENDING_EVALUATION", "PENDING_EVALUATION");
        states.put("PENDING_IANA_CHECK", "PENDING_IANA_CHECK");
        states.put("PENDING_SUPP_TECH_CHECK", "PENDING_USDOC_APPROVAL");
        states.put("PENDING_SUPP_TECH_CHECK_REMEDY", "PENDING_SUPP_TECH_CHECK_REMEDY");
        states.put("PENDING_USDOC_APPROVAL", "PENDING_USDOC_APPROVAL");
        states.put("PENDING_ZONE_INSERTION", "PENDING_ZONE_INSERTION");
        states.put("PENDING_ZONE_PUBLICATION", "PENDING_ZONE_PUBLICATION");
        states.put("PENDING_ZONE_TESTING", "COMPLETED");
        states.put("PENDING_DATABASE_INSERTION", "COMPLETED");
        states.put("COMPLETED", "COMPLETED");
        states.put("WITHDRAWN", "WITHDRAWN");
        states.put("REJECTED", "REJECTED");
        states.put("ADMIN_CLOSED", "ADMIN_CLOSED");
        states.put("EXCEPTION", "EXCEPTION");
    }
}
