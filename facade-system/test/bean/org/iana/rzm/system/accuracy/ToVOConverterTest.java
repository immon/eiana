package org.iana.rzm.system.accuracy;

import org.testng.annotations.Test;
import org.iana.rzm.domain.*;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.facade.system.*;

import java.util.Set;
import java.util.HashSet;
import java.sql.Timestamp;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"ToVOConverter", "facade-system"})
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

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"})
    public void testIPv4AddressConversion() throws InvalidIPAddressException {
        fromIPAddress = IPAddressFactory.getIPv4Address("10.0.0.1");
        toIPAddressVO = ToVOConverter.toIPAddressVO(fromIPAddress);
        assert toIPAddressVO.getType() == IPAddressVO.Type.IPv4;
        assert fromIPAddress.getAddress().equals(toIPAddressVO.getAddress());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"})
    public void testIPv6AddressConversion() throws InvalidIPAddressException {
        fromIPAddressV6 = IPAddressFactory.getIPv6Address("200c:0db8:0000:0000:0000:0000:1428:57ab");
        toIPAddressVOV6 = ToVOConverter.toIPAddressVO(fromIPAddressV6);
        assert toIPAddressVOV6.getType() == IPAddressVO.Type.IPv6;
        assert fromIPAddressV6.getAddress().equals(toIPAddressVOV6.getAddress());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"},
           dependsOnMethods = {"testIPv4AddressConversion", "testIPv6AddressConversion"})
    public void testHostConversion() throws InvalidIPAddressException, InvalidNameException {
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
        
        toHostVO = ToVOConverter.toHostVO(fromHost);
        assert fromHost.getName().equals(toHostVO.getName());
        assert fromHost.getModified() == toHostVO.getModified();
        assert fromHost.getModifiedBy().equals(toHostVO.getModifiedBy());
        assert fromHost.getCreated() == toHostVO.getCreated();
        assert fromHost.getCreatedBy().equals(toHostVO.getCreatedBy());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"})
    public void testAddressConversion() {
        fromAddress = new Address();
        fromAddress.setCity("LosAngeles");
        fromAddress.setCountryCode("US");
        fromAddress.setPostalCode("ZIP-999");
        fromAddress.setState("California");
        fromAddress.setStreet("Sun Set avenue");

        toAddressVO = ToVOConverter.toAddressVO(fromAddress);
        assert fromAddress.getCity().equals(toAddressVO.getCity());
        assert fromAddress.getCountryCode().equals(toAddressVO.getCountryCode());
        assert fromAddress.getPostalCode().equals(toAddressVO.getPostalCode());
        assert fromAddress.getState().equals(toAddressVO.getState());
        assert fromAddress.getStreet().equals(toAddressVO.getStreet());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"},
           dependsOnMethods = {"testAddressConversion"})
    public void testContactConversion() {
        fromContact = new Contact("contact1", fromAddress, "112-123-124", "212-223-542", "email@free.com", true);

        toContactVO = ToVOConverter.toContactVO(fromContact);
        assert toContactVO.getName().equals(fromContact.getName());
        assert toContactVO.getPhoneNumbers().contains("112-123-124");
        assert toContactVO.getFaxNumbers().contains("212-223-542");
        assert toContactVO.getEmails().contains("email@free.com");
        assert toContactVO.isRole();
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"},
           dependsOnMethods = {"testContactConversion", "testHostConversion"})
    public void testDomainConversion() throws InvalidNameException, NameServerAlreadyExistsException, MalformedURLException {
        fromDomain = new Domain("domain1.org");
        fromDomain.addAdminContact(fromContact);
        fromDomain.addBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        fromDomain.addBreakpoint(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        fromDomain.addNameServer(fromHost);
        fromDomain.addTechContact(fromContact);

        fromDomain.setRegistryUrl(new URL("http", "tmp.something.org", 80, "/someFile"));
        fromDomain.setSpecialInstructions("Super Secret Special Instruction Alpha One");
        fromDomain.setState(Domain.State.NO_ACTIVITY);
        fromDomain.setStatus(Domain.Status.ACTIVE);
        fromDomain.setSupportingOrg(fromContact);
        fromDomain.setWhoisServer("WhoIs");

        fromDomain.setTrackData(trackData);

        toDomainVO = ToVOConverter.toDomainVO(fromDomain);

        assert fromDomain.getName().equals(toDomainVO.getName());

        for(ContactVO adminContactVO : toDomainVO.getAdminContacts()) {
            assert adminContactVO.getName().equals(fromContact.getName());
            assert adminContactVO.getPhoneNumbers().containsAll(fromContact.getPhoneNumbers());
            assert adminContactVO.getFaxNumbers().containsAll(fromContact.getFaxNumbers());
            assert adminContactVO.getEmails().containsAll(fromContact.getEmails());
            assert adminContactVO.isRole() == fromContact.isRole();
        }

        assert toDomainVO.getBreakpoints().containsAll(ToVOConverter.toBreakpointVOSet(fromDomain.getBreakpoints()));

        toDomainVO.getNameServers().containsAll(ToVOConverter.toHostVOList(fromDomain.getNameServers()));
        toDomainVO.getTechContacts().containsAll(ToVOConverter.toContactVOList(fromDomain.getTechContacts()));
        
        assert toDomainVO.getRegistryUrl().equals(fromDomain.getRegistryUrl());
        assert toDomainVO.getSpecialInstructions().equals(fromDomain.getSpecialInstructions());
        assert toDomainVO.getState() == IDomainVO.State.NO_ACTIVITY;
        assert toDomainVO.getStatus() == IDomainVO.Status.ACTIVE;

        //assert toDomainVO.getSupportingOrg().equals(fromContact);
        // unable to assert because TrackData class fields not equals TrackDataVO 
        ContactVO tmpContactVO = toDomainVO.getSupportingOrg();
            assert tmpContactVO.getName().equals(fromContact.getName());
            assert tmpContactVO.getPhoneNumbers().containsAll(fromContact.getPhoneNumbers());
            assert tmpContactVO.getFaxNumbers().containsAll(fromContact.getFaxNumbers());
            assert tmpContactVO.getEmails().containsAll(fromContact.getEmails());
            assert tmpContactVO.isRole() == fromContact.isRole();

        assert toDomainVO.getWhoisServer().getName().equals(fromDomain.getWhoisServer());
        assert toDomainVO.getCreated().equals(fromDomain.getCreated());
        assert toDomainVO.getCreatedBy().equals(fromDomain.getCreatedBy());
        assert toDomainVO.getModified().equals(fromDomain.getModified());
        assert toDomainVO.getModifiedBy().equals(fromDomain.getModifiedBy());
    }
}
