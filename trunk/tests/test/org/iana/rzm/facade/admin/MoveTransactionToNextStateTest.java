package org.iana.rzm.facade.admin;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(sequential = true, groups = {"test"})
public class MoveTransactionToNextStateTest {

    ApplicationContext appCtx;
    AdminTransactionService gAdminTransactionServ;

    UserManager userManager;
    DomainManager domainManager;
    ProcessDAO processDAO;
    RZMUser user;

    Domain domain;

    Long transactionID;

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

        domain = createTestDomain("movetonextstate");
        domainManager.create(domain);
    }

    @Test
    public void testTransactionToNextState() throws Exception {
        long id = createDomainModificationProcess();

        gAdminTransactionServ.moveTransactionToNextState(id);

        TransactionVO transactionVO = gAdminTransactionServ.get(id);
        assert transactionVO.getState().getName().equals(TransactionStateVO.Name.PENDING_MANUAL_REVIEW);
    }

    private long createDomainModificationProcess() throws Exception {
        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
        gAdminTransactionServ.setUser(testAuthUser);

        Domain domain = createTestDomain("movetonextstate");
        domain.setRegistryUrl("newregurl");

        TransactionVO transactionVO = gAdminTransactionServ.createTransactions(DomainToVOConverter.toDomainVO(domain), false).get(0);
        return transactionVO.getTransactionID();
    }

    private Domain createTestDomain(String domainName) {
        Domain newDomain = new Domain(domainName);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        processDAO.deleteAll();
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
