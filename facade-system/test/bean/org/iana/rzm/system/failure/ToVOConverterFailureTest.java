/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.system.failure;

import org.iana.rzm.domain.*;
import org.iana.rzm.facade.system.domain.HostVO;
import org.iana.rzm.facade.system.domain.AddressVO;
import org.iana.rzm.facade.system.domain.ContactVO;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"ToVOConverter", "facade-system"})
public class ToVOConverterFailureTest {

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testIPConverter() throws IllegalArgumentException {
        IPAddress fromIPAddress = null;

        assert ToVOConverter.toIPAddressVO(fromIPAddress) == null;

        Set<IPAddress> IPAddressesSet = null;
        assert ToVOConverter.toIPAddressVOSet(IPAddressesSet) == null;

        IPAddressesSet = new HashSet<IPAddress>();
        assert ToVOConverter.toIPAddressVOSet(IPAddressesSet).isEmpty();
    }

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testHostConverter() {
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

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testAddressConverter() {
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

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testContactConverter() {
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

    @Test (groups = {"failure", "facade-system", "ToVOConverter"})
    public void testDomainConverter() {
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
