package org.iana.rzm.trans.test.common.hibernate;

import org.iana.rzm.domain.*;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.trans.change.ModifiedPrimitiveValue;
import org.iana.rzm.trans.change.ObjectValue;
import org.iana.rzm.trans.change.Modification;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.StateTransition;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.Transaction;

import java.sql.Timestamp;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 */
public class HibernateMappingTestUtil {
    public static Address setupAddress(Address address, String prefix) {
        address.setCity(prefix + "city");
        address.setCountryCode(prefix + "country code");
        address.setPostalCode(prefix + "postal code");
        address.setState(prefix + "state");
        address.setStreet(prefix + "street");
        return address;
    }

    public static TrackData setupTrackedObject(TrackData to, String prefix, Long id) {
        to.setCreated(new Timestamp(System.currentTimeMillis()));
        to.setCreatedBy(prefix + "-creator");
        to.setId(id);
        to.setModified(new Timestamp(System.currentTimeMillis()));
        to.setModifiedBy(prefix + "-modifier");
        return to;
    }

    public static Contact setupContact(Contact contact, String prefix, boolean flag) {
        contact.setName(prefix + " name");
        contact.setRole(flag);
        contact.addAddress(HibernateMappingTestUtil.setupAddress(new Address(), "contact"));
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
        domain.setRegistryUrl(new URL("http://" + prefix + "registry.pl"));
        domain.setSpecialInstructions(prefix + " special instructions");
        domain.setState(Domain.State.NO_ACTIVITY);
        domain.setStatus(Domain.Status.NEW);
        domain.setSupportingOrg(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "supporting org", true));
        domain.setWhoisServer("whois.server.com");
        domain.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "admin1", true));
        domain.addAdminContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "admin2", true));
        domain.addBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW);
        domain.addBreakpoint(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW);
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns1." + domain.getName()), prefix));
        domain.addNameServer(HibernateMappingTestUtil.setupHost(new Host("ns2." + domain.getName()), prefix));
        domain.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "tech1", true));
        domain.addTechContact(HibernateMappingTestUtil.setupContact(new Contact(), prefix + "tech2", true));
        return domain;
    }

    public static ModifiedPrimitiveValue setupMPV (ModifiedPrimitiveValue mpv, String prefix) {
        mpv.setOldValue(prefix + " old value");
        mpv.setNewValue(prefix + " new value");
        return mpv;
    }

    public static ObjectValue setupObjectValue(ObjectValue ov, String prefix) {
        ov.setId(System.currentTimeMillis());
        ov.setName(prefix + "-name");
        return ov;
    }

    public static Modification createPrimitiveModification(String prefix) {
        return new Modification(prefix + "modification",
                HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), prefix));
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

    public static TransactionAction setupAction(TransactionAction action, TransactionAction.Name name, List<Change> change) {
        action.setName(name);
        action.setChange(change);
        return action;
    }

    public static TransactionAction createAction(TransactionAction.Name name) {
        List<Change> change = new ArrayList<Change>();
        change.add(new Modification("1st modification",
                HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "created")));
        change.add(new Modification("2nd modification",
                HibernateMappingTestUtil.setupMPV(new ModifiedPrimitiveValue(), "created")));
        return HibernateMappingTestUtil.setupAction(new TransactionAction(), name, change);
    }

    public static Transaction setupTransaction(Transaction trans, String prefix, List<TransactionAction> actions, Domain domain, TransactionState state) {
        trans.setActions(actions);
        trans.setCurrentDomain(domain);
        trans.setName(prefix + " transaction");
        trans.setRtID(System.currentTimeMillis());
        trans.setStart(new Timestamp(System.currentTimeMillis()));
        trans.setEnd(new Timestamp(System.currentTimeMillis() + 1000L));
        trans.setState(state);
        trans.setTransactionID(System.currentTimeMillis());
        return trans;
    }
}
