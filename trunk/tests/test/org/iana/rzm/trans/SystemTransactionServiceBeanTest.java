package org.iana.rzm.trans;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.facade.system.trans.SystemTransactionService;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class SystemTransactionServiceBeanTest {

    private UserManager userManager;
    private SystemTransactionService service;
    private AuthenticationService authService;

    private final static String USER_NAME = "test";


    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        service = (SystemTransactionService) appCtx.getBean("GuardedSystemTransactionService");
        authService = (AuthenticationService) appCtx.getBean("authenticationServiceBean");
        createTestUsers();
    }


    private void createTestUsers() {
        RZMUser testUser = createTestUser();
        userManager.create(testUser);
    }

    private RZMUser createTestUser() {

        RZMUser testUser = new RZMUser();
        testUser.setObjId(1L);
        testUser.setFirstName("TestUser");
        testUser.setLastName("Testuser");
        testUser.setLoginName(USER_NAME);
        testUser.setPassword("test");
        testUser.addRole(new SystemRole(SystemRole.SystemType.TC));
        testUser.setSecurID(false);
        return testUser;
    }

    @Test
    public void testMultipleCallToGetTransactions() throws Exception {
        PasswordAuth passwordAuth = new PasswordAuth("test", "test");
        AuthenticatedUser authenticatedUser = authService.authenticate(passwordAuth);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertEquals("test", authenticatedUser.getUserName());
        service.setUser(authenticatedUser);
        List<TransactionVO> list = service.findOpenTransactions();
        Assert.assertNotNull(list);
        list = service.findOpenTransactions();
        Assert.assertNotNull(list);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
    }
}
