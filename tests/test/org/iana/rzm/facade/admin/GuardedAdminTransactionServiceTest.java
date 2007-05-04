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
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.NoSuchTransactionException;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"test", "GuardedAdminTransactionServiceTest"})
public class GuardedAdminTransactionServiceTest {

    ApplicationContext appCtx;
    AdminTransactionService gAdminTransactionServ;

    UserManager userManager;
    DomainManager domainManager;
    ProcessDAO processDAO;
    RZMUser user, wrongUser, domainUser;

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

        domainManager.create(createTestDomain(DOMAIN_NAME));

        domainUser = new RZMUser();
        domainUser.setLoginName("gatsdomainuser");
        domainUser.setFirstName("firstName");
        domainUser.setLastName("lastName");
        domainUser.setEmail("email@some.com");
        domainUser.addRole(new SystemRole(SystemRole.SystemType.AC, DOMAIN_NAME, true, true));
        this.userManager.create(domainUser);

    }

    @Test (expectedExceptions = {AccessDeniedException.class})
    public void testCreateTransactionByWrongUser() throws Exception {
        try {
            AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(wrongUser)).getAuthUser();
            gAdminTransactionServ.setUser(testAuthUser);
            gAdminTransactionServ.createDomainModificationTransaction(ToVOConverter.toDomainVO(createTestDomain(DOMAIN_NAME)));
        } catch (AccessDeniedException e) {
            gAdminTransactionServ.close();
            throw e;
        }
    }

    @Test (dependsOnMethods = {"testCreateTransactionByWrongUser"})
    public void testTransactions() throws NoSuchTransactionException {
//        AuthenticatedUser testAuthUser = new TestAuthenticatedUser(UserConverter.convert(user)).getAuthUser();
//        gAdminTransactionServ.setUser(testAuthUser);
//
//        gAdminTransactionServ.createDomainModificationTransaction(ToVOConverter.toDomainVO(createTestDomain(DOMAIN_NAME)));
//
//        List<TransactionVO> transactionVOs = gAdminTransactionServ.findTransactions(DOMAIN_NAME);
//        assert transactionVOs.size() == 1;
//        TransactionVO transactionVO = transactionVOs.iterator().next();
//        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
//        assert transactionVO.getName().equals(PROCESS_NAME);
//        transactionID = transactionVO.getTransactionID();
//
//        transactionVO = gAdminTransactionServ.getTransaction(transactionID);
//        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
//        assert transactionVO.getName().equals(PROCESS_NAME);
//
//        Set<String> names = new HashSet<String>();
//        names.add(DOMAIN_NAME);
//        transactionVOs = gAdminTransactionServ.findTransactions(names);
//        assert transactionVOs.size() == 1;
//        transactionVO = transactionVOs.iterator().next();
//        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
//        assert transactionVO.getName().equals(PROCESS_NAME);
//
//        transactionVOs = gAdminTransactionServ.findAll();
//        assert transactionVOs.size() == 1;
//        transactionVO = transactionVOs.iterator().next();
//        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
//        assert transactionVO.getName().equals(PROCESS_NAME);
//
//        transactionVOs = gAdminTransactionServ.findTransactions(UserConverter.convert(domainUser));
//        assert transactionVOs.size() == 1;
//        transactionVO = transactionVOs.iterator().next();
//        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
//        assert transactionVO.getName().equals(PROCESS_NAME);
//
//        transactionVOs = gAdminTransactionServ.findTransactions(UserConverter.convert(domainUser), DOMAIN_NAME);
//        assert transactionVOs.size() == 1;
//        transactionVO = transactionVOs.iterator().next();
//        assert transactionVO.getDomainName().equals(DOMAIN_NAME);
//        assert transactionVO.getName().equals(PROCESS_NAME);

//        gAdminTransactionServ.deleteTransaction(transactionVOs.iterator().next());

        gAdminTransactionServ.close();
    }

    private Domain createTestDomain(String domainName) {
        Domain newDomain = new Domain(domainName);
        newDomain.setSupportingOrg(new Contact("supportOrg"));
        return newDomain;
    }

    @AfterClass
    public void cleanUp() {
        domainManager.delete(DOMAIN_NAME);

        userManager.delete(user);
        userManager.delete(wrongUser);
        userManager.delete(domainUser);
    }
}
