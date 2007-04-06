package org.iana.rzm.trans.change;

import org.testng.annotations.Test;
import org.iana.rzm.domain.Domain;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"change", "eiana-trans"})
public class DomainChangeApplicatorTest {

    DiffConfiguration config = DomainDiffConfiguration.getInstance();

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
        dst.getSupportingOrg().addPhoneNumber("new-email");
        dst.getSupportingOrg().addEmail("new-email@post.org");
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
        dst.getAdminContacts().get(0).addFaxNumber("new-fax");
        dst.addAdminContact(DomainChangeDetectorTest.createContact("new-ac"));
        assert !src.equals(dst);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null;
        ChangeApplicator.applyChange(src, (ObjectChange) change, config);
        assert src.equals(dst);
    }
}
