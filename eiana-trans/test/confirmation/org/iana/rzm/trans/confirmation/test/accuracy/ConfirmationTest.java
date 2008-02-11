package org.iana.rzm.trans.confirmation.test.accuracy;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TestTransactionManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.conf.ConfirmationTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"confirmation", "eiana-trans"})
public class ConfirmationTest extends TransactionalSpringContextTests {
    protected TestTransactionManager testTransactionManager;
    protected ProcessDAO processDAO;
    protected DomainManager domainManager;
    protected UserManager userManager;

    public ConfirmationTest() {
        super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        try {
            processDAO.deploy(ConfirmationTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
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
        return testTransactionManager.createConfirmationTestTransaction(domain);
    }

    @Test
    public void testContactConfirmations() throws Exception {
        try {
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

            assert TransactionState.Name.PENDING_IANA_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();
        } finally {
            processDAO.close();
        }
    }

    @Test
    public void testAdminConfirmations() throws Exception {
        try {
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

            assert TransactionState.Name.PENDING_IANA_CONFIRMATION.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();

            transaction.accept(
                    userManager.get("user-admin2"));
            assert TransactionState.Name.COMPLETED.equals(transaction.getState().getName())
                    : "unexpected state: " + transaction.getState().getName();
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
            for (RZMUser user : userManager.findAll())
                userManager.delete(user);
            for (Domain domain : domainManager.findAll())
                domainManager.delete(domain);
        } finally {
            processDAO.close();
        }
    }

    private List<String> getTokens(Transaction transaction) {
        List<String> result = new ArrayList<String>();
        ContactConfirmations cc = transaction.getTransactionData().getContactConfirmations();
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