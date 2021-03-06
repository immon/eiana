package org.iana.rzm.trans.hibernate;

import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.StateTransition;
import org.iana.rzm.trans.TransactionState;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateMappingTestUtil {
    public static Address setupAddress(Address address, String prefix) {
        address.setTextAddress(prefix + "text address");
        address.setCountryCode(prefix + "country code");
        return address;
    }

    public static TrackData setupTrackedObject(TrackData to, String prefix, Long id) {
        to.setCreated(new Timestamp(System.currentTimeMillis()));
        to.setCreatedBy(prefix + "-creator");
        to.setModified(new Timestamp(System.currentTimeMillis()));
        to.setModifiedBy(prefix + "-modifier");
        return to;
    }

    public static Contact setupContact(Contact contact, String prefix, boolean flag) {
        contact.setName(prefix + " name");
        contact.setRole(flag);
        contact.setAddress(HibernateMappingTestUtil.setupAddress(new Address(), "contact"));
        contact.setEmail("jakubl@nask.pl");
        contact.setEmail("jakub.laszkiewicz@nask.pl");
        contact.setFaxNumber("+1234567890");
        contact.setFaxNumber("+1234567892");
        contact.setPhoneNumber("+1234567891");
        contact.setPhoneNumber("+1234567893");
        return contact;
    }

    public static Host setupHost(Host host) throws InvalidIPAddressException, InvalidDomainNameException {
        return HibernateMappingTestUtil.setupHost(host, "");
    }

    public static Host setupHost(Host host, String prefix) throws InvalidIPAddressException, InvalidDomainNameException {
        host.setName(prefix + host.getName());
        host.addIPAddress(IPAddress.createIPv4Address("1.2.3.4"));
        host.addIPAddress(IPAddress.createIPv4Address("5.6.7.8"));
        host.addIPAddress(IPAddress.createIPv6Address("1234:5678::90AB"));
        host.addIPAddress(IPAddress.createIPv6Address("CDEF::1234:5678"));
        return host;
    }

    public static Domain setupDomain(Domain domain) throws MalformedURLException, InvalidDomainNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        return HibernateMappingTestUtil.setupDomain(domain, "");
    }

    public static Domain setupDomain(Domain domain, String prefix) throws MalformedURLException, InvalidDomainNameException, InvalidIPAddressException, NameServerAlreadyExistsException {
        domain.setName(prefix + domain.getName());
        domain.setRegistryUrl("http://" + prefix + "registry.pl");
        domain.setSpecialInstructions(prefix + " special instructions");
        domain.setStatus(Domain.Status.NEW);
        domain.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "supporting org", true));
        domain.setWhoisServer("whois.server.com");
        domain.setAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "admin1", true));
        domain.setAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "admin2", true));
        domain.addBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domain.addBreakpoint(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns1." + domain.getName()), prefix));
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns2." + domain.getName()), prefix));
        domain.setTechContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "tech1", true));
        domain.setTechContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "tech2", true));
        return domain;
    }

    public static TransactionState setupState(TransactionState state, TransactionState.Name name, Set<StateTransition> availTrans) {
        state.setName(name);
        state.setStart(new Timestamp(System.currentTimeMillis()));
        state.setEnd(new Timestamp(System.currentTimeMillis() + 1000L));
        state.setAvailableTransitions(availTrans);
        return state;
    }

    public static TransactionState createState(TransactionState.Name name) {
        Set<StateTransition> transitions = new HashSet<StateTransition>();
        transitions.add(new StateTransition("1st transition"));
        transitions.add(new StateTransition("2nd transition"));
        return HibernateMappingTestUtil.setupState(new TransactionState(), name, transitions);
    }

}
