package org.iana.rzm.domain.hibernate.test.accuracy;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingUnitTest;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingTestUtil;
import org.testng.annotations.Test;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;

import java.io.Serializable;
import java.net.URL;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains"})
public class DomainHibernateMappingTest extends HibernateMappingUnitTest<Domain> {

    long domainID;

    protected Domain create() {
        return HibernateMappingTestUtil.setupDomain(new Domain("iana.org"));
    }

    protected Domain change(Domain o) {
        o.setName("changed-iana.org");
        o.setRegistryUrl("http://changed-registry.com");
        o.setSpecialInstructions("changed special instructions");
        o.setStatus(Domain.Status.ACTIVE);
        // commented-out since it requires session.merge to be used in a new approach (this new approach avoids leaving trash contacts after update).
        // o.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), "changed supporting", true, "US"));
        o.setWhoisServer("changed.whoid.server");
        o.removeAdminContact(o.getAdminContacts().iterator().next());
        o.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), "admin3", true, "US"));
        o.removeBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        o.addBreakpoint(Domain.Breakpoint.NS_CHANGE_EXT_REVIEW);
        o.removeNameServer(o.getNameServers().iterator().next());
        o.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns3." + o.getName())));
        o.removeTechContact(o.getTechContacts().iterator().next());
        o.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), "tech3", true, "US"));
        return o;
    }

    protected Serializable getId(Domain o) {
        return o.getObjId();
    }

    @Test
    public void testDomainHCreate() throws Exception {
        final Domain object = create();
        runInTransaction(new HibernateSeq() {
            public void run(Session session) throws HibernateException {
                session.save(object);
                domainID = object.getObjId();
            }
        });

        runInTransaction(new HibernateSeq() {
            public void run(Session session) throws HibernateException {
                Domain object1 = (Domain) session.get(Domain.class, domainID);
                assert create().equals(object1);
            }
        });
    }

    private Domain object2;

    @Test(dependsOnMethods = "testDomainHCreate")
    public void testDomainHUpdate() throws Exception {
        runInTransaction(new HibernateSeq() {
            public void run(Session session) throws HibernateException {
                object2 = (Domain) session.get(Domain.class, domainID);
                object2 = change(object2);
                session.merge(object2);
            }
        });

        runInTransaction(new HibernateSeq() {
            public void run(Session session) throws HibernateException {
                Domain object3 = (Domain) session.get(Domain.class, domainID);
                assert change(create()).equals(object3);
            }
        });
    }

    @Test(dependsOnMethods = "testDomainHUpdate")
    public void testDomainHDelete() throws Exception {
        runInTransaction(new HibernateSeq() {
            public void run(Session session) throws HibernateException {
                session.delete(session.get(Domain.class, domainID));
            }
        });

        runInTransaction(new HibernateSeq() {
            public void run(Session session) throws HibernateException {
                Domain object4 = (Domain) session.get(Domain.class, domainID);
                assert object4 == null;
            }
        });
    }
}
