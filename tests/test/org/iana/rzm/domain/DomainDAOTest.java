package org.iana.rzm.domain;

import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.Or;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(sequential = true, groups = {"dao", "eiana-domains", "DomainDAOTest"})
public class DomainDAOTest extends RollbackableSpringContextTest {

    protected DomainDAO domainDAO;

    public DomainDAOTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    public void init() {
//        dao = (DomainDAO) SpringApplicationContext.getInstance().getContext().getBean("domainDAO");
    }

    @Test
    public void testCount() throws Exception {
        int count = domainDAO.count(new Equal("name.name", "dao.org"));
        assert count == 0;
    }

    @Test(dependsOnMethods = "testCount")
    public void testFindCriteriaLimitOffset() {
        List<Domain> retrieved = domainDAO.find(new Equal("name.name", "dao.org"), 0, 1);
        assert retrieved.isEmpty();
        retrieved = domainDAO.find(new Equal("name.name", "dao.org"), 0, 10);
        assert retrieved.isEmpty();
    }

    @Test(dependsOnMethods = "testFindCriteriaLimitOffset")
    public void testDomainCreate() throws Exception {
        Domain domainCreated = new Domain("dao.org");
        domainCreated.addNameServer(new Host("host.dao.org"));
        domainDAO.create(domainCreated);
        Domain domainRetrieved = domainDAO.get(domainCreated.getObjId());
        assert "dao.org".equals(domainRetrieved.getName());
        domainRetrieved = domainDAO.get(domainCreated.getName());
        assert "dao.org".equals(domainRetrieved.getName());

        domainDAO.create(new Domain("second.org"));
    }

    @Test(dependsOnMethods = "testDomainCreate")
    public void testCount2() throws Exception {
        List<Criterion> criterias = new ArrayList<Criterion>();
        criterias.add(new Equal("name.name", "dao.org"));
        criterias.add(new Equal("name.name", "second.org"));
        int count = domainDAO.count(new Or(criterias));
        assert count == 2;
    }

    @Test(dependsOnMethods = "testCount2")
    public void testFindCriteriaLimitOffset2() {
        List<Criterion> criterias = new ArrayList<Criterion>();
        criterias.add(new Equal("name.name", "dao.org"));
        criterias.add(new Equal("name.name", "second.org"));

        List<Domain> retrieved = domainDAO.find(new Or(criterias), 0, 1);
        assert retrieved.size() == 1;
        assert retrieved.iterator().next().getName().equals("dao.org");

        retrieved = domainDAO.find(new Or(criterias), 1, 1);
        assert retrieved.size() == 1;
        assert retrieved.iterator().next().getName().equals("second.org");

        retrieved = domainDAO.find(new Or(criterias), 2, 1);
        assert retrieved.isEmpty();

        retrieved = domainDAO.find(new Or(criterias), 0, 2);
        assert retrieved.size() == 2;
        Iterator iterator = retrieved.iterator();
        assert ((Domain) iterator.next()).getName().equals("dao.org");
        assert ((Domain) iterator.next()).getName().equals("second.org");

        retrieved = domainDAO.find(new Or(criterias), 0, 5);
        assert retrieved.size() == 2;
        iterator = retrieved.iterator();
        assert ((Domain) iterator.next()).getName().equals("dao.org");
        assert ((Domain) iterator.next()).getName().equals("second.org");
    }

    @Test(dependsOnMethods = {"testFindCriteriaLimitOffset2"})
    public void testDomainUpdate() throws Exception {
    }

    @Test(dependsOnMethods = {"testDomainUpdate"})
    public void testDomainFindByCrit() {
        List<Domain> domains = domainDAO.find(new Equal("name.name", "dao.org"));
        assert domains.size() == 1;
        assert domains.iterator().next().getName().equals("dao.org");
    }

    @Test(dependsOnMethods = {"testDomainFindByCrit"})
    public void testDomainDelegatedTo() throws Exception {
        Set<String> hostNames = new HashSet<String>();
        hostNames.add("host.dao.org");
        List<Domain> domains = domainDAO.findDelegatedTo(hostNames);
        assert domains != null && domains.size() == 1;
        assert domains.iterator().next().getName().equals("dao.org");
    }

    @Test(dependsOnMethods = {"testDomainDelegatedTo"})
    public void testDelete() throws Exception {
        domainDAO.delete(domainDAO.get("dao.org"));
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
//        for (Domain domain : domainDAO.findAll())
//            domainDAO.delete(domain.getName());
    }
}
