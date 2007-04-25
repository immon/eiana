package org.iana.rzm.trans.change;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.ChangeDetector;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"change", "eiana-trans"})
public class DomainChangeDetectorTest {

    private DiffConfiguration config = DomainDiffConfiguration.getInstance();

    @Test
    public void testDomainNoChange() {
        Domain src = createDomain();
        Domain dst = createDomain();
        Change change = ChangeDetector.diff(src, dst, config);
        assert change == null;
    }

    @Test
    public void testWhoisRemoval() {
        Domain src = createDomain();
        Domain dst = createDomain();
        dst.setWhoisServer(null);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("whoisServer").isRemoval();
    }

    @Test
    public void testWhoisAddition() {
        Domain src = createDomain();
        src.setWhoisServer(null);
        Domain dst = createDomain();
        dst.setWhoisServer("new.whois.server");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("whoisServer").isAddition();
    }

    @Test
    public void testWhoisModification() {
        Domain src = createDomain();
        src.setWhoisServer("whois.server");
        Domain dst = createDomain();
        dst.setWhoisServer("new.whois.server");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("whoisServer").isModification();
    }

    @Test
    public void testRegistryUrlRemoval() {
        Domain src = createDomain();
        Domain dst = createDomain();
        dst.setRegistryUrl(null);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("registryUrl").isRemoval();
    }

    @Test
    public void testRegistryUrlAddition() {
        Domain src = createDomain();
        src.setRegistryUrl(null);
        Domain dst = createDomain();
        dst.setRegistryUrl("new.registry.url");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("registryUrl").isAddition();
    }

    @Test
    public void testRegistryUrlModification() {
        Domain src = createDomain();
        src.setRegistryUrl("registry.url");
        Domain dst = createDomain();
        dst.setRegistryUrl("new.registry.url");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("registryUrl").isModification();
    }

    @Test
    public void testSupportingOrgModification() {
        Domain src = createDomain();
        Domain dst = createDomain();
        dst.getSupportingOrg().setEmails(createStringList("new-so-email@post.org", 2));
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("supportingOrg").isModification();
    }

    @Test
    public void testAdminContactAddition() {
        Domain src = createDomain();
        Domain dst = createDomain();
        dst.addAdminContact(createContact("new-ac"));
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("adminContacts").isModification();
    }

    @Test
    public void testContactAddressModification() {
        Contact src = createContact("contact");
        Contact dst = createContact("contact");
        dst.getAddresses().get(0).setTextAddress("new.ta");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("addresses").isModification();
    }


    final static Domain createDomain() {
        Domain ret = new Domain("test.change");
        ret.setSupportingOrg(createContact("so"));
        ret.addAdminContact(createContact("ac"));
        ret.addTechContact(createContact("tc"));
        ret.setWhoisServer("whois.server");
        ret.setRegistryUrl("registry.url");
        return ret;
    }

    final static Contact createContact(String prefix) {
        Contact ret = new Contact();
        ret.setName(prefix + "-name");
        ret.setEmails(createStringList(prefix+"-email@post.org", 1));
        ret.setPhoneNumbers(createStringList(prefix+"-phone", 1));
        ret.setFaxNumbers(createStringList(prefix+"-fax", 1));
        ret.addAddress(new Address(prefix+"-ta", "CC"));
        return ret;
    }

    final static List<String> createStringList(String suffix, int size) {
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < size; ++i) ret.add(i + "-" + suffix);
        return ret;
    }
}
