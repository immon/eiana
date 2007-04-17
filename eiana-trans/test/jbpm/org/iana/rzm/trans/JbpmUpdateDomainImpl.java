package org.iana.rzm.trans;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.jbpm.JbpmConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Address;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.user.dao.UserDAO;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"eiana-trans", "jbpm", "UpdateDomain"})
public class JbpmUpdateDomainImpl {
    ApplicationContext appCtx;
    TransactionManager transMgr;
    ProcessDAO processDAO;
    DomainDAO domainDAO;
    SchedulerThread schedulerThread;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private UserDAO userDAO;
    private Set<String> userNames = new HashSet<String>();
    private Long testProcessInstanceId;

    @BeforeClass
    public void init() {
        appCtx = SpringTransApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        schedulerThread = new SchedulerThread((JbpmConfiguration) appCtx.getBean("jbpmConfiguration"));
        userDAO = (UserDAO) appCtx.getBean("userDAO");

        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        createUser(UserManagementTestUtil.createUser("UDsys1",
                UserManagementTestUtil.createSystemRole("testdomain.org", true, true, SystemRole.SystemType.AC)));
        createUser(UserManagementTestUtil.createUser("UDsys2",
                UserManagementTestUtil.createSystemRole("testdomain.org", true, true, SystemRole.SystemType.AC)));
        createUser(UserManagementTestUtil.createUser("UDsys3",
                UserManagementTestUtil.createSystemRole("testdomain.org", true, false, SystemRole.SystemType.AC)));
        createUser(UserManagementTestUtil.createUser("UDsys4",
                UserManagementTestUtil.createSystemRole("testdomain.org", true, false, SystemRole.SystemType.TC)));
        createUser(UserManagementTestUtil.createUser("UDsys5",
                UserManagementTestUtil.createSystemRole("testdomain.org", true, false, SystemRole.SystemType.TC)));
        createUser(UserManagementTestUtil.createUser("UDsys6",
                UserManagementTestUtil.createSystemRole("testdomain.org", false, false, SystemRole.SystemType.TC)));

        processDAO.deploy(DefinedTestProcess.getDefinition());

        processDAO.close();
        txMgr.commit(txStatus);
    }

    @AfterClass
    public void cleanUp() {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        for (String name : userNames) userDAO.delete(userDAO.get(name));
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        processDAO.delete(processDAO.getProcessInstance(testProcessInstanceId));
        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);
        domainDAO.delete(domainDAO.get("testdomain.org"));
        txMgr.commit(txStatus);
    }

    private void createUser(RZMUser user) {
        userNames.add(user.getLoginName());
        userDAO.create(user);
    }

    @Test
    public void doUpdate() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);

        Address address = new Address();
        address.setTextAddress("66th Marszalkowska Str., 00-950 Warsaw, Mazovia");
        address.setCountryCode("PL");
       
        Contact supportingOrg = new Contact("NotRealOrg");
        supportingOrg.addEmail("oldemail@post.org");
        supportingOrg.addPhoneNumber("staryNumer");

        List<String> emails = new ArrayList<String>();
        emails.add("verynewemail@post.org");
        Contact clonedSupportingOrg = (Contact) supportingOrg.clone();
        clonedSupportingOrg.setEmails(emails);
        List<String> phones = new ArrayList<String>();
        phones.add("newphone");
        clonedSupportingOrg.setPhoneNumbers(phones);

        Domain domain = new Domain("testdomain.org");
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
        newContact.addEmail("noContact-new-emial@post.org");
        //clonedDomain.addTechContact(newContact);
        clonedDomain.setTechContacts(new ArrayList<Contact>());
        
        Transaction tr = transMgr.createDomainModificationTransaction(clonedDomain);

        ProcessInstance pi = processDAO.getProcessInstance(tr.getTransactionID());
        testProcessInstanceId = pi.getId();

        Token token = pi.getRootToken();
        token.signal();

        Thread.sleep(3001L);
        schedulerThread.executeTimers();

        token.signal("accept");
        token.signal("accept");
        token.signal("normal");
        token.signal("accept");

        token.signal("accept");

        processDAO.close();
        txMgr.commit(txStatus);

        txStatus = txMgr.getTransaction(txDef);

        Domain retrivedDomain = domainDAO.get(domain.getName());

        assert (retrivedDomain.getWhoisServer().equals("newwhoisserver") &&
                retrivedDomain.getRegistryUrl() == null);

        txMgr.commit(txStatus);
    }
}
