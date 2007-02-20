package org.iana.rzm.domain;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;

import java.util.*;

public class Host extends TrackedObject {

    private Name name;
    private Set<IPAddress> addresses;
    private int numDelegations;

    public Host(String name) throws InvalidNameException {
        setName(name);
        this.addresses = new HashSet<IPAddress>();
        this.numDelegations = 0;
    }

    final public String getName() {
        return name == null ? null : name.getName();
    }

    final public void setName(String name) throws InvalidNameException {
        this.name = new Name(name);
    }

    final public Set<IPAddress> getAddresses() {
        return Collections.unmodifiableSet(this.addresses);
    }

    final public void setAddresses(Collection<IPAddress> addresses) {
        CheckTool.checkCollectionNull(addresses, "IP addresses");
        this.addresses.clear();
        this.addresses.addAll(addresses);
    }

    final public void addIPv4Address(IPv4Address addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
    }

    final public void addIPv4Address(String addr) throws InvalidIPAddressException {
        addIPv4Address(IPAddress.createIPv4Address(addr));
    }

    final public void addIPv6Address(IPv6Address addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
    }

    final public void addIPv6Address(String addr) throws InvalidIPAddressException {
        addIPv6Address(IPAddress.createIPv6Address(addr));
    }

    final public void addIPAddress(IPAddress addr) {
        CheckTool.checkNull(addr, "IP address");
        this.addresses.add(addr);
    }

    final public void addIPAddress(String addr) throws InvalidIPAddressException {
        CheckTool.checkNull(addr, "IP address");
        addIPAddress(IPAddress.createIPAddress(addr));
    }
    
    final void incDelegations() { ++numDelegations; }

    final void decDelegations() { --numDelegations; }

    final public boolean isShared() { return numDelegations > 1; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (name != null ? !name.equals(host.name) : host.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
 