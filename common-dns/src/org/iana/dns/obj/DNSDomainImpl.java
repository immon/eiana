package org.iana.dns.obj;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public String getNameWithDot() {
        return name.getNameWithDot();
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
        return new HashSet<DNSHost>(nameServers.values());
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
}
