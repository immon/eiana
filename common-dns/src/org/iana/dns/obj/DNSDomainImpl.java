package org.iana.dns.obj;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

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
