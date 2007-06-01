package org.iana.dns.obj;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.validator.InvalidIPAddressException;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSHostImpl implements DNSHost {

    private Name name;
    private Set<DNSIPAddress> addresses = new HashSet<DNSIPAddress>();

    public DNSHostImpl(String name) {
        this.name = new Name(name);
    }

    public String getName() {
        return name.getName();
    }

    public String getNameWithDot() {
        return name.getNameWithDot();
    }

    public String[] getLabels() {
        return name.getLabels();
    }

    public Set<DNSIPAddress> getIPAddresses() {
        return addresses;
    }

    public Set<String> getIPAddressesAsStrings() {
        Set<String> ret = new HashSet<String>();
        for (DNSIPAddress addr : addresses) {
            ret.add(addr.getAddress());
        }
        return ret;
    }

    public void setIPAddresses(Set<DNSIPAddress> addrs) {
        addresses = new HashSet<DNSIPAddress>(addrs);
    }

    public void setIPAddressesAsStrings(Set<String> addrs) throws InvalidIPAddressException {
        addresses = new HashSet<DNSIPAddress>();
        for (String addr : addrs) {
            addIPAddress(addr);
        }
    }

    public void addIPAddress(String addr) throws InvalidIPAddressException {
        addresses.add(DNSIPAddressImpl.createIPAddress(addr));
    }

    public void addIPAddress(DNSIPAddress addr) throws InvalidIPAddressException {
        addresses.add(addr);
    }

    public boolean hasIPAddress(DNSIPAddress addr) {
        return addresses.contains(addr);
    }

    public boolean hasIPAddress(String addr) throws InvalidIPAddressException {
        return hasIPAddress(DNSIPAddressImpl.createIPAddress(addr));
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNSHostImpl dnsHost = (DNSHostImpl) o;

        if (name != null ? !name.equals(dnsHost.name) : dnsHost.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
