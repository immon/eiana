package org.iana.rzm.mail;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.IPAddressVO;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionVO;
import org.iana.rzm.facade.system.trans.vo.changes.ChangeVO;
import org.iana.rzm.facade.system.trans.vo.changes.StringValueVO;
import org.iana.rzm.facade.system.trans.vo.changes.ObjectValueVO;
import org.iana.rzm.mail.processor.MailsProcessor;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.AdminRole;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.iana.criteria.Equal;
import org.iana.criteria.Criterion;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;
import org.hibernate.Criteria;

import java.io.*;
import java.util.*;

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
    protected AuthenticationService authenticationServiceBean;
    protected TransactionService transTransactionService;
    protected SystemDomainService transSystemDomainService;

    private static final String EMAIL_AC = "ac@no-mail.org";
    private static final String EMAIL_TC = "tc@no-mail.org";
    private static final String EMAIL_IANA = "iana@no-mail.org";
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

            RZMUser user1 = new RZMUser();
            user1.setLoginName("sys1mailrec");
            user1.addRole(new SystemRole(SystemRole.SystemType.AC, "mailrecdomain", true, true));
            user1.addRole(new SystemRole(SystemRole.SystemType.AC, "templatedomain", true, true));
            user1.setEmail(EMAIL_AC);
            user1.setPublicKey(loadFromFile(PUBLIC_KEY_AC_FILE_NAME));
            userManager.create(user1);

            RZMUser user2 = new RZMUser();
            user2.setLoginName("sys2mailrec");
            user2.addRole(new SystemRole(SystemRole.SystemType.TC, "mailrecdomain", true, true));
            user2.setEmail(EMAIL_TC);
            user2.setPublicKey(loadFromFile(PUBLIC_KEY_TC_FILE_NAME));
            userManager.create(user2);

            RZMUser user3 = new RZMUser();
            user2.setLoginName("ianamailrec");
            user2.addRole(new AdminRole(AdminRole.AdminType.IANA));
            user2.setEmail(EMAIL_IANA);
            userManager.create(user3);

            Domain domain = new Domain("mailrecdomain");
            domain.setAdminContact(new Contact("mailrecdomain-admin"));
            domain.setTechContact(new Contact("mailrecdomain-tech"));
            domainManager.create(domain);

            domain = new Domain("templatedomain");
            domain.setSupportingOrg(createContact("templatedomain-supp"));
            domain.setTechContact(createContact("templatedomain-tech"));
            domain.setAdminContact(createContact("templatedomain-admin"));
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
        contact.setJobTitle(prefix + "-job-title");
        contact.setAddress(new Address(prefix + "addr", "US"));
        contact.setPublicEmail(prefix + "-pub@no-mail.org");
        contact.setPrivateEmail(prefix + "-prv@no-mail.org");
        contact.setFaxNumber("+1234567890");
        contact.setAltFaxNumber("+1234567891");
        contact.setPhoneNumber("+1234567892");
        contact.setAltPhoneNumber("+1234567893");
        return contact;
    }

    @Test
    public void testProcessConfirmationMail() throws Exception {
        try {
            setServicesUser("sys1mailrec");
            IDomainVO domain = transSystemDomainService.getDomain("mailrecdomain");
            assert domain != null;
            HostVO host1 = new HostVO("ns1.mailrecdomain");
            IPAddressVO ipAddr1 = new IPAddressVO();
            ipAddr1.setAddress("1.2.3.4");
            ipAddr1.setType(IPAddressVO.Type.IPv4);
            host1.getAddresses().add(ipAddr1);
            domain.getNameServers().add(host1);
            HostVO host2 = new HostVO("ns2.mailrecdomain");
            IPAddressVO ipAddr2 = new IPAddressVO();
            ipAddr2.setAddress("2.2.3.4");
            ipAddr2.setType(IPAddressVO.Type.IPv4);
            host2.getAddresses().add(ipAddr2);
            domain.getNameServers().add(host2);
            TransactionVO transaction = transTransactionService.createTransactions(domain, false).get(0);
            assert transaction != null;
            assert TransactionStateVO.Name.PENDING_CREATION.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();
            Long domainTrId = transaction.getTransactionID();
            setServicesUser("ianamailrec");
            transTransactionService.transitTransaction(domainTrId, "go-on");
            transaction = transTransactionService.get(domainTrId);
            assert TransactionStateVO.Name.PENDING_CONTACT_CONFIRMATION.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();

            List<String> tokens = getTokens(transaction.getTransactionID());
            assert tokens.size() == 2 : "unexpected number of tokens: " + tokens.size();
            Iterator<String> tokenIterator = tokens.iterator();

            String subject = EMAIL_SUBJECT_PREFIX + domainTrId + EMAIL_SUBJECT_STATE_AND_TOKEN + "mailrecdomain | " + tokenIterator.next();
            mailsProcessor.process(EMAIL_AC, subject, loadFromFile(CONTENT_AC_FILE_NAME));
            subject = EMAIL_SUBJECT_PREFIX + domainTrId + EMAIL_SUBJECT_STATE_AND_TOKEN + "mailrecdomain | " + tokenIterator.next();
            mailsProcessor.process(EMAIL_TC, subject, loadFromFile(CONTENT_TC_FILE_NAME));

            transaction = transTransactionService.get(domainTrId);
            assert transaction != null;
            assert TransactionStateVO.Name.PENDING_MANUAL_REVIEW.equals(transaction.getState().getName()) :
                    "unexpected state: " + transaction.getState().getName();
        } finally {
            closeServices();
        }
    }

    @Test(dependsOnMethods = "testProcessConfirmationMail")
    public void testProcessTemplateMailNoChange() throws Exception {
        mailsProcessor.process(EMAIL_AC, TEMPLATE_EMAIL_SUBJECT, loadFromFile(TEMPLATE_CONTENT_AC_FILE_NAME_NO_CHANGE));

        try {
            // todo: test
//            setServicesUser("sys1mailrec");
//            TransactionCriteriaVO criteria = new TransactionCriteriaVO();
//            criteria.addDomainName("templatedomain");
//            List<TransactionVO> transactions = transTransactionService.find(criteria);
//            assert transactions != null;
//            assert transactions.isEmpty() : "unexpected number of transactions found: " + transactions.size();
        } finally {
            closeServices();
        }
    }

    @Test(dependsOnMethods = "testProcessTemplateMailNoChange")
    public void testProcessTemplateMail() throws Exception {
        try {
            mailsProcessor.process(EMAIL_AC, TEMPLATE_EMAIL_SUBJECT, loadFromFile(TEMPLATE_CONTENT_AC_FILE_NAME));

            setServicesUser("sys1mailrec");
//            TransactionCriteriaVO criteria = new TransactionCriteriaVO();
//            criteria.addDomainName("templatedomain");
            // todo
            Criterion domainName = new Equal(TransactionCriteriaFields.CURRENT_DOMAIN_NAME, "templatedomain");
            List<TransactionVO> transactions = transTransactionService.find(domainName);
            assert transactions != null;
            assert transactions.size() == 1 : "unexpected number of transactions found: " + transactions.size();
            TransactionVO trans = transactions.iterator().next();
            List<TransactionActionVO> changes = trans.getDomainActions();

            Set<TestTransactionActionVO> expected = toTestTransactionActions(getExpectedDomainActions());
            Set<TestTransactionActionVO> actual = toTestTransactionActions(changes);
            assert expected.equals(actual);
        } finally {
            closeServices();
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

    private List<String> getTokens(long transactionId) throws NoSuchTransactionException {
        try {
            Transaction transaction = transactionManagerBean.getTransaction(transactionId);
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
        } finally {
            processDAO.close();
        }
    }

    private void setServicesUser(String userName) throws AuthenticationFailedException, AuthenticationRequiredException {
        AuthenticatedUser au = authenticationServiceBean.authenticate(new PasswordAuth(userName, null));
        transSystemDomainService.setUser(au);
        transTransactionService.setUser(au);
    }

    private void closeServices() {
        transSystemDomainService.close();
        transTransactionService.close();
    }

    private List<TransactionActionVO> getExpectedDomainActions() {
        List<TransactionActionVO> result = new ArrayList<TransactionActionVO>();

        TransactionActionVO action1 = new TransactionActionVO(TransactionActionVO.MODIFY_WHOIS_SERVER);
        action1.addChange(createStringValueChange("whoisServer", ChangeVO.Type.UPDATE, "whois.templatedomain", "whois.url"));
        result.add(action1);

        TransactionActionVO action2 = new TransactionActionVO(TransactionActionVO.MODIFY_REGISTRATION_URL);
        action2.addChange(createStringValueChange("registryUrl", ChangeVO.Type.UPDATE, "registry.templatedomain", "registry.url"));
        result.add(action2);

        TransactionActionVO action3 = new TransactionActionVO(TransactionActionVO.MODIFY_TC);
        List<ChangeVO> changes3 = new ArrayList<ChangeVO>();
        changes3.add(createStringValueChange("altFaxNumber", ChangeVO.Type.UPDATE, "+1234567891", "+1122335567"));
        changes3.add(createStringValueChange("faxNumber", ChangeVO.Type.UPDATE, "+1234567890", "+1122335566"));
        changes3.add(createStringValueChange("jobTitle", ChangeVO.Type.UPDATE, "templatedomain-tech-job-title", "templatedomain-job-title"));
        changes3.add(createStringValueChange("privateEmail", ChangeVO.Type.UPDATE, "templatedomain-tech-prv@no-mail.org", "templatedomain-tech-rep-prv@no-mail.org"));
        changes3.add(createStringValueChange("altPhoneNumber", ChangeVO.Type.UPDATE, "+1234567893", "+1122334456"));
        List<ChangeVO> subchanges3addr = new ArrayList<ChangeVO>();
        subchanges3addr.add(createStringValueChange("textAddress", ChangeVO.Type.UPDATE, "templatedomain-techaddr", "9 Queens Road"));
        changes3.add(createObjectValueChange("address", ChangeVO.Type.UPDATE, 0, "templatedomain-techaddr", subchanges3addr));
        changes3.add(createStringValueChange("email", ChangeVO.Type.UPDATE, "templatedomain-tech-pub@no-mail.org", "templatedomain-tech-rep-pub@no-mail.org"));
        changes3.add(createStringValueChange("phoneNumber", ChangeVO.Type.UPDATE, "+1234567892", "+1122334455"));
        changes3.add(createStringValueChange("name", ChangeVO.Type.UPDATE, "templatedomain-tech", "templatedomain-tech-rep"));
        changes3.add(createStringValueChange("organization", ChangeVO.Type.UPDATE, "templatedomain-techorg", "ICB Plc"));
        action3.addChange(createObjectValueChange("techContact", ChangeVO.Type.UPDATE, 0, "templatedomain-tech", changes3));
        result.add(action3);

        TransactionActionVO action4 = new TransactionActionVO(TransactionActionVO.MODIFY_NAME_SERVERS);
        List<ChangeVO> changes4ns1 = new ArrayList<ChangeVO>();
        changes4ns1.add(createStringValueChange("name", ChangeVO.Type.ADDITION, null, "ns11.templatedomain"));
        List<ChangeVO> subchanges4ns1addr1 = new ArrayList<ChangeVO>();
        subchanges4ns1addr1.add(createStringValueChange("address", ChangeVO.Type.ADDITION, null, "2.2.5.6"));
        changes4ns1.add(createObjectValueChange("addresses", ChangeVO.Type.ADDITION, 0, "2.2.5.6", subchanges4ns1addr1));
        List<ChangeVO> subchanges4ns1addr2 = new ArrayList<ChangeVO>();
        subchanges4ns1addr2.add(createStringValueChange("address", ChangeVO.Type.ADDITION, null, "2.2.5.7"));
        changes4ns1.add(createObjectValueChange("addresses", ChangeVO.Type.ADDITION, 0, "2.2.5.7", subchanges4ns1addr2));
        action4.addChange(createObjectValueChange("nameServers", ChangeVO.Type.ADDITION, 0, "ns11.templatedomain", changes4ns1));
        List<ChangeVO> changes4ns2 = new ArrayList<ChangeVO>();
        changes4ns2.add(createStringValueChange("name", ChangeVO.Type.ADDITION, null, "ns3.templatedomain"));
        List<ChangeVO> subchanges4ns2addr1 = new ArrayList<ChangeVO>();
        subchanges4ns2addr1.add(createStringValueChange("address", ChangeVO.Type.ADDITION, null, "3.3.4.5"));
        changes4ns2.add(createObjectValueChange("addresses", ChangeVO.Type.ADDITION, 0, "3.3.4.5", subchanges4ns2addr1));
        action4.addChange(createObjectValueChange("nameServers", ChangeVO.Type.ADDITION, 0, "ns3.templatedomain", changes4ns2));
        List<ChangeVO> changes4ns3 = new ArrayList<ChangeVO>();
        changes4ns3.add(createStringValueChange("name", ChangeVO.Type.REMOVAL, "ns1.templatedomain", null));
        List<ChangeVO> subchanges4ns3addr1 = new ArrayList<ChangeVO>();
        subchanges4ns3addr1.add(createStringValueChange("address", ChangeVO.Type.REMOVAL, "4.3.2.1", null));
        changes4ns3.add(createObjectValueChange("addresses", ChangeVO.Type.REMOVAL, 0, "4.3.2.1", subchanges4ns3addr1));
        action4.addChange(createObjectValueChange("nameServers", ChangeVO.Type.REMOVAL, 0, "ns1.templatedomain", changes4ns3));
        List<ChangeVO> changes4ns4 = new ArrayList<ChangeVO>();
        changes4ns4.add(createStringValueChange("name", ChangeVO.Type.REMOVAL, "ns2.templatedomain", null));
        List<ChangeVO> subchanges4ns4addr1 = new ArrayList<ChangeVO>();
        subchanges4ns4addr1.add(createStringValueChange("address", ChangeVO.Type.REMOVAL, "4.3.2.2", null));
        changes4ns4.add(createObjectValueChange("addresses", ChangeVO.Type.REMOVAL, 0, "4.3.2.2", subchanges4ns4addr1));
        action4.addChange(createObjectValueChange("nameServers", ChangeVO.Type.REMOVAL, 0, "ns2.templatedomain", changes4ns4));
        result.add(action4);

        return result;
    }

    private ChangeVO createStringValueChange(String fieldName, ChangeVO.Type type, String oldValue, String newValue) {
        ChangeVO result = new ChangeVO();
        result.setFieldName(fieldName);
        result.setType(type);
        result.setValue(new StringValueVO(oldValue, newValue));
        return result;
    }

    private ChangeVO createObjectValueChange(String fieldName, ChangeVO.Type type, long objectId, String objectName, List<ChangeVO> subchanges) {
        ChangeVO result = new ChangeVO();
        result.setFieldName(fieldName);
        result.setType(type);
        ObjectValueVO value = new ObjectValueVO(objectId, objectName);
        value.addChanges(subchanges);
        result.setValue(value);
        return result;
    }

    private Set<TestTransactionActionVO> toTestTransactionActions(List<TransactionActionVO> taList) {
        Set<TestTransactionActionVO> result = new HashSet<TestTransactionActionVO>();
        for (TransactionActionVO ta : getExpectedDomainActions())
            result.add(new TestTransactionActionVO(ta));
        return result;
    }

    // helper classes to compare list of changes as sets

    class TestTransactionActionVO {
        private String name;
        private Set<TestChangeVO> change = new HashSet<TestChangeVO>();

        public TestTransactionActionVO(TransactionActionVO ta) {
            name = ta.getName();
            for (ChangeVO ch : ta.getChange())
                change.add(new TestChangeVO(ch));
        }

        public String getName() {
            return name;
        }

        public Set<TestChangeVO> getChange() {
            return change;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestTransactionActionVO that = (TestTransactionActionVO) o;

            if (change != null ? !change.equals(that.change) : that.change != null) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (name != null ? name.hashCode() : 0);
            result = 31 * result + (change != null ? change.hashCode() : 0);
            return result;
        }
    }

    class TestChangeVO {
        private String fieldName;
        private String type;
        private TestValueVO value;

        public TestChangeVO(ChangeVO c) {
            fieldName = c.getFieldName();
            type = c.getType().toString();
            value = c.getValue() instanceof StringValueVO ? new TestStringValueVO((StringValueVO) c.getValue()) :
                    new TestObjectValueVO((ObjectValueVO) c.getValue());
        }

        public TestChangeVO(String fieldName, String type, TestValueVO value) {
            this.fieldName = fieldName;
            this.type = type;
            this.value = value;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestChangeVO that = (TestChangeVO) o;

            if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (fieldName != null ? fieldName.hashCode() : 0);
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    abstract class TestValueVO {}

    public class TestStringValueVO extends TestValueVO {
        private String oldValue;
        private String newValue;

        public TestStringValueVO(StringValueVO sv) {
            oldValue = sv.getOldValue();
            newValue = sv.getNewValue();
        }

        public String getOldValue() {
            return oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestStringValueVO that = (TestStringValueVO) o;

            if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) return false;
            if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (oldValue != null ? oldValue.hashCode() : 0);
            result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
            return result;
        }
    }

    public class TestObjectValueVO extends TestValueVO {
        private long id;
        private String name;
        private Set<TestChangeVO> changes = new HashSet<TestChangeVO>();

        public TestObjectValueVO(ObjectValueVO ov) {
            id = ov.getId();
            name = ov.getName();
            for (ChangeVO ch : ov.getChanges())
                changes.add(new TestChangeVO(ch));
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Set<TestChangeVO> getChanges() {
            return changes;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestObjectValueVO that = (TestObjectValueVO) o;

            if (id != that.id) return false;
            if (changes != null ? !changes.equals(that.changes) : that.changes != null) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (changes != null ? changes.hashCode() : 0);
            return result;
        }
    }
}
