package org.iana.rzm.trans.change;

import org.iana.objectdiff.*;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"change", "eiana-trans"})
public class DomainChangeApplicatorTest {

    private DiffConfiguration config = (DiffConfiguration) SpringTransApplicationContext.getInstance().getContext().getBean("diffConfig");

    @Test
    public void testRegistryUrlApplication() {
        Domain src = DomainChangeDetectorTest.createDomain();
        src.setRegistryUrl("old.url");
        Domain dst = DomainChangeDetectorTest.createDomain();
        dst.setRegistryUrl("new.url");
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testWhoisServerApplication() {
        Domain src = DomainChangeDetectorTest.createDomain();
        src.setWhoisServer("old.whois");
        Domain dst = DomainChangeDetectorTest.createDomain();
        dst.setWhoisServer("new.whois");
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testSupportingOrgApplication() {
        Domain src = DomainChangeDetectorTest.createDomain();
        Domain dst = DomainChangeDetectorTest.createDomain();
        dst.getSupportingOrg().setName("new-supporting-org");
        dst.getSupportingOrg().setPhoneNumber("new-email");
        dst.getSupportingOrg().setEmail("new-email@post.org");
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testAdminContactsApplication() {
        Domain src = DomainChangeDetectorTest.createDomain();
        Domain dst = DomainChangeDetectorTest.createDomain();
        dst.getAdminContact().setFaxNumber("new-fax");
        dst.setAdminContact(DomainChangeDetectorTest.createContact("new-ac"));
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }

    @Test
    public void testIPAddressAdditionApplication() {
        Host src = DomainChangeDetectorTest.createHost("host.test");
        Host dst = DomainChangeDetectorTest.createHost("host.test");
        dst.addIPAddress("3.3.3.3");
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }
}
