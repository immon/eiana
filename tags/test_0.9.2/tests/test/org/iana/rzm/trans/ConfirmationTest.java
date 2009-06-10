package org.iana.rzm.trans;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"confirmation", "eiana-trans"})
public class ConfirmationTest extends RollbackableSpringContextTest {
    protected TransactionManager transactionManagerBean;
    protected ProcessDAO processDAO;
    protected DomainManager domainManager;
    protected UserManager userManager;

    public ConfirmationTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
    }

    private void createTestUsers() {
        userManager.create(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userManager.create(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));
        userManager.create(UserManagementTestUtil.createUser("admin3", new AdminRole(AdminRole.AdminType.IANA)));
    }

    private Transaction createTestTransaction(String suffix) throws CloneNotSupportedException {
        userManager.create(UserManagementTestUtil.createUser("sys1" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, true,
                        SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("sys2" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, true,
                        SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("sys3" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.AC)));
        userManager.create(UserManagementTestUtil.createUser("sys4" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.TC)));
        userManager.create(UserManagementTestUtil.createUser("sys5" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.TC)));
        userManager.create(UserManagementTestUtil.createUser("sys6" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, false, false,
                        SystemRole.SystemType.TC)));

        String domainName = "conftestdomain" + suffix;
        Domain domain = new Domain(domainName);
        domain.setAdminContact(new Contact(domainName + "-admin"));
        domain.setTechContact(new Contact(domainName + "-tech"));
        domainManager.create(domain);
        return transactionManagerBean.createDomainCreationTransaction(domain);
    }

    @Test
    public void testContactConfirmations() throws Exception {
        Transaction transaction = createTestTransaction("-contact-confirmations");
        assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();

        List<String> tokens = getTokens(transaction);
        assert tokens.size() == 2;
        Iterator<String> tokenIterator = tokens.iterator();

        transaction.accept(tokenIterator.next());
        assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();

        transaction.accept(tokenIterator.next());

        assert TransactionState.Name.PENDING_MANUAL_REVIEW.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();
    }

    @Test
    public void testAdminConfirmations() throws Exception {
        createTestUsers();
        Transaction transaction = createTestTransaction("-admin-confirmations");
        assert transaction != null;
        assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();

        List<String> tokens = getTokens(transaction);
        assert tokens.size() == 2;
        Iterator<String> tokenIterator = tokens.iterator();

        transaction.accept(tokenIterator.next());
        assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();

        transaction.accept(tokenIterator.next());

        assert TransactionState.Name.PENDING_MANUAL_REVIEW.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();

        transaction.accept(
                userManager.get("user-admin2"));
        assert TransactionState.Name.PENDING_IANA_CHECK.equals(transaction.getState().getName())
                : "unexpected state: " + transaction.getState().getName();
    }

    protected void cleanUp() throws Exception {
        processDAO.deleteAll();
    }

    private List<String> getTokens(Transaction transaction) {
        List<String> result = new ArrayList<String>();
        ContactConfirmations cc = transaction.getContactConfirmations();
        assert cc != null : "contact confirmations not found";
        for (Identity identity : cc.getUsersAbleToAccept()) {
            if (identity instanceof ContactIdentity) {
                ContactIdentity contactIdentity = (ContactIdentity) identity;
                result.add(contactIdentity.getToken());
            }
        }
        return result;
    }
}