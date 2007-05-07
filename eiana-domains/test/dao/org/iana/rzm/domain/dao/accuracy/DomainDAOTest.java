package org.iana.rzm.domain.dao.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.conf.SpringDomainsApplicationContext;
import org.iana.criteria.Equal;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential=true, groups = {"dao", "eiana-domains", "DomainDAOTest"})
public class DomainDAOTest {

    private DomainDAO dao;

    @BeforeClass
    public void init() {
        dao = (DomainDAO) SpringDomainsApplicationContext.getInstance().getContext().getBean("domainDAO");
    }

    @Test
    public void testDomainCreate() throws Exception {
        Domain domainCreated = new Domain("dao.org");
        domainCreated.addNameServer(new Host("host.dao.org"));
        dao.create(domainCreated);
        Domain domainRetrieved = dao.get(domainCreated.getObjId());
        assert "dao.org".equals(domainRetrieved.getName());
        domainRetrieved = dao.get(domainCreated.getName());
        assert "dao.org".equals(domainRetrieved.getName());
    }

    @Test(dependsOnMethods = {"testDomainCreate"})
    public void testDomainUpdate() throws Exception {
    }

    @Test(dependsOnMethods = {"testDomainUpdate"})
    public void testDomainFindByCrit() {
        List<Domain> domains = dao.find(new Equal("name.name", "dao.org"));
        assert domains.size() == 1;
        assert domains.iterator().next().getName().equals("dao.org");
    }

    @Test(dependsOnMethods = {"testDomainFindByCrit"})
    public void testDomainDelegatedTo() throws Exception {
        Set<String> hostNames = new HashSet<String>();
        hostNames.add("host.dao.org");
        List<Domain> domains = dao.findDelegatedTo(hostNames);
        assert domains != null && domains.size() == 1;
        assert domains.iterator().next().getName().equals("dao.org");
    }

    @Test(dependsOnMethods = {"testDomainDelegatedTo"})
    public void testDelete() throws Exception {
        dao.delete(dao.get("dao.org"));
    }

    @AfterClass
    public void destroy() {
        Domain domain = dao.get("dao.org");
        if (domain != null) dao.delete(dao.get("dao.org"));        
    }
}
