package org.iana.dns.obj;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSVisitor;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSDomainImpl implements DNSDomain {

    private Name name;

    private Map<String, DNSHost> nameServers = new HashMap<String, DNSHost>();

    public DNSDomainImpl(String name) {
        this.name = new Name(name);
    }

    public String getName() {
        return name.getName();
    }

    public String getFullyQualifiedName() {
        return name.getNameWithDot();
    }

    public String getNameAsFullyQualifiedSuffix() {
        return name.getNameWithDots();
    }

    public String[] getLabels() {
        return name.getLabels();
    }

    public void addNameServer(DNSHost host) {
        nameServers.put(host.getName(), host);
    }

    public void setNameServers(Set<DNSHost> hosts) {
        for (DNSHost host : hosts)
            addNameServer(host);
    }

    public Set<DNSHost> getNameServers() {
        return new TreeSet<DNSHost>(nameServers.values());
    }

    public Map<String, DNSHost> getNameServerMap() {
        return nameServers;
    }

    public Set<String> getNameServerNames() {
        return nameServers.keySet();
    }

    public DNSHost getNameServer(String name) {
        if (name != null) name = name.toLowerCase();
        return nameServers.get(name);
    }

    public void accept(DNSVisitor visitor) {
        visitor.visitDomain(this);
    }

    public int compareTo(DNSDomain o) {
        return getName().compareTo(o.getName());
    }
}
