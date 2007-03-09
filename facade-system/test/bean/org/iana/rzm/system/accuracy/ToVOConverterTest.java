package org.iana.rzm.system.accuracy;

import org.testng.annotations.Test;
import org.iana.rzm.domain.*;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.facade.system.ToVOConverter;
import org.iana.rzm.facade.system.IPAddressVO;
import org.iana.rzm.facade.system.HostVO;
import org.iana.rzm.facade.system.AddressVO;

import java.util.Set;
import java.util.HashSet;
import java.sql.Timestamp;


/**
 * @author Piotr Tkaczyk
 */

@Test(groups = {"ToVOConverter", "facade-system"})
public class ToVOConverterTest {

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"})
    public void testIPv4AddressConversion() throws InvalidIPAddressException {
        IPAddress fromIPAddress = IPAddressFactory.getIPv4Address("10.0.0.1");
        IPAddressVO toIPAddressVO = ToVOConverter.toIPAddressVO(fromIPAddress);
        assert toIPAddressVO.getType() == IPAddressVO.Type.IPv4;
        assert fromIPAddress.getAddress().equals(toIPAddressVO.getAddress());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"})
    public void testIPv6AddressConversion() throws InvalidIPAddressException {
        IPAddress fromIPAddress = IPAddressFactory.getIPv6Address("10.0.0.1");
        IPAddressVO toIPAddressVO = ToVOConverter.toIPAddressVO(fromIPAddress);
        assert toIPAddressVO.getType() == IPAddressVO.Type.IPv6;
        assert fromIPAddress.getAddress().equals(toIPAddressVO.getAddress());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"},
           dependsOnMethods = {"testIPv4AddressConversion", "testIPv6AddressConversion"})
    public void testHostConversion() throws InvalidIPAddressException, InvalidNameException {
        Host fromHost = new Host("testHost");

        Set<IPAddress> ipAddresses = new HashSet<IPAddress>();
        ipAddresses.add(IPAddressFactory.getIPv4Address("10.0.0.1"));
        ipAddresses.add(IPAddressFactory.getIPv6Address("10.0.0.1"));

        fromHost.setAddresses(ipAddresses);

        TrackData trackData = new TrackData();
        trackData.setCreatedBy("user1");
        trackData.setModified(new Timestamp(System.currentTimeMillis()));
        trackData.setModifiedBy("user2");

        fromHost.setTrackData(trackData);
        fromHost.setObjId(1L);
        
        HostVO toHostVO = ToVOConverter.toHostVO(fromHost);
        assert fromHost.getName().equals(toHostVO.getName());
        assert fromHost.getModified() == toHostVO.getModified();
        assert fromHost.getModifiedBy().equals(toHostVO.getModifiedBy());
        assert fromHost.getCreated() == toHostVO.getCreated();
        assert fromHost.getCreatedBy().equals(toHostVO.getCreatedBy());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"})
    public void testAddressConversion() {
        Address address = new Address();
        address.setCity("LosAngeles");
        address.setCountryCode("US");
        address.setObjId(1L);
        address.setPostalCode("ZIP-999");
        address.setState("California");
        address.setStreet("Sun Set avenue");

        AddressVO toAddressVO = ToVOConverter.toAddressVO(address);

        assert address.getCity().equals(toAddressVO.getCity());
        assert address.getCountryCode().equals(toAddressVO.getCountryCode());
        assert address.getPostalCode().equals(toAddressVO.getPostalCode());
        assert address.getState().equals(toAddressVO.getState());
        assert address.getStreet().equals(toAddressVO.getStreet());
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"},
           dependsOnMethods = {"testAddressConversion"})
    public void testContactConversion() {
        //todo
    }

    @Test (groups = {"accuracy", "facade-system", "ToVOConverter"},
           dependsOnMethods = {"testContactConversion"})
    public void testDomainConversion() {
        //todo 
    }
}
