package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.exe.ProcessInstance;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class ImpactedPartyWorkflowTest extends CommonGuardedSystemTransaction {

    RZMUser userIANA;

    @BeforeClass
    public void init() {
        Domain domain1 = new Domain("impactedpartytest");
        domain1.setSupportingOrg(new Contact("so-name"));
        domain1.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain1.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain1);

        Domain domain2 = new Domain("impacteddomain-test1");
        domain2.addNameServer(new Host("impactedhost-test1"));
        domain2.setSupportingOrg(new Contact("so-name"));
        domain2.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain2.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain2);

        Domain domain3 = new Domain("impacteddomain-test2");
        domain3.addNameServer(new Host("impactedhost-test1"));
        domain3.setSupportingOrg(new Contact("so-name"));
        domain3.setAdminContact(new Contact("ac-name", "org", null, "", "", "a@x.pl", true));
        domain3.setTechContact(new Contact("tc-name", "org", null, "", "", "a@x.pl", true));
        domainManager.create(domain3);

        Domain domain4 = new Domain("impacteddomain-test3");
        domain4.addNameServer(new Host("impactedhost-test2"));
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
        acceptPENDING_IMPACTED_PARTIES(userIANA, t1.getTransactionID(), 2);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        try {
            for (ProcessInstance pi : processDAO.findAll())
                processDAO.delete(pi);
        } finally {
            processDAO.close();
        }
        for (RZMUser user : userManager.findAll())
            userManager.delete(user);
        for (Domain domain : domainManager.findAll())
            domainManager.delete(domain.getName());
    }
}
