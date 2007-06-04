package org.iana.rzm.system.accuracy;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.common.Name;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"accuracy", "facade-system", "FromVOConverter"})
public class FromVOConverterAccuracyTest {
    List<ContactVO> contactVOList;
    AddressVO addressVO;
    List<HostVO> hostVOList;
    String email;
    String phone;
    String fax;
    Set<IDomainVO.Breakpoint> breakpointVOSet;
    Set<IPAddressVO> ipAddressVOSet;

    ContactVO contactVO;

    Timestamp created = new Timestamp(System.currentTimeMillis());
    Timestamp modified = new Timestamp(System.currentTimeMillis());

    @BeforeClass
    public void init() {
        contactVOList = new ArrayList<ContactVO>();
        addressVO = new AddressVO();
        hostVOList = new ArrayList<HostVO>();
        breakpointVOSet = new HashSet<IDomainVO.Breakpoint>();
        ipAddressVOSet = new HashSet<IPAddressVO>();

        created = new Timestamp(System.currentTimeMillis());
        modified = new Timestamp(System.currentTimeMillis());
    }

    private boolean assertAddress(Address address) {
        assert address.getTextAddress().equals("1st Marszalkowska Str., 00-009 Warsaw, Mazovia");
        assert address.getCountryCode().equals("PL");
        return true;
    }

    @Test
    public void testAddressConversion() {
        AddressVO addressVO = new AddressVO();
        addressVO.setTextAddress("1st Marszalkowska Str., 00-009 Warsaw, Mazovia");
        addressVO.setCountryCode("PL");

        Address address = FromVOConverter.toAddress(addressVO);
        assert assertAddress(address);

        this.addressVO = addressVO;
    }

    private boolean assertContact(Contact contact) {
        assert contact.getName().equals("newContact");
        assert contact.getOrganization().equals("some_org");

        assertAddress(contact.getAddress());

        assert contact.getJobTitle().equals("jobtitle");
        assert contact.getEmail().equals("public@mail.com");
        assert contact.getPrivateEmail().equals("private@mail.com");
        assert contact.getFaxNumber().equals("fax1");
        assert contact.getAltFaxNumber().equals("altfax2");
        assert contact.getPhoneNumber().equals("phone1");
        assert contact.getAltPhoneNumber().equals("altphone2");
        assert !contact.isRole();
        assert !contact.getRole();

        assert contact.getCreated().equals(created);
        assert contact.getCreatedBy().equals("somebody");
        assert contact.getModified().equals(modified);
        assert contact.getModifiedBy().equals("anybody");
        return true;
    }

    @Test(dependsOnMethods = {"testAddressConversion"})
    public void testContactConversion() {
        contactVO = new ContactVO();
        contactVO.setName("newContact");
        contactVO.setOrganization("some_org");
        contactVO.setAddress(addressVO);

        contactVO.setJobTitle("jobtitle");
        contactVO.setEmail("public@mail.com");
        contactVO.setPrivateEmail("private@mail.com");
        contactVO.setFaxNumber("fax1");
        contactVO.setAltFaxNumber("altfax2");
        contactVO.setPhoneNumber("phone1");
        contactVO.setAltPhoneNumber("altphone2");
        contactVO.setRole(false);

        contactVO.setCreated(created);
        contactVO.setCreatedBy("somebody");
        contactVO.setModified(modified);
        contactVO.setModifiedBy("anybody");

        Contact contact = FromVOConverter.toContact(contactVO);
        assert assertContact(contact);

        contactVO.equals(ToVOConverter.toContactVO(contact));

        contactVOList.add(contactVO);
    }

