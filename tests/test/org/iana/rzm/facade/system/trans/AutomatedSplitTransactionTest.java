package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.IPAddressVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * It tests an automated splitting of transaction in case of name server change
 * that impacts other parties.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class AutomatedSplitTransactionTest extends CommonGuardedSystemTransaction {

    protected void initTestData() {

        Domain domain2 = new Domain("impacteddomain-1");
        Host h1 = new Host("impactedhost-1");
        h1.addIPAddress("1.1.1.1");
        domain2.addNameServer(h1);
        domain2.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain2);

        Domain domain3 = new Domain("impacteddomain-2");
        domain3.addNameServer(h1);
        domain3.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain3);

        Domain domain4 = new Domain("impacteddomain-3");
        Host h2 = new Host("impactedhost-2");
        h2.addIPAddress("2.2.2.2");
        domain4.addNameServer(h2);
        domain4.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain4);


        Domain domain1 = new Domain("automatedsplittest");
        domain1.setSupportingOrg(new Contact("so-name"));
        domain1.addNameServer(h1);
        domain1.addNameServer(h2);
        domainManager.create(domain1);

        Domain domain5 = new Domain("automatedsplittest2");
        domain5.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain5);

    }

    @Test
    public void testSingleTransaction() throws Exception {
        IDomainVO domain = getDomain("automatedsplittest");
        List<HostVO> hosts = new ArrayList<HostVO>();
        hosts.add(new HostVO("impactedhost-1"));
        hosts.add(new HostVO("impactedhost-2"));
        hosts.add(new HostVO("notimpactedhost"));
        domain.setNameServers(hosts);
        domain.setRegistryUrl("automatedsplittest.registry.url");

        setDefaultUser();
        List<TransactionVO> trans = GuardedSystemTransactionService.createTransactions(domain, false);
        closeServices();

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
        IDomainVO domain = getDomain("automatedsplittest");
        List<HostVO> hosts = new ArrayList<HostVO>();
        hosts.add(new HostVO("impactedhost-1"));
        hosts.add(new HostVO("impactedhost-2"));
        hosts.add(new HostVO("notimpactedhost"));
        domain.setNameServers(hosts);
        domain.setRegistryUrl("automatedsplittest.registry.url");

        setDefaultUser();
        List<TransactionVO> trans = GuardedSystemTransactionService.createTransactions(domain, true);
        closeServices();

        // 1 group/trans -> impactedhost-1
        // 2 group/trans -> impactedhost-2
        // 3 group/trans -> notimpactedhost
        // 4 group/trans -> registry url
        assert trans != null && trans.size() == 4;
    }

    @Test
    public void testNoGlueChangeTransaction() throws Exception {
        IDomainVO domain = getDomain("automatedsplittest2");
        HostVO h1 = new HostVO("impactedhost-1");
        h1.setAddress(new IPAddressVO("1.1.1.1", IPAddressVO.Type.IPv4));
        HostVO h2 = new HostVO("impactedhost-2");
        h2.setAddress(new IPAddressVO("2.2.2.2", IPAddressVO.Type.IPv4));
        domain.getNameServers().add(h1);
        domain.getNameServers().add(h2);
        domain.getNameServers().add(new HostVO("notimpactedhost"));
        domain.setRegistryUrl("automatedsplittest.registry.url");

        setDefaultUser();
        List<TransactionVO> trans = GuardedSystemTransactionService.createTransactions(domain, false);
        closeServices();

        // 1 group/trans -> impactedhost-1
        // 2 group/trans -> impactedhost-2
        // 3 group/trans -> notimpactedhost
        // 4 group/trans -> registry url
        assert trans.size() == 1 : "transaction created: " + trans.size();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTransactions() {
        processDAO.deleteAll();
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
//        for (RZMUser user : userManager.findAll())
//            userManager.delete(user);
//        domainManager.deleteAll();
//        hostManager.deleteAll();
    }
}
