package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.xbill.DNS.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSNameServer implements DNSHost {

    private DNSDomain domain;
    private DNSHost host;
    private Message soa;

    public DNSNameServer(DNSDomain domain, DNSHost host) {
        if (domain == null) throw new IllegalArgumentException("null domain");
        if (host == null) throw new IllegalArgumentException("null host");
        this.domain = domain;
        this.host = host;
        sendSOAQuery();
    }

    private void sendSOAQuery() {
        try {
            Record question = Record.newRecord(new Name(domain.getNameWithDot()), Type.SOA, DClass.IN);
            Message query = Message.newQuery(question);
            soa = new SimpleResolver(host.getName()).send(query);
        } catch (TextParseException e) {
            Logger.getLogger(DNSNameServer.class).error("parsing domain name: " + domain.getNameWithDot(), e);
        } catch (IOException e) {
            Logger.getLogger(DNSNameServer.class).error("io exception: " + host.getName(), e);
        }
    }

    public DNSDomain getDomain() {
        return domain;
    }

    public String getName() {
        return host.getName();
    }

    public String getNameWithDot() {
        return host.getNameWithDot();
    }

    public Set<DNSIPAddress> getIPAddresses() {
        return host.getIPAddresses();
    }

    public Set<String> getIPAddressesAsStrings() {
        return host.getIPAddressesAsStrings();
    }

    public boolean hasIPAddress(DNSIPAddress addr) {
        return host.hasIPAddress(addr);
    }

    public boolean hasIPAddress(String addr) {
        return host.hasIPAddress(addr);
    }

    public Message getSOA() {
        return soa;
    }
}
