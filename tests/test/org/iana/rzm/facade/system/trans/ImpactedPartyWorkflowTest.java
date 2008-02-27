package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.IsNull;
import org.iana.criteria.Not;
import org.iana.criteria.In;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class ImpactedPartyWorkflowTest extends CommonGuardedSystemTransaction {

    RZMUser userIANA;

    RZMUser userAC;

    @BeforeClass
    public void init() {
        Domain domain1 = new Domain("impactedpartytest");
        domain1.setSupportingOrg(new Contact("so-name"));
        domain1.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain1.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain1);

        Domain domain2 = new Domain("impacteddomain-test1");
        Host h1 = new Host("impactedhost-test1");
        h1.addIPAddress("1.1.1.1");
        domain2.addNameServer(h1);
        domain2.setSupportingOrg(new Contact("so-name"));
        domain2.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain2.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain2);

        Domain domain3 = new Domain("impacteddomain-test2");
        domain3.addNameServer(h1);
        domain3.setSupportingOrg(new Contact("so-name"));
        domain3.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain3.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain3);

        Domain domain4 = new Domain("impacteddomain-test3");
        Host h2 = new Host("impactedhost-test2");
        h2.addIPAddress("2.2.2.2");
        domain4.addNameServer(h2);
        domain4.setSupportingOrg(new Contact("so-name"));
        domain4.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain4.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain4);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();

        userIANA = new RZMUser();
        userIANA.setLoginName("gstsignaliana");
        userIANA.setFirstName("IANAuser");
        userIANA.setLastName("lastName");
        userIANA.setEmail("email@some.com");
        userIANA.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(userIANA);

        userAC = new RZMUser();
        userAC.setLoginName("acuser");
        userAC.setFirstName("ac");
        userAC.setLastName("lastName");
        userAC.setEmail("email@some.com");
        userAC.addRole(new SystemRole(SystemRole.SystemType.AC, "impacteddomain-test1", true, false));
        userManager.create(userAC);
    }

    @Test
    public void testSingleTransaction() throws Exception {
        IDomainVO domain = getDomain("impactedpartytest");
        domain.getNameServers().add(new HostVO("impactedhost-test1"));
        domain.getNameServers().add(new HostVO("impactedhost-test2"));
        domain.getNameServers().add(new HostVO("notimpactedhost-test"));
        domain.setRegistryUrl("impactedpartytest.registry.url");

        setDefaultUser();
        List<TransactionVO> trans = gsts.createTransactions(domain, false);
        closeServices();

        // 1 group/trans -> impactedhost-test1
        // 2 group/trans -> impactedhost-test2
        // 3 group/trans -> notimpactedhost + registry url
        TransactionVO t1 = trans.get(0);
        acceptPENDING_CONTACT_CONFIRMATION_IMPACTED_PARTIES(userIANA, t1.getTransactionID(), 2);
        acceptPENDING_IMPACTED_PARTIES(userAC, t1.getTransactionID(), 4);

        TransactionVO t2 = trans.get(1);
        acceptPENDING_CONTACT_CONFIRMATION_IMPACTED_PARTIES(userIANA, t2.getTransactionID(), 2);
        acceptPENDING_IMPACTED_PARTIES(userIANA, t2.getTransactionID(), 2);
    }

    @Test
    public void testFindTransactionsByCriteriaNotNullName() throws Exception {
        IDomainVO domain = getDomain("impactedpartytest");
        domain.getNameServers().add(new HostVO("impactedhost-test1"));
        domain.getNameServers().add(new HostVO("impactedhost-test2"));
        domain.getNameServers().add(new HostVO("notimpactedhost-test"));
        domain.setRegistryUrl("impactedpartytest.registry.url");

        setDefaultUser();
        List<TransactionVO> trans = gsts.createTransactions(domain, false);
        Criterion impactOnDomains = new Not(new IsNull(TransactionCriteriaFields.IMPACTED_DOMAIN));
        List<TransactionVO> found = gsts.find(impactOnDomains);
        assert found.size() == 2;
        closeServices();
    }

    @Test
    public void testFindTransactionsByCriteriaInNames() throws Exception {
        IDomainVO domain = getDomain("impactedpartytest");
        domain.getNameServers().add(new HostVO("impactedhost-test1"));
        domain.getNameServers().add(new HostVO("impactedhost-test2"));
        domain.getNameServers().add(new HostVO("notimpactedhost-test"));
        domain.setRegistryUrl("impactedpartytest.registry.url");

        setDefaultUser();
        List<TransactionVO> trans = gsts.createTransactions(domain, false);
        Set<String> names = new HashSet<String>();
        names.add("impacteddomain-test1");
        names.add("impacteddomain-test2");
        Criterion impactOnDomains = new In(TransactionCriteriaFields.IMPACTED_DOMAIN, names);
        List<TransactionVO> found = gsts.find(impactOnDomains);
        assert found.size() == 1;
        closeServices();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
