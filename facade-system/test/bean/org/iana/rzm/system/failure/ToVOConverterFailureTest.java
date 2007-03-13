/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.system.failure;

import org.testng.annotations.Test;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.system.*;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.user.Role;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"ToVOConverter", "facade-system"})
public class ToVOConverterFailureTest {

    @Test (expectedExceptions = {IllegalArgumentException.class},
            groups = {"failure", "facade-system", "ToVOConverter"})
    public void testIPConverter1() {
        ToVOConverter.toIPAddressVO(null, null);
        }

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testIPConverter2() throws IllegalArgumentException {
        IPAddress fromIPAddress = null;

        assert ToVOConverter.toIPAddressVO(fromIPAddress) == null;

        Set<IPAddress> IPAddressesSet = null;
        assert ToVOConverter.toIPAddressVOSet(IPAddressesSet) == null;

        IPAddressesSet = new HashSet<IPAddress>();
        assert ToVOConverter.toIPAddressVOSet(IPAddressesSet).isEmpty();
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            groups = {"failure", "facade-system", "ToVOConverter"})
    public void testHostConverter1() {
        ToVOConverter.toHostVO(null, null);
    }

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testHostConverter2() {
        List<Host> hostsList = null;
        assert ToVOConverter.toHostVOList(hostsList) == null;
        hostsList = new ArrayList<Host>();
        assert ToVOConverter.toHostVOList(hostsList).isEmpty();

        assert ToVOConverter.toHostVO(null) == null;

        Host host = new Host("aaaa");
        HostVO hostVO = ToVOConverter.toHostVO(host);

        assert hostVO.getName().equals("aaaa");
        assert hostVO.getAddresses().isEmpty();
        assert hostVO.isShared() == false;
        assert hostVO.getObjId() == null;
        assert hostVO.getCreated().equals(host.getCreated());
        assert hostVO.getCreatedBy() == null;
        assert hostVO.getModified() == null;
        assert hostVO.getModifiedBy() == null;
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            groups = {"failure", "facade-system", "ToVOConverter"})
    public void testAddressConverter1() {
        ToVOConverter.toAddressVO(null, null);
    }

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testAddressConverter2() {
        List<Address> addressList = null;
        assert ToVOConverter.toAddressVOList(addressList) == null;
        addressList = new ArrayList<Address>();
        assert ToVOConverter.toAddressVOList(addressList).isEmpty();

        assert ToVOConverter.toAddressVO(null) == null;

        Address address = new Address();
        AddressVO addressVO = ToVOConverter.toAddressVO(address);

        assert addressVO.getCity() == null;
        assert addressVO.getCountryCode() == null;
        assert addressVO.getPostalCode() == null;
        assert addressVO.getState() == null;
        assert addressVO.getStreet() == null;
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            groups = {"failure", "facade-system", "ToVOConverter"})
    public void testContactConverter1() {
        ToVOConverter.toContactVO(null, null);
    }

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testContactConverter2() {
        List<Contact> contactList = null;
        assert ToVOConverter.toContactVOList(contactList) == null;
        contactList = new ArrayList<Contact>();
        assert ToVOConverter.toContactVOList(contactList).isEmpty();

        assert ToVOConverter.toContactVO(null) == null;

        Contact contact = new Contact();
        ContactVO contactVO = ToVOConverter.toContactVO(contact);

        assert contactVO.getName().equals("");
        assert contactVO.getAddresses().isEmpty();
        assert contactVO.getPhoneNumbers().isEmpty();
        assert contactVO.getFaxNumbers().isEmpty();
        assert contactVO.getEmails().isEmpty();
        assert contactVO.isRole() == false;
        assert contactVO.getObjId() == null;
        assert contactVO.getCreated().equals(contact.getCreated());
        assert contactVO.getCreatedBy() == null;
        assert contactVO.getModified() == null;
        assert contactVO.getModifiedBy() == null;
    }

    @Test (expectedExceptions = {IllegalArgumentException.class},
            groups = {"failure", "facade-system", "ToVOConverter"})
    public void testDomainConverter1() {
        ToVOConverter.toDomainVO(null, null);
    }

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testDomainConverter2() {
        assert ToVOConverter.toDomainVO(null) == null;

        Domain domain = new Domain("domain.org");
        DomainVO domainVO = ToVOConverter.toDomainVO(domain);

        assert domainVO.getName().equals("domain.org");
        assert domainVO.getSupportingOrg() == null;
        assert domainVO.getAdminContacts().isEmpty();
        assert domainVO.getTechContacts().isEmpty();
        assert domainVO.getNameServers().isEmpty();
        assert domainVO.getRegistryUrl() == null;
        assert domainVO.getWhoisServer() == null;
        assert domainVO.getBreakpoints().isEmpty();
        assert domainVO.getSpecialInstructions() == null;
        assert domainVO.getStatus().equals(DomainVO.Status.NEW);
        assert domainVO.getState().equals(DomainVO.State.NO_ACTIVITY);
        assert domainVO.getRoles() == null;
        assert domainVO.getObjId() == null;
        assert domainVO.getCreated().equals(domain.getCreated());
        assert domainVO.getCreatedBy() == null;
        assert domainVO.getModified() == null;
        assert domainVO.getModifiedBy() == null;
    }
}
