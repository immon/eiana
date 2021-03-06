package org.iana.rzm.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.IsNull;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.facade.auth.PasswordAuth;
import org.iana.rzm.facade.system.trans.TransactionCriteriaFields;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class SystemTransactionServiceBeanTest extends RollbackableSpringContextTest {

    protected UserManager userManager;
    protected TransactionService GuardedSystemTransactionService;
    protected AuthenticationService authenticationServiceBean;

    private final static String USER_NAME = "test";

    public SystemTransactionServiceBeanTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() {
//        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
//        userManager = (UserManager) appCtx.getBean("userManager");
//        GuardedSystemTransactionService = (TransactionService) appCtx.getBean("GuardedSystemTransactionService");
//        authenticationServiceBean = (AuthenticationService) appCtx.getBean("authenticationServiceBean");
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
        AuthenticatedUser authenticatedUser = authenticationServiceBean.authenticate(passwordAuth);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertEquals("test", authenticatedUser.getUserName());
        GuardedSystemTransactionService.setUser(authenticatedUser);
        Criterion open = new IsNull(TransactionCriteriaFields.END);
        List<TransactionVO> list = GuardedSystemTransactionService.find(open);
        Assert.assertNotNull(list);
        list = GuardedSystemTransactionService.find(open);
        Assert.assertNotNull(list);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
    }
}
