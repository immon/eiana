package org.iana.rzm.mail;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.mail.processor.MailsProcessor;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.ChangeApplicator;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.Notification;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.*;
import java.io.*;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = "excluded")
public class MailsProcessorTest {
    private PlatformTransactionManager txManager;
    private TransactionDefinition txDefinition = new DefaultTransactionDefinition();
    private TransactionManager transactionManager;
    private ProcessDAO processDAO;
    private UserManager userManager;
    private DomainManager domainManager;
    private MailsProcessor mailsProcessor;
    DiffConfiguration diffConfig;
    private Set<RZMUser> users = new HashSet<RZMUser>();
    private List<String> domainNames = new ArrayList<String>();
    private Long domainTrId;
    private NotificationManager notificationManager;

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

    @BeforeClass
    public void init() throws Exception {
        ApplicationContext appCtx = SpringApplicationContext.getInstance().getContext();
        txManager = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        transactionManager = (TransactionManager) appCtx.getBean("transactionManagerBean");
        domainManager = (DomainManager) appCtx.getBean("domainManager");
        userManager = (UserManager) appCtx.getBean("userManager");
        mailsProcessor = (MailsProcessor) appCtx.getBean("mailsProcessor");
        diffConfig = (DiffConfiguration) appCtx.getBean("diffConfig");
        notificationManager = (NotificationManager) appCtx.getBean("NotificationManagerBean");
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());

            RZMUser user = UserManagementTestUtil.createUser("sys1mailrec",
                    UserManagementTestUtil.createSystemRole("mailrecdomain", true, true,
                            SystemRole.SystemType.AC));
            user.addRole(UserManagementTestUtil.createSystemRole("templatedomain", true, true,
                            SystemRole.SystemType.AC));
            user.setEmail(EMAIL_AC);
            user.setPublicKey(loadFromFile(PUBLIC_KEY_AC_FILE_NAME));
            users.add(user);
            userManager.create(user);

            user = UserManagementTestUtil.createUser("sys2mailrec",
                    UserManagementTestUtil.createSystemRole("mailrecdomain", true, true,
                            SystemRole.SystemType.TC));
            user.setEmail(EMAIL_TC);
            user.setPublicKey(loadFromFile(PUBLIC_KEY_TC_FILE_NAME));
            users.add(user);
            userManager.create(user);

            Domain domain = new Domain("mailrecdomain");
            domainManager.create(domain);
            domainNames.add(domain.getName());
            Host host = new Host("ns1.mailrecdomain");
            host.addIPAddress("1.2.3.4");
            domain.addNameServer(host);
            host = new Host("ns2.mailrecdomain");
            host.addIPAddress("2.2.3.4");
            domain.addNameServer(host);
            domainTrId = transactionManager.createDomainModificationTransaction(domain).getTransactionID();

            domain = new Domain("templatedomain");
            domain.setSupportingOrg(createContact("templatedomain-supp"));
            domain.addTechContact(createContact("templatedomain-tech"));
            domain.addAdminContact(createContact("templatedomain-admin"));
            host = new Host("ns1.templatedomain");
            host.addIPAddress("4.3.2.1");
            domain.addNameServer(host);
            host = new Host("ns2.templatedomain");
            host.addIPAddress("4.3.2.2");
            domain.addNameServer(host);
            domain.setRegistryUrl("registry.templatedomain");
            domain.setWhoisServer("whois.templatedomain");
            domainManager.create(domain);
            domainNames.add(domain.getName());
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

    public void testProcessConfirmationMail() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            Transaction transaction = transactionManager.getTransaction(domainTrId);
            assert transaction != null;
            assert TransactionState.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();

            String subject = EMAIL_SUBJECT_PREFIX + domainTrId + EMAIL_SUBJECT_STATE_AND_TOKEN + "mailrecdomain";
            mailsProcessor.process(EMAIL_AC, subject, loadFromFile(CONTENT_AC_FILE_NAME));
            mailsProcessor.process(EMAIL_TC, subject, loadFromFile(CONTENT_TC_FILE_NAME));

            transaction = transactionManager.getTransaction(domainTrId);
            assert transaction != null;
            /*
            assert TransactionState.Name.PENDING_IMPACTED_PARTIES.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();
            */
            assert TransactionState.Name.PENDING_IANA_CONFIRMATION.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test (dependsOnMethods = "testProcessConfirmationMail")
    public void testProcessTemplateMailNoChange() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            mailsProcessor.process(EMAIL_AC, TEMPLATE_EMAIL_SUBJECT, loadFromFile(TEMPLATE_CONTENT_AC_FILE_NAME_NO_CHANGE));

            List<Transaction> transactions = transactionManager.findOpenTransactions("templatedomain");
            assert transactions != null;
            assert transactions.isEmpty() : "unexpected number of transactions found: " + transactions.size();

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @Test (dependsOnMethods = "testProcessTemplateMailNoChange")
    public void testProcessTemplateMail() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            mailsProcessor.process(EMAIL_AC, TEMPLATE_EMAIL_SUBJECT, loadFromFile(TEMPLATE_CONTENT_AC_FILE_NAME));

            List<Transaction> transactions = transactionManager.findOpenTransactions("templatedomain");
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

            assert domain.equals(testDomain);

            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txManager.getTransaction(txDefinition);
        try {
            for (ProcessInstance processInstance : processDAO.findAll())
                processDAO.delete(processInstance);
            for (RZMUser user : userManager.findAll())
                userManager.delete(user);
            for (Domain domain : domainManager.findAll())
                domainManager.delete(domain.getName());
            for (Notification notif : notificationManager.findAll())
                notificationManager.delete(notif);
            txManager.commit(txStatus);
        } catch (Exception e) {
            if (!txStatus.isCompleted())
                txManager.rollback(txStatus);
            throw e;
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
}
