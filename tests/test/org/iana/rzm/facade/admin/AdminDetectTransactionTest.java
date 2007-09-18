package org.iana.rzm.facade.admin;

import org.springframework.context.ApplicationContext;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.facade.auth.TestAuthenticatedUser;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.trans.TransactionActionsVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AdminDetectTransactionTest {

    AdminTransactionService gTransServ;
    AdminDomainService gDomainServ;
    UserManager userManager;
    DomainManager domainManager;

    @BeforeClass
    public void init() {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        userManager = (UserManager) appCtx.getBean("userManager");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        gTransServ = (AdminTransactionService) appCtx.getBean("GuardedAdminTransactionServiceBean");
        gDomainServ = (AdminDomainService) appCtx.getBean("GuardedAdminDomainServiceBean");

        RZMUser user = new RZMUser("fn", "ln", "", "login", "email", "", false);
        user.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(user);

        domainManager.create(new Domain("admindetect-test"));
        AuthenticatedUser auth = new TestAuthenticatedUser(UserConverter.convert(user));
        gTransServ.setUser(auth);
        gDomainServ.setUser(auth);
    }

    @AfterClass
    public void destroy() {
        userManager.delete("login");
        domainManager.delete("admindetect-test");
    }

    @Test
    public void testDetectDescriptionChange() throws Exception {
        IDomainVO domain = gDomainServ.getDomain("admindetect-test");
        domain.setDescription("new description");
        TransactionActionsVO actions = gTransServ.detectTransactionActions(domain);
        assert actions.getActions().size() == 1;
    }

    @Test
    public void testDetectTypeChange() throws Exception {
        IDomainVO domain = gDomainServ.getDomain("admindetect-test");
        domain.setType("new type");
        TransactionActionsVO actions = gTransServ.detectTransactionActions(domain);
        assert actions.getActions().size() == 1;
    }

    @Test
    public void testDetectEnableEmailsChange() throws Exception {
        IDomainVO domain = gDomainServ.getDomain("admindetect-test");
        domain.setEnableEmails(true);
        TransactionActionsVO actions = gTransServ.detectTransactionActions(domain);
        assert actions.getActions().size() == 1;
    }

    @Test
    public void testDetectSpecialInstructionsChange() throws Exception {
        IDomainVO domain = gDomainServ.getDomain("admindetect-test");
        domain.setSpecialInstructions("new special instructions");
        TransactionActionsVO actions = gTransServ.detectTransactionActions(domain);
        assert actions.getActions().size() == 1;
    }
}
