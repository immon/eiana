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
    public void setContext() {
        appCtx = SpringTransApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        userDAO = (UserDAO) appCtx.getBean("userDAO");
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        userDAO.create(UserManagementTestUtil.createUser("sys1", UserManagementTestUtil.createSystemRole("conftestdomain", true, true, SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys2", UserManagementTestUtil.createSystemRole("conftestdomain", true, true, SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys3", UserManagementTestUtil.createSystemRole("conftestdomain", true, false, SystemRole.SystemType.AC)));
        userDAO.create(UserManagementTestUtil.createUser("sys4", UserManagementTestUtil.createSystemRole("conftestdomain", true, false, SystemRole.SystemType.TC)));
        userDAO.create(UserManagementTestUtil.createUser("sys5", UserManagementTestUtil.createSystemRole("conftestdomain", true, false, SystemRole.SystemType.TC)));
        userDAO.create(UserManagementTestUtil.createUser("sys6", UserManagementTestUtil.createSystemRole("conftestdomain", false, false, SystemRole.SystemType.TC)));
        userDAO.create(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        userDAO.create(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));
        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
        txMgr.commit(txStatus);
    }

    @Test
    public void doUpdate() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Address address = new Address();
        address.setCity("Warsaw");
        address.setCountryCode("PL");
        address.setPostalCode("00-950");
        address.setState("Mazovia");
        address.setStreet("Marszalkowska Str. 66/6");

        Contact supportingOrg = new Contact("NotRealOrg");
        supportingOrg.addEmail("oldemil");
        supportingOrg.addPhoneNumber("staryNumer");

        List<String> emails = new ArrayList<String>();
        emails.add("verynewemail");
        Contact clonedSupportingOrg = (Contact) supportingOrg.clone();
        clonedSupportingOrg.setEmails(emails);
        List<String> phones = new ArrayList<String>();
        phones.add("newphone");
        clonedSupportingOrg.setPhoneNumbers(phones);

        Domain domain = new Domain("conftestdomain");
       // domain.setWhoisServer("oldwhoisserver");
        domain.setRegistryUrl("http://www.oldregistryurl.org");
        domain.setSupportingOrg(supportingOrg);
        domain.addTechContact(new Contact("aaaaaa"));
        domainDAO.create(domain);

        Domain clonedDomain = (Domain) domain.clone();
        clonedDomain.setWhoisServer("newwhoisserver");
        clonedDomain.setRegistryUrl(null);
        clonedDomain.setSupportingOrg(clonedSupportingOrg);
        Contact newContact = new Contact("aaaaaa");
        newContact.addEmail("noContact new emial");
        //clonedDomain.addTechContact(newContact);
        clonedDomain.setTechContacts(new ArrayList<Contact>());

        Transaction trans = transMgr.createDomainModificationTransaction(clonedDomain);

        ProcessInstance processInstance = processDAO.getProcessInstance(trans.getTransactionID());

        Token token = processInstance.getRootToken();
        token.signal();

        trans.accept(userDAO.get("user-sys1"));

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        trans.accept(userDAO.get("user-sys4"));

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        userDAO.create(UserManagementTestUtil.createUser("sys7", UserManagementTestUtil.createSystemRole("conftestdomain", true, true, SystemRole.SystemType.AC)));

        trans.accept(userDAO.get("user-sys2"));

        assert "PENDING_CONTACT_CONFIRMATION".equals(processInstance.getRootToken().getNode().getName());

        trans.accept(userDAO.get("user-sys7"));

        assert "PENDING_IMPACTED_PARTIES".equals(processInstance.getRootToken().getNode().getName());

        processDAO.close();
        txMgr.commit(txStatus);
    }
}
