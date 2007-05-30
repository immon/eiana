package org.iana.rzm.facade.system.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.trans.conf.DefinedTestProcess;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.List;

/**
 * It tests an automated splitting of transaction in case of name server change
 * that impacts other parties.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class AutomatedSplitTransactionTest extends CommonGuardedSystemTransaction {

    RZMUser iana;

    @BeforeClass
    public void init() {
        iana = new RZMUser("fn", "ln", "org", "iana", "iana@nowhere", "", false);
        iana.addRole(new AdminRole(AdminRole.AdminType.IANA));
        userManager.create(iana);

        Domain domain1 = new Domain("automatedsplittest");
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

        Domain domain2 = new Domain("impacteddomain-1");
        domain2.addNameServer(new Host("impactedhost-1"));
        domain2.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain2);

        Domain domain3 = new Domain("impacteddomain-2");
        domain3.addNameServer(new Host("impactedhost-1"));
        domain3.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain3);

        Domain domain4 = new Domain("impacteddomain-3");
        domain4.addNameServer(new Host("impactedhost-2"));
        domain4.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain4);

        processDAO.deploy(DefinedTestProcess.getDefinition());
        processDAO.close();
    }

    @Test
    public void testSingleTransaction() throws Exception {
        IDomainVO domain = getDomain("automatedsplittest", iana);
        domain.getNameServers().add(new HostVO("impactedhost-1"));
        domain.getNameServers().add(new HostVO("impactedhost-2"));
        domain.getNameServers().add(new HostVO("notimpactedhost"));
        domain.setRegistryUrl("automatedsplittest.registry.url");

        setGSTSAuthUser(iana);
        List<TransactionVO> trans = gsts.createTransactions(domain, false);

        // 1 group/trans -> impactedhost-1
        // 2 group/trans -> impactedhost-2
        // 3 group/trans -> notimpactedhost + registry url
        assert trans != null && trans.size() == 3;
    }

    // domain1 -> not delegated
    // domain2 -> host1
    // domain3 -> host1
    // domain4 -> host2

    // domain1 -> host1, host2, someotherhost, registry url
    @Test
    public void testSeparatedTransaction() throws Exception {
        IDomainVO domain = getDomain("automatedsplittest", iana);
        domain.getNameServers().add(new HostVO("impactedhost-1"));
        domain.getNameServers().add(new HostVO("impactedhost-2"));
        domain.getNameServers().add(new HostVO("notimpactedhost"));
        domain.setRegistryUrl("automatedsplittest.registry.url");

        setGSTSAuthUser(iana);
        List<TransactionVO> trans = gsts.createTransactions(domain, true);

        // 1 group/trans -> impactedhost-1
        // 2 group/trans -> impactedhost-2
        // 3 group/trans -> notimpactedhost
        // 4 group/trans -> registry url
        assert trans != null && trans.size() == 4;
    }

    @AfterClass (alwaysRun = true)
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
