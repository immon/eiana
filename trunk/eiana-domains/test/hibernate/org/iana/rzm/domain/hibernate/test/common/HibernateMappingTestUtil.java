package org.iana.rzm.domain.hibernate.test.common;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateMappingTestUtil {
    public static Address setupAddress(Address address, String prefix, String countryCode) {
        address.setTextAddress(prefix + "text address");
        address.setCountryCode(countryCode);
        return address;
    }

    public static TrackData setupTrackedObject(TrackData to, String prefix, Long id) {
        to.setCreated(new Timestamp(System.currentTimeMillis()));
        to.setCreatedBy(prefix + "-creator");
        to.setModified(new Timestamp(System.currentTimeMillis()));
        to.setModifiedBy(prefix + "-modifier");
        return to;
    }

    public static Contact setupContact(Contact contact, String prefix, boolean flag, String countryCode) {
        contact.setName(prefix + " name");
        contact.setRole(flag);
        contact.addAddress(HibernateMappingTestUtil.setupAddress(new Address(), "contact", countryCode));
        contact.addEmail("jakubl@nask.pl");
        contact.addEmail("jakub.laszkiewicz@nask.pl");
        contact.addFaxNumber("+1234567890");
        contact.addFaxNumber("+1234567892");
        contact.addPhoneNumber("+1234567891");
        contact.addPhoneNumber("+1234567893");
        return contact;
    }

    public static Host setupHost(Host host) throws InvalidIPAddressException, InvalidNameException {
        return HibernateMappingTestUtil.setupHost(host, "");
    }

    public static Host setupHost(Host host, String prefix) throws InvalidIPAddressException, InvalidNameException {
        host.setName(prefix + host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv4Address("5.6.7.8"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        host.addIPAddress(IPAddress.createIPv6Address("CDEF::1234:5678"));
        return host;
    }

    public static Domain setupDomain(Domain domain) throws MalformedURLException, InvalidNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        return HibernateMappingTestUtil.setupDomain(domain, "");
    }

    public static Domain setupDomain(Domain domain, String prefix) throws MalformedURLException, InvalidNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        domain.setName(prefix + domain.getName());
        domain.setRegistryUrl("http://" + prefix + "registry.pl");
        domain.setSpecialInstructions(prefix + " special instructions");
        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.NEW);
        domain.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "supporting org", true, "US"));
        domain.setWhoisServer("whois.server.com");
        domain.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "admin1", true, "US"));
        domain.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "admin2", true, "US"));
        domain.addBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domain.addBreakpoint(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns1." + domain.getName()), prefix));
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns2." + domain.getName()), prefix));
        domain.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "tech1", true, "US"));
        domain.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "tech2", true, "US"));
        return domain;
    }
}
