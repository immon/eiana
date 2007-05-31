package org.iana.rzm.trans;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.TransactionTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.auth.Identity;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"eiana-trans", "TransactionTest"})
public class TransactionTest extends TransactionalSpringContextTests {
    protected ProcessDAO processDAO;
    protected UserManager userManager;
    protected TestTransactionManager testTransactionManager;
    protected DomainManager domainManager;

    private long transactionId1, transactionId2, transactionId3;
    private long ticketId1;
    private Domain domain;

    public TransactionTest() {
        super(SpringTransApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        try {
            processDAO.deploy(TransactionTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
        createDomain();
        createUsers();
    }

    private void createDomain() {
        domain = new Domain("transtestdomain");
        domain.addAdminContact(new Contact("admin"));
        domain.addTechContact(new Contact("tech"));
        domainManager.create(domain);
    }

    private void createUsers() {
        Set<RZMUser> userSet = new HashSet<RZMUser>();

        userSet.add(UserManagementTestUtil.createUser("sys1trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", true, true,
                        SystemRole.SystemType.AC)));
        userSet.add(UserManagementTestUtil.createUser("sys2trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", true, false,
                        SystemRole.SystemType.TC)));
        userSet.add(UserManagementTestUtil.createUser("sys3trans",
                UserManagementTestUtil.createSystemRole("transtestdomain", false, false,
                        SystemRole.SystemType.TC)));
        userSet.add(UserManagementTestUtil.createUser("admin1trans", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userSet.add(UserManagementTestUtil.createUser("admin2trans", new AdminRole(AdminRole.AdminType.IANA)));

        for (RZMUser user : userSet) {
            userManager.create(user);
        }
    }

    @Test
    public void testTransactionCreation() throws Exception {
        try {
            Transaction transaction = testTransactionManager.createTransactionTestTransaction(domain);
            ticketId1 = 123L;
            transaction.setTicketID(ticketId1);
            transactionId1 = transaction.getTransactionID();
        } finally {
            processDAO.close();
        }

        Long ticketId2 = 456L;
        try {
            Transaction transaction = testTransactionManager.createTransactionTestTransaction(domain);
            transaction.setTicketID(ticketId2);
            transactionId2 = transaction.getTransactionID();
        } finally {
            processDAO.close();
        }


        Long ticketId3 = 125L;
        try {
            Transaction transaction = testTransactionManager.createTransactionTestTransaction(domain);
            transaction.setTicketID(ticketId3);
            transactionId3 = transaction.getTransactionID();
        } finally {
            processDAO.close();
        }

        try {
            Transaction transFromDB = testTransactionManager.getTransaction(transactionId1);
            assert (transFromDB != null && transFromDB.getTransactionID() == transactionId1
                    && transFromDB.getTicketID().equals(new Long(ticketId1)));

            Transaction transFromDB2 = testTransactionManager.getTransaction(transactionId2);
            assert (transFromDB2 != null && transFromDB2.getTransactionID() == transactionId2
                    && transFromDB2.getTicketID().equals(new Long(ticketId2)));

            Transaction transFromDB3 = testTransactionManager.getTransaction(transactionId3);
            assert (transFromDB3 != null && transFromDB3.getTransactionID() == transactionId3
                    && transFromDB3.getTicketID().equals(new Long(ticketId3)));
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionCreation"})
    public void testTransactionUpdate() throws Exception {
        try {
            Transaction transToUpdate = testTransactionManager.getTransaction(transactionId1);
            assert transToUpdate.getTicketID().equals(new Long(ticketId1));
            ticketId1 = 456L;
            transToUpdate.setTicketID(ticketId1);
        } finally {
            processDAO.close();
        }

        try {
            Transaction transFromDB = testTransactionManager.getTransaction(transactionId1);
            assert (transFromDB != null && transFromDB.getTransactionID() == transactionId1 &&
                    transFromDB.getTicketID().equals(new Long(ticketId1)));
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionUpdate"})
    public void testTransactionAccept() throws Exception {
        try {
            Transaction trans = testTransactionManager.getTransaction(transactionId1);
            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            for (String token : getTokens(trans))
                trans.accept(new ContactIdentity(token));
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_IANA_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionAccept"})
    public void testTransactionReject() throws Exception {
        try {
            Transaction trans = testTransactionManager.getTransaction(transactionId2);
            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            List<String> tokens = getTokens(trans);
            assert tokens.size() > 0;
            trans.reject(new ContactIdentity(tokens.iterator().next()));
            assert trans.getState().getName().equals(TransactionState.Name.REJECTED)
                    : "unexpected state: " + trans.getState().getName();
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionReject"},
            expectedExceptions = {UserNotAuthorizedToTransit.class})
    public void testTransactionTransitionFailed() throws Exception {
        try {
            Transaction trans = testTransactionManager.getTransaction(transactionId3);
            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.transit(userManager.get("user-admin1trans"), "normal");
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = {"testTransactionTransitionFailed"})
    public void testTransactionTransitionSuccessful() throws Exception {
        try {
            Transaction trans = testTransactionManager.getTransaction(transactionId3);

            assert trans != null;
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_CONTACT_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            for (String token : getTokens(trans))
                trans.accept(new ContactIdentity(token));
            assert trans.getState().getName().equals(TransactionState.Name.PENDING_IANA_CONFIRMATION)
                    : "unexpected state: " + trans.getState().getName();
            trans.transit(userManager.get("user-admin2trans"), "normal");
            assert trans.getState().getName().equals(TransactionState.Name.COMPLETED)
                    : "unexpected state: " + trans.getState().getName();
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        try {
            List<ProcessInstance> pis = processDAO.findAll();
            for (ProcessInstance pi : pis) {
                processDAO.delete(pi);
            }
            List<RZMUser> users = userManager.findAll();
            for (RZMUser user : users) {
                userManager.delete(user);
            }
            List<Domain> domains = domainManager.findAll();
            for (Domain domain : domains) {
                domainManager.delete(domain);
            }
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
