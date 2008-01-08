package org.iana.rzm.trans;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.jbpm.handlers.ticketingservice.RequestTrackerTicket;
import org.iana.test.spring.TransactionalSpringContextTests;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(sequential = true)
public class RequestTrackerTicketTest extends TransactionalSpringContextTests {
    protected DomainManager domainManager;
    protected TransactionManager transactionManagerBean;
    protected ProcessDAO processDAO;

    public RequestTrackerTicketTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() {
        try {
            processDAO.deploy(DefinedTestProcess.getDefinition());
        } finally {
            processDAO.close();
        }
    }

    public void testTicketId() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-id");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.addNameServer(new Host("ns2.rttickettest"));
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            transaction.setTicketID(111L);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            assert rtt.getId() != null;
            assert new Long(111L).equals(rtt.getId());
        } finally {
            processDAO.close();
        }
    }

    public void testTld() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-tld");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domain.setIanaCode("iana-code");
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.addNameServer(new Host("ns2.rttickettest"));
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            assert rtt.getTld() != null;
            assert "rttickettest-tld".equals(rtt.getTld());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeNs() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-ns");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.addNameServer(new Host("ns2.rttickettest"));
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "ns".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeAc() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-ac");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.setAdminContact(new Contact("admin1"));
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "ac change".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeAcData() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-acdata");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.getAdminContact().setEmail("admin@admin.org");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "ac-data".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeTc() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-tc");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.setTechContact(new Contact("tech1"));
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "tc change".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeTcData() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-tcdata");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.getTechContact().setEmail("tech@tech.org");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "tc-data".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeSo() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-so");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.setSupportingOrg(new Contact("supporg1"));
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "so change".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeSoData() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-sodata");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.getSupportingOrg().setEmail("supporg@supporg.org");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "so-data".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeUrl() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-url");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.setRegistryUrl("regurl1");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "url".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testRequestTypeWhois() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-whois");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.setWhoisServer("whois1");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            List<String> rt = rtt.getRequestType();
            assert rt != null;
            assert rt.size() == 1;
            assert "whois".equals(rt.iterator().next());
        } finally {
            processDAO.close();
        }
    }

    public void testIanaState() throws Exception {
        try {
            Domain domain = new Domain("rttickettest-ianastate");
            domain.setSupportingOrg(new Contact("supporg"));
            domain.setAdminContact(new Contact("admin"));
            domain.setTechContact(new Contact("tech"));
            domain.setRegistryUrl("regurl");
            domain.setWhoisServer("whois");
            domain.addNameServer(new Host("ns1.rttickettest"));
            domainManager.create(domain);
            Domain modified = domain.clone();
            modified.setWhoisServer("whois1");
            Transaction transaction = transactionManagerBean.createDomainModificationTransaction(modified, null);
            transaction.transitTo(new ContactIdentity(""), "ADMIN_CLOSED");
            RequestTrackerTicket rtt = new RequestTrackerTicket(transaction);
            assert rtt.getIanaState() != null;
            assert "admin-close".equals(rtt.getIanaState());
        } finally {
            processDAO.close();
        }
    }

    protected void cleanUp() throws Exception {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
