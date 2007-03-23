package org.iana.rzm.domain.hibernate.test.stress;

import org.iana.rzm.domain.hibernate.test.common.HibernateOperationStressTest;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.testng.annotations.Test;

import java.util.List;
import java.net.URL;

/**
 * @author Jakub Laszkiewicz
 */
public class DomainUpdateHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        Domain domain = (Domain) o;
        domain.setName("changed-" + domain.getName());
        domain.setRegistryUrl("http://changed-registry.com");
        domain.setSpecialInstructions("changed special instructions");
        domain.setState(Domain.State.OPERATIONS_PENDING);
        domain.setStatus(Domain.Status.ACTIVE);
        domain.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), "changed supporting", true));
        domain.setWhoisServer("changed.whoid.server");
        domain.removeAdminContact(domain.getAdminContacts().iterator().next());
        domain.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), "admin3", true));
        domain.removeBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domain.addBreakpoint(Domain.Breakpoint.NS_CHANGE_EXT_REVIEW);
        domain.removeNameServer(domain.getNameServers().iterator().next());
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns3." + domain.getName())));
        domain.removeTechContact(domain.getTechContacts().iterator().next());
        domain.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), "tech3", true));
        session.save(domain);
    }

    protected List getList() throws Exception {
        return session.createCriteria(Domain.class).list();
    }

    @Test(groups = {"hibernate", "eiana-domains","stress"})
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
