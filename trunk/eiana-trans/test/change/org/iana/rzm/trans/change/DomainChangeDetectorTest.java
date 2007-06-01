package org.iana.rzm.trans.change;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.ChangeDetector;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.conf.SpringTransApplicationContext;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"change", "eiana-trans"})
public class DomainChangeDetectorTest {

    private DiffConfiguration config = (DiffConfiguration) SpringTransApplicationContext.getInstance().getContext().getBean("diffConfig");

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
        dst.getSupportingOrg().setEmail("new-so-email@post.org");
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
        dst.getAddress().setTextAddress("new.ta");
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 1 && fieldChanges.get("address").isModification();
    }

    @Test
    public void testContactOrgAndRoleModification() {
        Contact src = createContact("contact");
        Contact dst = createContact("contact");
        dst.setOrganization("new.org");
        dst.setRole(true);
        Change change = ChangeDetector.diff(src, dst, config);
        assert change != null && change.isModification();
        ObjectChange objectChange = (ObjectChange) change;
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
        assert fieldChanges.size() == 2 && fieldChanges.get("organization").isModification() && fieldChanges.get("role").isModification();
    }

    @Test
    public void testIPAddressAddition() {
        Host src = createHost("host.test");
        Host dst = createHost("host.test");
        dst.addIPAddress("3.3.3.3");
        Change change = ChangeDetector.diff(src, dst, config);
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
        ret.setObjId(new Long(prefix.hashCode()));
        ret.setOrganization("org");
        ret.setName(prefix + "-name");
        ret.setEmail(prefix+"-email@post.org");
        ret.setPhoneNumber(prefix+"-phone");
        ret.setFaxNumber(prefix+"-fax");
        ret.setAddress(new Address(prefix+"-ta", "CC"));
        ret.setRole(false);
        return ret;
    }

    final static Host createHost(String hostName) {
        Host ret = new Host(hostName);
        ret.addIPAddress(IPAddress.createIPAddress("1.1.1.1"));
        ret.addIPAddress(IPAddress.createIPAddress("2.2.2.2"));
        return ret;
    }

    final static List<String> createStringList(String suffix, int size) {
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < size; ++i) ret.add(i + "-" + suffix);
        return ret;
    }
}
