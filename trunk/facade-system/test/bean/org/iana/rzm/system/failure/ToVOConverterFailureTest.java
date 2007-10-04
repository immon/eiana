/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.system.failure;

import org.iana.rzm.domain.*;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.AddressVO;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.HostVO;
import org.iana.rzm.facade.system.domain.vo.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"failure", "facade-system", "ToVOConverter"})
public class ToVOConverterFailureTest {

    @Test
    public void testIPConverter() throws IllegalArgumentException {
        IPAddress fromIPAddress = null;

        assert DomainToVOConverter.toIPAddressVO(fromIPAddress) == null;

        Set<IPAddress> IPAddressesSet = null;
        assert DomainToVOConverter.toIPAddressVOSet(IPAddressesSet) == null;

        IPAddressesSet = new HashSet<IPAddress>();
        assert DomainToVOConverter.toIPAddressVOSet(IPAddressesSet).isEmpty();
    }

    @Test
    public void testHostConverter() {
        List<Host> hostsList = null;
        assert DomainToVOConverter.toHostVOList(hostsList) == null;
        hostsList = new ArrayList<Host>();
        assert DomainToVOConverter.toHostVOList(hostsList).isEmpty();

        assert DomainToVOConverter.toHostVO(null) == null;

        Host host = new Host("aaaa");
        HostVO hostVO = DomainToVOConverter.toHostVO(host);

        assert hostVO.getName().equals("aaaa");
        assert hostVO.getAddresses().isEmpty();
        assert hostVO.isShared() == false;
        assert hostVO.getObjId() == null;
        assert hostVO.getCreated().equals(host.getCreated());
        assert hostVO.getCreatedBy() == null;
        assert hostVO.getModified() == null;
        assert hostVO.getModifiedBy() == null;
    }

    @Test
    public void testAddressConverter() {
        List<Address> addressList = null;
        assert DomainToVOConverter.toAddressVOList(addressList) == null;
        addressList = new ArrayList<Address>();
        assert DomainToVOConverter.toAddressVOList(addressList).isEmpty();

        assert DomainToVOConverter.toAddressVO(null) == null;

        Address address = new Address("text address", "US");
        AddressVO addressVO = DomainToVOConverter.toAddressVO(address);

        assert "text address".equals(addressVO.getTextAddress());
        assert "US".equals(addressVO.getCountryCode());
    }

    @Test
    public void testContactConverter() {
        List<Contact> contactList = null;
        assert DomainToVOConverter.toContactVOList(contactList) == null;
        contactList = new ArrayList<Contact>();
        assert DomainToVOConverter.toContactVOList(contactList).isEmpty();

        assert DomainToVOConverter.toContactVO(null) == null;

        Contact contact = new Contact();
        ContactVO contactVO = DomainToVOConverter.toContactVO(contact);

        assert contactVO.getName().equals("");
        assert contactVO.getAddress() == null;
        assert contactVO.getPhoneNumber() == null;
        assert contactVO.getFaxNumber() == null;
        assert contactVO.getEmail() == null;
        assert contactVO.isRole() == false;
        assert contactVO.getObjId() == 0L;
        assert contactVO.getCreated().equals(contact.getCreated());
        assert contactVO.getCreatedBy() == null;
        assert contactVO.getModified() == null;
        assert contactVO.getModifiedBy() == null;
    }

    @Test
    public void testDomainConverter() {
        assert DomainToVOConverter.toDomainVO(null) == null;

        Domain domain = new Domain("domain.org");
        DomainVO domainVO = DomainToVOConverter.toDomainVO(domain);

        assert domainVO.getName().equals("domain.org");
        assert domainVO.getSupportingOrg() == null;
        assert domainVO.getAdminContact() == null;
        assert domainVO.getTechContact() == null;
        assert domainVO.getNameServers().isEmpty();
        assert domainVO.getRegistryUrl() == null;
        assert domainVO.getWhoisServer() == null;
        assert domainVO.getBreakpoints().isEmpty();
        assert domainVO.getSpecialInstructions() == null;
        assert domainVO.getStatus().equals(DomainVO.Status.ACTIVE);
        assert domainVO.getState().equals(DomainVO.State.NO_ACTIVITY);
        assert domainVO.getRoles() == null;
        assert domainVO.getObjId() == null;
        assert domainVO.getCreated().equals(domain.getCreated());
        assert domainVO.getCreatedBy() == null;
        assert domainVO.getModified() == null;
        assert domainVO.getModifiedBy() == null;
    }
}
