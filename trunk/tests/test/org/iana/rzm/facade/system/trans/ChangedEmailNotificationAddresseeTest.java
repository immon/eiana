package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class ChangedEmailNotificationAddresseeTest extends CommonGuardedSystemTransaction {

    RZMUser userIANA;

    protected void initTestData() {
        Domain domain1 = new Domain("changedemailtest");
        domain1.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain1.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domain1.setEnableEmails(true);
        domainManager.create(domain1);

        userIANA = new RZMUser();
        userIANA.setLoginName("gstsignaliana");
        userIANA.setFirstName("IANAuser");
        userIANA.setLastName("lastName");
        userIANA.setEmail("email@some.com");
        userIANA.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(userIANA);
    }

    @Test
    public void test4Notifications() throws Exception {
        IDomainVO domain = getDomain("changedemailtest");
        domain.getAdminContact().setEmail("b@x.pl");
        domain.getTechContact().setEmail("c@x.pl");

        setDefaultUser();
        List<TransactionVO> trans = GuardedSystemTransactionService.createTransactions(domain, false);

        TransactionVO t1 = trans.get(0);
        long transId = t1.getTransactionID();
        List<NotificationVO> notifs = notificationService.getNotifications(transId);
        assert notifs.size() == 4;
        acceptPENDING_CONTACT_CONFIRMATION(userIANA, transId, 4);
        closeServices();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        processDAO.deleteAll();
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        for (Domain domain : domainManager.findAll())
//            domainManager.delete(domain.getName());
    }
}
