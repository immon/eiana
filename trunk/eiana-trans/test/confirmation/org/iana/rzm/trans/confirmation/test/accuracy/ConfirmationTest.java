package org.iana.rzm.trans.confirmation.test.accuracy;

import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true, groups = {"confirmation", "eiana-trans"})
public class ConfirmationTest {
    private ApplicationContext appCtx;
    private TransactionManager transMgr;
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private UserDAO userDAO;


    @BeforeClass
    public void init() {
        appCtx = SpringTransApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        userDAO = (UserDAO) appCtx.getBean("userDAO");
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        userDAO.create(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userDAO.create(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));
        userDAO.create(UserManagementTestUtil.createUser("admin3", new AdminRole(AdminRole.AdminType.IANA)));
        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    private Transaction createTransaction(String suffix) throws CloneNotSupportedException {
        userDAO.create(UserManagementTestUtil.createUser("sys1" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, true,
                        SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys2" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, true,
                        SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys3" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys4" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.TC)));
        userDAO.create(UserManagementTestUtil.createUser("sys5" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, true, false,
                        SystemRole.SystemType.TC)));
        userDAO.create(UserManagementTestUtil.createUser("sys6" + suffix,
                UserManagementTestUtil.createSystemRole("conftestdomain" + suffix, false, false,
                        SystemRole.SystemType.TC)));

        Address address = new Address();
        address.setCity("city" + suffix);
        address.setCountryCode("cc" + suffix);
        address.setPostalCode("pc" + suffix);
        address.setState("st" + suffix);
        address.setStreet("str" + suffix);

        Contact supportingOrg = new Contact("supporg" + suffix);
        supportingOrg.addEmail("oldemail" + suffix);
        supportingOrg.addPhoneNumber("oldnum" + suffix);

        List<String> emails = new ArrayList<String>();
        emails.add("newemail" + suffix);
        Contact clonedSupportingOrg = (Contact) supportingOrg.clone();
        clonedSupportingOrg.setEmails(emails);
        List<String> phones = new ArrayList<String>();
        phones.add("newphone" + suffix);
        clonedSupportingOrg.setPhoneNumbers(phones);

        Domain domain = new Domain("conftestdomain" + suffix);
       // domain.setWhoisServer("oldwhoisserver");
        domain.setRegistryUrl("http://www.oldregistryurl" + suffix + ".org");
        domain.setSupportingOrg(supportingOrg);
        domain.addTechContact(new Contact("aaa" + suffix));
        domainDAO.create(domain);

        Domain clonedDomain = (Domain) domain.clone();
        clonedDomain.setWhoisServer("newwhoisserver" + suffix);
        clonedDomain.setRegistryUrl(null);
        clonedDomain.setSupportingOrg(clonedSupportingOrg);
        Contact newContact = new Contact("aaa" + suffix);
        newContact.addEmail("nocontactnewemial" + suffix);
        //clonedDomain.addTechContact(newContact);
        clonedDomain.setTechContacts(new ArrayList<Contact>());

        return transMgr.createDomainModificationTransaction(clonedDomain);
    }

    @Test
    public void testContactConfirmations() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Transaction transaction = createTransaction("contact");
        ProcessInstance processInstance = processDAO.getProcessInstance(transaction.getTransactionID());

        Token token = processInstance.getRootToken();
        token.signal();

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        transaction.accept(userDAO.get("user-sys1contact"));

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        transaction.accept(userDAO.get("user-sys4contact"));

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        userDAO.create(UserManagementTestUtil.createUser("sys7contact",
                UserManagementTestUtil.createSystemRole("conftestdomaincontact", true, true,
                        SystemRole.SystemType.AC)));

        transaction.accept(userDAO.get("user-sys2contact"));

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        transaction.accept(userDAO.get("user-sys7contact"));

        assert "PENDING_IMPACTED_PARTIES".equals(processInstance.getRootToken().getNode().getName());

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test
    public void testAdminConfirmations() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Transaction transaction = createTransaction("admin");
        ProcessInstance processInstance = processDAO.getProcessInstance(transaction.getTransactionID());

        Token token = processInstance.getRootToken();
        token.signal();
        token.signal("accept");
        token.signal("accept");
        token.signal("normal");

        assert "PENDING_EXT_APPROVAL".equals(processInstance.getRootToken().getNode().getName());

        transaction.accept(userDAO.get("user-admin2"));

        assert "PENDING_USDOC_APPROVAL".equals(processInstance.getRootToken().getNode().getName());

        processDAO.close();
        txMgr.commit(txStatus);
    }
}
