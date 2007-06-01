package org.iana.rzm.mail;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.mail.processor.MailsProcessor;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class MailsProcessorTest extends TransactionalSpringContextTests {
    protected TransactionManager transactionManagerBean;
    protected ProcessDAO processDAO;
    protected UserManager userManager;
    protected DomainManager domainManager;
    protected MailsProcessor mailsProcessor;
    protected DiffConfiguration diffConfig;
    protected NotificationManager NotificationManagerBean;

    private static final String EMAIL_AC = "ac@no-mail.org";
    private static final String EMAIL_TC = "tc@no-mail.org";
    private static final String EMAIL_SUBJECT_PREFIX = "Re: ";
    private static final String EMAIL_SUBJECT_STATE_AND_TOKEN = " | PENDING_CONTACT_CONFIRMATION | [RZM] |";
    private static final String PUBLIC_KEY_AC_FILE_NAME = "tester.pgp.asc";
    private static final String PUBLIC_KEY_TC_FILE_NAME = "tester1.pgp.asc";
    private static final String CONTENT_AC_FILE_NAME = "accept-message.txt.asc";
    private static final String CONTENT_TC_FILE_NAME = "accept-message-1.txt.asc";
    private static final String TEMPLATE_EMAIL_SUBJECT = ("Domain Modification Transaction (Unified Workflow)");
    private static final String TEMPLATE_CONTENT_AC_FILE_NAME_NO_CHANGE = "template-nochange.txt.asc";
    //private static final String TEMPLATE_CONTENT_AC_FILE_NAME = "template.txt.asc";
    private static final String TEMPLATE_CONTENT_AC_FILE_NAME = "template.not-signed.txt.asc";

    public MailsProcessorTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());

            RZMUser user = UserManagementTestUtil.createUser("sys1mailrec",
                    UserManagementTestUtil.createSystemRole("mailrecdomain", true, true,
                            SystemRole.SystemType.AC));
            user.addRole(UserManagementTestUtil.createSystemRole("templatedomain", true, true,
                    SystemRole.SystemType.AC));
            user.setEmail(EMAIL_AC);
            user.setPublicKey(loadFromFile(PUBLIC_KEY_AC_FILE_NAME));
            userManager.create(user);

            user = UserManagementTestUtil.createUser("sys2mailrec",
                    UserManagementTestUtil.createSystemRole("mailrecdomain", true, true,
                            SystemRole.SystemType.TC));
            user.setEmail(EMAIL_TC);
            user.setPublicKey(loadFromFile(PUBLIC_KEY_TC_FILE_NAME));
            userManager.create(user);

            Domain domain = new Domain("mailrecdomain");
            domain.addAdminContact(new Contact("mailrecdomain-admin"));
            domain.addTechContact(new Contact("mailrecdomain-tech"));
            domainManager.create(domain);

            domain = new Domain("templatedomain");
            domain.setSupportingOrg(createContact("templatedomain-supp"));
            domain.addTechContact(createContact("templatedomain-tech"));
            domain.addAdminContact(createContact("templatedomain-admin"));
            Host host = new Host("ns1.templatedomain");
            host.addIPAddress("4.3.2.1");
            domain.addNameServer(host);
            host = new Host("ns2.templatedomain");
            host.addIPAddress("4.3.2.2");
            domain.addNameServer(host);
            domain.setRegistryUrl("registry.templatedomain");
            domain.setWhoisServer("whois.templatedomain");
            domainManager.create(domain);
        } finally {
            processDAO.close();
        }
    }

    private Contact createContact(String prefix) {
        Contact contact = new Contact(prefix, prefix + "org");
        contact.addAddress(new Address(prefix + "addr", "US"));
        contact.addEmail(prefix + "@no-mail.org");
        contact.addFaxNumber("+1234567890");
        contact.addPhoneNumber("+1234567890");
        return contact;
    }

    @Test
    public void testProcessConfirmationMail() throws Exception {
        try {
            Domain domain = domainManager.get("mailrecdomain");
            assert domain != null;
            domain = domain.clone();
            Host host = new Host("ns1.mailrecdomain");
            host.addIPAddress("1.2.3.4");
            domain.addNameServer(host);
            host = new Host("ns2.mailrecdomain");
            host.addIPAddress("2.2.3.4");
            domain.addNameServer(host);
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(domain);
            Long domainTrId = transaction.getTransactionID();
            assert transaction != null;
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();

            List<String> tokens = getTokens(transaction);
            assert tokens.size() == 2 : "unexpected number of tokens: " + tokens.size();
            Iterator<String> tokenIterator = tokens.iterator();

            String subject = EMAIL_SUBJECT_PREFIX + domainTrId + EMAIL_SUBJECT_STATE_AND_TOKEN + "mailrecdomain | " + tokenIterator.next();
            mailsProcessor.process(EMAIL_AC, subject, loadFromFile(CONTENT_AC_FILE_NAME));
            subject = EMAIL_SUBJECT_PREFIX + domainTrId + EMAIL_SUBJECT_STATE_AND_TOKEN + "mailrecdomain | " + tokenIterator.next();
            mailsProcessor.process(EMAIL_TC, subject, loadFromFile(CONTENT_TC_FILE_NAME));

            transaction = transactionManagerBean.getTransaction(domainTrId);
            assert transaction != null;
            assert TransactionState.Name.PENDING_MANUAL_REVIEW.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testProcessConfirmationMail")
    public void testProcessTemplateMailNoChange() throws Exception {
        try {
            mailsProcessor.process(EMAIL_AC, TEMPLATE_EMAIL_SUBJECT, loadFromFile(TEMPLATE_CONTENT_AC_FILE_NAME_NO_CHANGE));

            List<Transaction> transactions = transactionManagerBean.findOpenTransactions("templatedomain");
            assert transactions != null;
            assert transactions.isEmpty() : "unexpected number of transactions found: " + transactions.size();
        } finally {
            processDAO.close();
        }
    }

    @Test(dependsOnMethods = "testProcessTemplateMailNoChange")
    public void testProcessTemplateMail() throws Exception {
        try {
            mailsProcessor.process(EMAIL_AC, TEMPLATE_EMAIL_SUBJECT, loadFromFile(TEMPLATE_CONTENT_AC_FILE_NAME));

            List<Transaction> transactions = transactionManagerBean.findOpenTransactions("templatedomain");
            assert transactions != null;
            assert transactions.size() == 1 : "unexpected number of transactions found: " + transactions.size();
            Transaction trans = transactions.iterator().next();
            ObjectChange change = trans.getDomainChange();
            Domain testDomain = trans.getCurrentDomain();
            ChangeApplicator.applyChange(testDomain, change, diffConfig);

            Domain domain = new Domain("templatedomain");
            domain.setSupportingOrg(createContact("templatedomain-supp"));
            Contact contact = new Contact("templatedomain-tech-rep", "ICB Plc");
            contact.addAddress(new Address("9 Queens Road", "US"));
            contact.addAddress(new Address("11 Queens Road", "US"));
            contact.addEmail("templatedomain-tech-rep@no-mail.org");
            contact.addFaxNumber("+1122334456");
            contact.addPhoneNumber("+1122334455");
            domain.addTechContact(contact);
            domain.addAdminContact(createContact("templatedomain-admin"));
            Host host = new Host("ns11.templatedomain");
            host.addIPAddress("2.2.5.6");
            host.addIPAddress("2.2.5.7");
            domain.addNameServer(host);
            host = new Host("ns3.templatedomain");
            host.addIPAddress("3.3.4.5");
            domain.addNameServer(host);
            domain.setRegistryUrl("registry.url");
            domain.setWhoisServer("whois.url");

            assert domain.equals(testDomain.clone());
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        try {
            for (ProcessInstance processInstance : processDAO.findAll())
                processDAO.delete(processInstance);
            for (RZMUser user : userManager.findAll())
                userManager.delete(user);
            for (Domain domain : domainManager.findAll())
                domainManager.delete(domain.getName());
            for (Notification notif : NotificationManagerBean.findAll())
                NotificationManagerBean.delete(notif);
        } finally {
            processDAO.close();
        }
    }

    private String loadFromFile(String fileName) throws IOException {
        InputStream in = getClass().getResourceAsStream(fileName);
        if (in == null) throw new FileNotFoundException(fileName);
        DataInputStream dis = new DataInputStream(in);
        StringBuffer buf;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(dis, "US-ASCII"));
            try {
                buf = new StringBuffer();
                String line = reader.readLine();
                if (line != null) {
                    buf.append(line);
                    while ((line = reader.readLine()) != null) {
                        buf.append("\n");
                        buf.append(line);
                    }
                }
                return buf.toString();
            } finally {
                reader.close();
            }
        } finally {
            dis.close();
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
