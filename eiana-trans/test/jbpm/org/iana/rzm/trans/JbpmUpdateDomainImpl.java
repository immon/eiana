package org.iana.rzm.trans;

import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.impl.SchedulerThread;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"eiana-trans", "jbpm", "UpdateDomain"})
public class JbpmUpdateDomainImpl {
    private ApplicationContext appCtx;
    private TransactionManager transMgr;
    private ProcessDAO processDAO;
    private DomainDAO domainDAO;
    private SchedulerThread schedulerThread;
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();
    private UserDAO userDAO;
    private Set<String> userNames = new HashSet<String>();
    private Long testProcessInstanceId;

    @BeforeClass
    public void init() throws Exception {
        appCtx = SpringTransApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
        schedulerThread = new SchedulerThread((JbpmConfiguration) appCtx.getBean("jbpmConfiguration"));
        userDAO = (UserDAO) appCtx.getBean("userDAO");

        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
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

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    @AfterClass
    public void cleanUp() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
            for (String name : userNames) {
                RZMUser user = userDAO.get(name);
                if (user != null) userDAO.delete(user);
            }
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            ProcessInstance pi = processDAO.getProcessInstance(testProcessInstanceId);
            if (pi != null) processDAO.delete(pi);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }

        txStatus = txMgr.getTransaction(txDef);
        try {
            Domain domain = domainDAO.get("testdomain.org");
            if (domain != null) domainDAO.delete(domain);
            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }

    private void createUser(RZMUser user) {
        userNames.add(user.getLoginName());
        userDAO.create(user);
    }

    @Test
    public void doUpdate() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        try {
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

            Thread.sleep(3001L);
            schedulerThread.executeTimers();

            assert token.getNode().getName().equals("PENDING_CONTACT_CONFIRMATION") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_IMPACTED_PARTIES") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_IANA_CONFIRMATION") : "unexpected state: " + token.getNode().getName();
            token.signal("normal");
            assert token.getNode().getName().equals("PENDING_EXT_APPROVAL") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("PENDING_USDOC_APPROVAL") : "unexpected state: " + token.getNode().getName();
            token.signal("accept");
            assert token.getNode().getName().equals("COMPLETED") : "unexpected state: " + token.getNode().getName();

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
        
        txStatus = txMgr.getTransaction(txDef);
        try {

            Domain retrivedDomain = domainDAO.get("testdomain.org");

            assert (retrivedDomain.getWhoisServer().equals("newwhoisserver") &&
                    retrivedDomain.getRegistryUrl() == null);

            txMgr.commit(txStatus);
        } catch (Exception e) {
            txMgr.rollback(txStatus);
            throw e;
        } finally {
            processDAO.close();
        }
    }
}
