package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
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

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "SetTransactionTicketIdTest"})
public class SetTransactionTicketIdTest {

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

    @Test
    public void testSetTransactionTicketId() throws Exception {
        createDomainModificationProcess();
        TransactionVO transactionVO = gAdminTransactionServ.get(transactionID);
        assert !new Long(123l).equals(transactionVO.getTicketID());
        transactionVO.setTicketID(123L);
        gAdminTransactionServ.updateTransaction(transactionVO);
        TransactionVO retTransactionVO = gAdminTransactionServ.get(transactionID);
        assert retTransactionVO.getTicketID() != transactionVO.getTicketID();
        assert retTransactionVO.getTicketID() == 123L;
    }

    @Test
    public void testUpdateUSDoCNotes() throws Exception {
        String USDOC_NOTES = "USDOC_NOTES";
        createDomainModificationProcess();
        TransactionVO transactionVO = gAdminTransactionServ.get(transactionID);
        assert null == transactionVO.getUsdocNotes();
        transactionVO.setUsdocNotes(USDOC_NOTES);
        gAdminTransactionServ.updateTransaction(transactionVO);
        TransactionVO retTransactionVO = gAdminTransactionServ.get(transactionID);
        assert USDOC_NOTES.equals(retTransactionVO.getUsdocNotes());
    }

    private void createDomainModificationProcess() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminTransactionServ.setUser(testAuthUser);

        Domain domain = createTestDomain(DOMAIN_NAME);
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = gAdminTransactionServ.createTransactions(DomainToVOConverter.toDomainVO(domain), false).get(0);
        transactionID = transactionVO.getTransactionID();

        transactionVO = gAdminTransactionServ.get(transactionID);

        assert transactionVO != null;

        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
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
    
    @AfterClass (alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }

}
