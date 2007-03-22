package org.iana.rzm.trans;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Address;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.jbpm.JbpmContextFactory;

import java.net.URL;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */

public class JbpmUpdateDomainImpl implements JbpmUpdateDomain{
    ApplicationContext appCtx;
    TransactionManager transMgr;
    ProcessDAO processDAO;
    DomainDAO domainDAO;
    HibernateTransactionManager hibernateTransactionManager;

    public void setContext(ApplicationContext ctx) {
        appCtx = ctx;
        transMgr = (TransactionManager) appCtx.getBean("transactionManagerBean");
        processDAO = (ProcessDAO) appCtx.getBean("processDAO");
        domainDAO = (DomainDAO) appCtx.getBean("domainDAO");

    }

    public boolean doUpdate() throws Exception {
        processDAO.deploy(getDefinedProcess());
        processDAO.close();


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
        domain.setRegistryUrl(new URL("http://www.oldregistryurl.org"));
        domain.setSupportingOrg(supportingOrg);
        domain.addTechContact(new Contact("aaaaaa"));
        domainDAO.create(domain);

        Domain clonedDomain = (Domain) domain.clone();
        clonedDomain.setWhoisServer("newwhoisserver");
        clonedDomain.setRegistryUrl(null);
        clonedDomain.setSupportingOrg(clonedSupportingOrg);
        Contact newContact = new Contact("aaaaaa");
        newContact.addEmail("noContact new emial");
        clonedDomain.addTechContact(newContact);

        
        JbpmContextFactory fact = (JbpmContextFactory) appCtx.getBean("jbpmContextFactory");
        transMgr.setJBPMContext(fact.getJbpmContext());
        transMgr.createDomainModificationTransaction(clonedDomain);

        ProcessInstance procesInstance = processDAO.getProcessInstance(1L);

        Token token = procesInstance.getRootToken();
        token.signal();
        token.signal("accept");
        token.signal("accept");
        token.signal("normal");
        token.signal("accept");
        token.signal("accept");

        Domain retrivedDomain = domainDAO.get(clonedDomain.getName());

        return (retrivedDomain.getWhoisServer().equals("newwhoisserver") &&
                retrivedDomain.getRegistryUrl().equals(new URL("http://supernewaddress.org/")));
    }

    private ProcessDefinition getDefinedProcess() throws Exception {
        FileReader fileReader = new FileReader("eiana-trans\\etc\\processes\\domain-modification.xml");
        return ProcessDefinition.parseXmlReader(fileReader);
    }
}
