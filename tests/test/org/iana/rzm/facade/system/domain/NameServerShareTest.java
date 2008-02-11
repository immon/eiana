package org.iana.rzm.facade.system.domain;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.CommonGuardedSystemTransaction;
import org.iana.rzm.user.RZMUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * It tests whether name servers' share flag is set appropriately.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"facade-system"})
public class NameServerShareTest  extends CommonGuardedSystemTransaction {

    @Test
    public void testNotShared_Create() throws Exception {
        setDefaultUser();

        Domain domain = new Domain("share1");
        domain.addNameServer(new Host("share1.host1"));
        domain.addNameServer(new Host("share1.host2"));
        domain.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain);

        IDomainVO created = gsds.getDomain("share1");
        List<HostVO> nss = created.getNameServers();
        assert nss.size() == 2;
        for (HostVO host : nss) {
            assert !host.isShared();
        }
    }

    @Test
    public void testShared_CreateCreate() throws Exception {
        setDefaultUser();

        Domain domain1 = new Domain("share2");
        domain1.addNameServer(new Host("share2.host1"));
        domain1.addNameServer(new Host("share2.host2"));
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

        Domain domain2 = new Domain("share3");
        domain2.addNameServer(new Host("share2.host1"));
        domain2.addNameServer(new Host("share2.host2"));
        domain2.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain2);

        IDomainVO created1 = gsds.getDomain("share2");
        List<HostVO> nss1 = created1.getNameServers();
        assert nss1.size() == 2;
        for (HostVO host : nss1) {
            assert host.isShared();
        }

        IDomainVO created2 = gsds.getDomain("share3");
        List<HostVO> nss2 = created2.getNameServers();
        assert nss2.size() == 2;
        for (HostVO host : nss2) {
            assert host.isShared();
        }
    }

    @Test
    public void testNotShared_CreateCreateRemove() throws Exception {
        setDefaultUser();

        Domain domain1 = new Domain("share4");
        domain1.addNameServer(new Host("share4.host1"));
        domain1.addNameServer(new Host("share4.host2"));
        domain1.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain1);

        Domain domain2 = new Domain("share5");
        domain2.addNameServer(new Host("share4.host1"));
        domain2.addNameServer(new Host("share4.host2"));
        domain2.setSupportingOrg(new Contact("so-name"));
        domainManager.create(domain2);

        domainManager.delete(domain2);

        IDomainVO created = gsds.getDomain("share4");
        List<HostVO> nss = created.getNameServers();
        assert nss.size() == 2;
        for (HostVO host : nss) {
            assert !host.isShared();
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
