package org.iana.rzm.domain.dao.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.conf.SpringDomainsApplicationContext;
import org.iana.criteria.Equal;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.util.List;

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
        List<Domain> domains = dao.find(new Equal("name", "dao.org"));
        int i=0;
    }

    @Test(dependsOnMethods = {"testDomainFindByCrit"})
    public void testDelete() throws Exception {
        dao.delete(dao.get("dao.org"));
    }

}
