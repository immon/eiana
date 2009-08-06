package org.iana.rzm.facade.system.trans;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.IPAddressVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"facade-system"})
public class NameServerChangeNotAllowedTest extends CommonGuardedSystemTransaction {

    protected void initTestData() {
        Domain domain1 = new Domain("domain1");
        domain1.setSupportingOrg(new Contact("so-name"));
        Host host = new Host("ns1.foo");
        host.addIPAddress("122.122.122.122");
        domain1.addNameServer(host);
        domainManager.create(domain1);

        Domain domain2 = new Domain("domain2");
        domain2.setSupportingOrg(new Contact("so-name"));
        Host host2 = new Host("ns1.foo");
        host2.addIPAddress("122.122.122.122");
        domain2.addNameServer(host2);
        domainManager.create(domain2);

    }

    @Test
    public void testSameNameserverAdditionDifferentIPs() throws Exception {
        IDomainVO domain = getDomain("domain1");
        domain.setRegistryUrl("withdrawtest.registry.url");

        List<HostVO> hosts = domain.getNameServers();

        HostVO host = new HostVO("ns1.abc");
        host.setAddress(new IPAddressVO("123.123.123.123", IPAddressVO.Type.IPv4));
        hosts.add(host);

        domain.setNameServers(hosts);

        setDefaultUser();
        TransactionVO trans = GuardedSystemTransactionService.createTransactions(domain, false).get(0);

        long firstTransId = trans.getTransactionID();

        TransactionVO ret = GuardedSystemTransactionService.get(firstTransId);
        assert ret != null;

        domain = getDomain("domain2");
        domain.setRegistryUrl("withdrawtest.registry.url");

        hosts = domain.getNameServers();

        host = new HostVO("ns1.abc");
        host.setAddress(new IPAddressVO("123.123.123.124", IPAddressVO.Type.IPv4));

        hosts.add(host);

        domain.setNameServers(hosts);


        boolean wasException = false;

        try {
            //expected exception
            GuardedSystemTransactionService.createTransactions(domain, false).get(0);
        } catch (NameServerChangeNotAllowedException e) {
            assert e.getMessage().equals("for: [ns1.abc]");
            wasException = true;
        }

        GuardedSystemTransactionService.withdrawTransaction(firstTransId);

        closeServices();

        if (!wasException)
            throw new Exception("unreachable");

    }

    @Test
    public void testSameNameserverSameIPs() throws Exception {
        IDomainVO domain = getDomain("domain1");
        domain.setRegistryUrl("withdrawtest.registry.url");

        List<HostVO> hosts = domain.getNameServers();

        HostVO host = new HostVO("ns1.abc");
        host.setAddress(new IPAddressVO("123.123.123.123", IPAddressVO.Type.IPv4));
        hosts.add(host);

        domain.setNameServers(hosts);

        setDefaultUser();
        TransactionVO trans = GuardedSystemTransactionService.createTransactions(domain, false).get(0);

        TransactionVO ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        assert ret != null;

        domain = getDomain("domain2");
        domain.setRegistryUrl("withdrawtest.registry.url");

        hosts = domain.getNameServers();

        host = new HostVO("ns1.abc");
        host.setAddress(new IPAddressVO("123.123.123.123", IPAddressVO.Type.IPv4));

        hosts.add(host);

        domain.setNameServers(hosts);

        trans = GuardedSystemTransactionService.createTransactions(domain, false).get(0);

        ret = GuardedSystemTransactionService.get(trans.getTransactionID());
        assert ret != null;

    }


    @AfterClass(alwaysRun = true)
    public void cleanUp() {

    }
}