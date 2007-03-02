package org.iana.rzm.domain.hibernate.test.accuracy.hibernate;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.hibernate.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.domain.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.net.URL;

/**
 * @author Jakub Laszkiewicz
 */
public class DomainHibernateMappingTest extends HibernateMappingUnitTest<Domain> {
    protected Domain create() throws Exception {
        return HibernateMappingTestUtil.setupDomain(new Domain("iana.org"));
    }

    protected Domain change(Domain o) throws Exception {
        o.setName("changed-iana.org");
        o.setRegistryUrl(new URL("http://changed-registry.com"));
        o.setSpecialInstructions("changed special instructions");
        o.setState(Domain.State.OPERATIONS_PENDING);
        o.setStatus(Domain.Status.ACTIVE);
        o.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), "changed supporting", true));
        o.setWhoisServer("changed.whoid.server");
        o.removeAdminContact(o.getAdminContacts().iterator().next());
        o.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), "admin3", true));
        o.removeBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        o.addBreakpoint(Domain.Breakpoint.NS_CHANGE_EXT_REVIEW);
        o.removeNameServer(o.getNameServers().iterator().next());
        o.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns3." + o.getName())));
        o.removeTechContact(o.getTechContacts().iterator().next());
        o.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), "tech3", true));
        return o;
    }

    protected Serializable getId(Domain o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-domains"})
    public void testDomain() throws Exception {
        super.test();
    }
}
