package org.iana.rzm.domain.hibernate.test.stress;

import org.iana.rzm.domain.hibernate.test.common.HibernateOperationStressTest;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains","stress", "eiana-domains-stress-update"},
        dependsOnGroups = {"eiana-domains-stress-create"})
public class DomainUpdateHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        Domain domain = (Domain) o;
        domain.setName("changed-" + domain.getName());
        domain.setRegistryUrl("http://changed-registry.com");
        domain.setSpecialInstructions("changed special instructions");
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), "changed supporting", true, "US"));
        domain.setWhoisServer("changed.whoid.server");
        domain.setAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), "admin3", true, "US"));
        domain.removeBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domain.addBreakpoint(Domain.Breakpoint.NS_CHANGE_EXT_REVIEW);
        domain.removeNameServer(domain.getNameServers().iterator().next());
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns3." + domain.getName())));
        domain.setTechContact(HibernateMappingTestUtil.setupContact(new Contact(), "tech3", true, "US"));
        session.save(domain);
    }

    protected List getList() throws Exception {
        return session.createCriteria(Domain.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