    private boolean assertBreakpointSet(Set<Domain.Breakpoint> breakpointSet) {
        assert breakpointSet.size() == 1;
        for (Domain.Breakpoint breakpoint : breakpointSet)
            assert breakpoint.equals(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        return true;
    }

    @Test
    public void testBreakpointConversion() {
        breakpointVOSet.add(IDomainVO.Breakpoint.AC_CHANGE_EXT_REVIEW);

        Set<Domain.Breakpoint> breakpointSet = FromVOConverter.toBreakpointSet(breakpointVOSet);
        assert assertBreakpointSet(breakpointSet);
    }

    private boolean assertIPAddress(IPAddress ipAddress) {
        assert ipAddress.isIPv4();
        assert ipAddress.getAddress().equals("10.0.0.1");
        return true;
    }

    @Test
    public void testIPAddressConversion() throws InvalidIPAddressException {
        IPAddressVO ipAddressVO = new IPAddressVO();
        ipAddressVO.setAddress("10.0.0.1");
        ipAddressVO.setType(IPAddressVO.Type.IPv4);

        IPAddress ipAddress = FromVOConverter.toIPAddress(ipAddressVO);
        assert assertIPAddress(ipAddress);

        ipAddressVOSet.add(ipAddressVO);
    }

    private boolean assertHost(Host host) {
        assert host.getName().equals("samplehost");
        assert host.getObjId() == 1L;
        assert !host.isShared();
        Set<IPAddress> ipAddressSet = host.getAddresses();
        assert ipAddressSet.size() == 1;
        for (IPAddress ipAddress : ipAddressSet)
            assert assertIPAddress(ipAddress);

        assert host.getCreated().equals(created);
        assert host.getCreatedBy().equals("somebody");
        assert host.getModified().equals(modified);
        assert host.getModifiedBy().equals("anybody");
        return true;
    }

    @Test(dependsOnMethods = {"testIPAddressConversion"})
    public void testHostConversion() {
        HostVO hostVO = new HostVO();
        hostVO.setName("samplehost");
        hostVO.setObjId(1L);
        hostVO.setShared(false);
        hostVO.setAddresses(ipAddressVOSet);

        hostVO.setCreated(created);
        hostVO.setCreatedBy("somebody");
        hostVO.setModified(modified);
        hostVO.setModifiedBy("anybody");

        Host host = FromVOConverter.toHost(hostVO);
        assert assertHost(host);

        hostVOList.add(hostVO);
    }

    @Test
    public void testStateConversion() throws MalformedURLException {
        Domain.State state;
        state = FromVOConverter.toState(IDomainVO.State.NO_ACTIVITY);
        assert state.equals(Domain.State.NO_ACTIVITY);
        state = FromVOConverter.toState(IDomainVO.State.OPERATIONS_PENDING);
        assert state.equals(Domain.State.OPERATIONS_PENDING);
        state = FromVOConverter.toState(IDomainVO.State.THIRD_PARTY_PENDING);
        assert state.equals(Domain.State.THIRD_PARTY_PENDING);
    }

    @Test
    public void testStatusConversion() throws MalformedURLException {
        Domain.Status status;
        status = FromVOConverter.toStatus(IDomainVO.Status.ACTIVE);
        assert status.equals(Domain.Status.ACTIVE);
        status = FromVOConverter.toStatus(IDomainVO.Status.CLOSED);
        assert status.equals(Domain.Status.CLOSED);
        status = FromVOConverter.toStatus(IDomainVO.Status.NEW);
        assert status.equals(Domain.Status.NEW);
    }

    @Test(dependsOnMethods = {"testContactConversion", "testBreakpointConversion", "testHostConversion",
            "testStateConversion", "testStatusConversion"})
    public void testDomainConversion() throws MalformedURLException {
        DomainVO domainVO = new DomainVO();
        domainVO.setName("domain.org");
        domainVO.setObjId(1L);
        domainVO.setAdminContact(contactVO);
        domainVO.setBreakpoints(breakpointVOSet);
        domainVO.setNameServers(hostVOList);
        domainVO.setRegistryUrl("http://somepage.com/someFile.txt");
        domainVO.setSpecialInstructions("Super secret special instruction Alpha One");
        domainVO.setState(IDomainVO.State.NO_ACTIVITY);
        domainVO.setStatus(IDomainVO.Status.NEW);
        domainVO.setSupportingOrg(contactVO);
        domainVO.setTechContact(contactVO);
        domainVO.setWhoisServer(new Name("whois"));

        domainVO.setCreated(created);
        domainVO.setCreatedBy("somebody");
        domainVO.setModified(modified);
        domainVO.setModifiedBy("anybody");

        Domain domain = FromVOConverter.toDomain(domainVO);

        assert domain != null;
        assert domain.getObjId() == 1L;
        Contact adminContact = domain.getAdminContact();
        assert assertContact(adminContact);
        Set<Domain.Breakpoint> breakpointSet = domain.getBreakpoints();
        assert assertBreakpointSet(breakpointSet);
        List<Host> hostList = domain.getNameServers();
        for (Host host : hostList)
            assert assertHost(host);

        assert domain.getRegistryUrl().equals(domainVO.getRegistryUrl());
        assert domain.getSpecialInstructions().equals("Super secret special instruction Alpha One");
        assert domain.getState().equals(Domain.State.NO_ACTIVITY);
        assert domain.getStatus().equals(Domain.Status.NEW);
        assert assertContact(domain.getSupportingOrg());
        Contact techContact = domain.getTechContact();
        assert assertContact(techContact);
        assert domain.getWhoisServer().equals("whois");

        assert domain.getName().equals("domain.org");
        assert domain.getCreated().equals(created);
        assert domain.getCreatedBy().equals("somebody");
        assert domain.getModified().equals(modified);
        assert domain.getModifiedBy().equals("anybody");
    }
}
