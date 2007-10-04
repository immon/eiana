package org.iana.rzm.system.accuracy;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.domain.*;
import org.iana.rzm.facade.system.domain.converters.DomainFromVOConverter;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.user.SystemRole;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"accuracy", "facade-system", "ToVOConverter"})
public class ToVOConverterTest {
    IPAddress fromIPAddress;
    IPAddressVO toIPAddressVO;
    IPAddress fromIPAddressV6;
    IPAddressVO toIPAddressVOV6;

    Host fromHost;
    HostVO toHostVO;

    Address fromAddress;
    AddressVO toAddressVO;

    Contact fromContact;
    ContactVO toContactVO;

    Domain fromDomain;
    DomainVO toDomainVO;

    TrackData trackData;

    @Test
    public void testIPv4AddressConversion() throws InvalidIPAddressException {
        fromIPAddress = IPv4Address.createIPv4Address("10.0.0.1");
        toIPAddressVO = DomainToVOConverter.toIPAddressVO(fromIPAddress);
        assert toIPAddressVO.getType() == IPAddressVO.Type.IPv4;
        assert fromIPAddress.getAddress().equals(toIPAddressVO.getAddress());
    }

    @Test
    public void testIPv6AddressConversion() throws InvalidIPAddressException {
        fromIPAddressV6 = IPv6Address.createIPv6Address("200c:0db8:0000:0000:0000:0000:1428:57ab");
        toIPAddressVOV6 = DomainToVOConverter.toIPAddressVO(fromIPAddressV6);
        assert toIPAddressVOV6.getType() == IPAddressVO.Type.IPv6;
        assert fromIPAddressV6.getAddress().equals(toIPAddressVOV6.getAddress());
    }

    @Test
    public void testRoleTypeConversion() throws InvalidIPAddressException, InvalidDomainNameException {
        assert DomainToVOConverter.toRoleTypeVO(SystemRole.SystemType.AC) == SystemRoleVO.SystemType.AC;
        assert DomainToVOConverter.toRoleTypeVO(SystemRole.SystemType.SO) == SystemRoleVO.SystemType.SO;
        assert DomainToVOConverter.toRoleTypeVO(SystemRole.SystemType.TC) == SystemRoleVO.SystemType.TC;
    }

    @Test(dependsOnMethods = {"testIPv4AddressConversion", "testIPv6AddressConversion"})
    public void testHostConversion() throws InvalidIPAddressException, InvalidDomainNameException {
        fromHost = new Host("testHost");

        Set<IPAddress> ipAddresses = new HashSet<IPAddress>();
        ipAddresses.add(fromIPAddress);
        ipAddresses.add(fromIPAddressV6);

        fromHost.setAddresses(ipAddresses);

        trackData = new TrackData();
        trackData.setCreated(new Timestamp(System.currentTimeMillis()));
        trackData.setCreatedBy("user1");
        trackData.setModified(new Timestamp(System.currentTimeMillis()));
        trackData.setModifiedBy("user2");

        fromHost.setTrackData(trackData);

        toHostVO = DomainToVOConverter.toHostVO(fromHost);
        assert fromHost.getName().equals(toHostVO.getName());
        assert fromHost.getModified() == toHostVO.getModified();
        assert fromHost.getModifiedBy().equals(toHostVO.getModifiedBy());
        assert fromHost.getCreated() == toHostVO.getCreated();
        assert fromHost.getCreatedBy().equals(toHostVO.getCreatedBy());
    }

    @Test
    public void testAddressConversion() {
        fromAddress = new Address();
        fromAddress.setTextAddress("Sun Set avenue, LosAngeles, CA ZIP-999");
        fromAddress.setCountryCode("US");

        toAddressVO = DomainToVOConverter.toAddressVO(fromAddress);
        assert fromAddress.getTextAddress().equals(toAddressVO.getTextAddress());
        assert fromAddress.getCountryCode().equals(toAddressVO.getCountryCode());
    }

