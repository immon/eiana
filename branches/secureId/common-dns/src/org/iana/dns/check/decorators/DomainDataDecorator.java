package org.iana.dns.check.decorators;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class DomainDataDecorator {

    private DNSDomain domain;

    public DomainDataDecorator(DNSDomain domain) {
        this.domain = domain;
    }

    public String getName() {
        return domain.getName();
    }

    public List<HostDataDecorator> getHosts() {
        List<HostDataDecorator> ret = new ArrayList<HostDataDecorator>();

        for (DNSHost host : domain.getNameServers())
            ret.add(new HostDataDecorator(host));

        return ret;
    }
}
