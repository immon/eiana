package org.iana.rzm.trans;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
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
import org.iana.rzm.trans.conf.SpringApplicationContext;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.RZMUser;
import org.iana.notifications.EmailAddress;
import org.iana.notifications.EmailAddresses;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

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
    private PlatformTransactionManager txMgr;
    private TransactionDefinition txDef = new DefaultTransactionDefinition();

    @BeforeClass
    public void setContext() {
        appCtx = SpringApplicationContext.getInstance().getContext();
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");
        txMgr = (PlatformTransactionManager) appCtx.getBean("transactionManager");
    }

    @Test
    public void doUpdate() throws Exception {
        TransactionStatus txStatus = txMgr.getTransaction(txDef);
        processDAO.deploy(DefinedTestProcess.getDefinition());


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
        newContact.addEmail("noContact new emial");
        //clonedDomain.addTechContact(newContact);
        clonedDomain.setTechContacts(new ArrayList<Contact>());
        
        Transaction tr = transMgr.createDomainModificationTransaction(clonedDomain);

        ProcessInstance procesInstance = processDAO.getProcessInstance(tr.getTransactionID());

        Token token = procesInstance.getRootToken();
        token.signal();

        RZMUser user = new RZMUser();
        user.setLoginName("gregorM");
        user.setFirstName("Gregor");
        user.setLastName("Martin");
        procesInstance.getContextInstance().setVariable("EMAIL_TEMPLATE_DATA", user);
        procesInstance.getContextInstance().setVariable("TEMPLATE_TYPE", "SAMPLE_TEMPLATE1");

        procesInstance.getContextInstance().setVariable("EMAIL_ADDRESSES", new EmailAddress("someName", "somebody@mail.com"));

        token.signal("accept");
        token.signal("accept");
        token.signal("normal");
        token.signal("accept");

        List<EmailAddress> addresses = new ArrayList<EmailAddress>();
        addresses.add(new EmailAddress("Helmut", "helmut@mail.de"));
        addresses.add(new EmailAddress("Ivan", "ivan@mail.ru"));
        EmailAddresses eMailAddressesList = new EmailAddresses(addresses);
        procesInstance.getContextInstance().setVariable("TEMPLATE_TYPE", "SAMPLE_TEMPLATE2");
        procesInstance.getContextInstance().setVariable("EMAIL_ADDRESSES", eMailAddressesList);


        token.signal("accept");        

        Domain retrivedDomain = domainDAO.get(domain.getName());

        assert (retrivedDomain.getWhoisServer().equals("newwhoisserver") &&
                retrivedDomain.getRegistryUrl() == null);

        txMgr.commit(txStatus);
        processDAO.close();
    }
}