    @Test(dependsOnMethods = {"testAddressConversion"})
    public void testContactConversion() {
        fromContact = new Contact("contact1", "some_org", fromAddress, "phone123", "fax123", "email@free.com", true);
        fromContact.setAltFaxNumber("altfax");
        fromContact.setAltPhoneNumber("altphone");
        fromContact.setPrivateEmail("priv@email.com");

        toContactVO = DomainToVOConverter.toContactVO(fromContact);

        assert toContactVO.getName().equals(fromContact.getName());
        assert toContactVO.getOrganization().equals(fromContact.getOrganization());
        assert toContactVO.getPhoneNumber().equals(fromContact.getPhoneNumber());
        assert toContactVO.getFaxNumber().equals(fromContact.getFaxNumber());
        assert toContactVO.getEmail().equals(fromContact.getEmail());
        assert toContactVO.isRole();
        assert toContactVO.getAltFaxNumber().equals("altfax");
        assert toContactVO.getAltPhoneNumber().equals("altphone");
        assert toContactVO.getPrivateEmail().equals("priv@email.com");

        assert fromContact.equals(DomainFromVOConverter.toContact(toContactVO));
    }

    @Test(dependsOnMethods = {"testContactConversion", "testHostConversion"})
    public void testDomainConversion() throws InvalidDomainNameException, NameServerAlreadyExistsException, MalformedURLException {
        fromDomain = new Domain("domain1.org");
        fromDomain.setAdminContact(fromContact);
        fromDomain.addBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        fromDomain.addBreakpoint(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        fromDomain.addNameServer(fromHost);
        fromDomain.setTechContact(fromContact);

        fromDomain.setRegistryUrl("http://tmp.something.org:80/someFile");
        fromDomain.setSpecialInstructions("Super Secret Special Instruction Alpha One");
//        fromDomain.setState(Domain.State.NO_ACTIVITY);
        fromDomain.setStatus(Domain.Status.ACTIVE);
        fromDomain.setSupportingOrg(fromContact);
        fromDomain.setWhoisServer("WhoIs");

        fromDomain.setTrackData(trackData);

        toDomainVO = DomainToVOConverter.toDomainVO(fromDomain);

        assert fromDomain.getName().equals(toDomainVO.getName());

        ContactVO adminContactVO = toDomainVO.getAdminContact();
        assert adminContactVO.getName().equals(fromContact.getName());
        assert adminContactVO.getPhoneNumber().equals(fromContact.getPhoneNumber());
        assert adminContactVO.getFaxNumber().equals(fromContact.getFaxNumber());
        assert adminContactVO.getEmail().equals(fromContact.getEmail());
        assert adminContactVO.isRole() == fromContact.isRole();


        assert toDomainVO.getBreakpoints().equals(DomainToVOConverter.toBreakpointVOSet(fromDomain.getBreakpoints()));

        toDomainVO.getNameServers().equals(DomainToVOConverter.toHostVOList(fromDomain.getNameServers()));
        toDomainVO.getTechContact().equals(DomainToVOConverter.toContactVO(fromDomain.getTechContact()));

        assert toDomainVO.getRegistryUrl().equals(fromDomain.getRegistryUrl());
        assert toDomainVO.getSpecialInstructions().equals(fromDomain.getSpecialInstructions());
        assert toDomainVO.getState() == IDomainVO.State.NO_ACTIVITY;
        assert toDomainVO.getStatus() == IDomainVO.Status.ACTIVE;

        //assert toDomainVO.getSupportingOrg().equals(ToVOConverter.toContactVO(fromDomain.getSupportingOrg()));
        // unable to assert because TrackData class fields not equals TrackDataVO

        ContactVO tmpContactVO = toDomainVO.getSupportingOrg();
        assert tmpContactVO.getName().equals(fromContact.getName());
        assert tmpContactVO.getPhoneNumber().equals(fromContact.getPhoneNumber());
        assert tmpContactVO.getFaxNumber().equals(fromContact.getFaxNumber());
        assert tmpContactVO.getEmail().equals(fromContact.getEmail());
        assert tmpContactVO.isRole() == fromContact.isRole();

        assert toDomainVO.getWhoisServer().getName().equals(fromDomain.getWhoisServer());
        assert toDomainVO.getCreated().equals(fromDomain.getCreated());
        assert toDomainVO.getCreatedBy().equals(fromDomain.getCreatedBy());
        assert toDomainVO.getModified().equals(fromDomain.getModified());
        assert toDomainVO.getModifiedBy().equals(fromDomain.getModifiedBy());
    }
}
