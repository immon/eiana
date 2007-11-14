package org.iana.dns.check;

import org.apache.log4j.Logger;
import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;
import org.xbill.DNS.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A helper class that represents a host which is a name server for a domain. During
 * construction DNS is queried to obtain SOA record configured for a given domain on a given name server. 
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class DNSNameServer {

    private DNSDomain domain;
    private DNSHost host;
    private DNSSOA soaByUDP;
    private DNSSOA soaByTCP;

    private class DNSSOA {

        private Message message;
        private boolean tcp;
        private boolean initialized;

        public DNSSOA(boolean tcp) {
            this.tcp = tcp;
            this.initialized = false;
        }

        public DNSSOA(Message message, boolean tcp) {
            this.message = message;
            this.tcp = tcp;
            this.initialized = true;
        }

        public Message getMessage() {
            if (!initialized) {
                message = sendSOAQuery(tcp);
                initialized = true;
            }
            return message;
        }

        private Message sendSOAQuery(boolean byTCP) {
            try {
                Record question = Record.newRecord(new Name(domain.getNameWithDot()), Type.SOA, DClass.IN);
                Message query = Message.newQuery(question);
                Resolver resolver = new SimpleResolver(host.getName());
                resolver.setTCP(byTCP);
                return resolver.send(query);
            } catch (TextParseException e) {
                Logger.getLogger(DNSNameServer.class).error("parsing domain name: " + domain.getNameWithDot(), e);
            } catch (IOException e) {
                Logger.getLogger(DNSNameServer.class).error("io exception: " + host.getName(), e);
            }
            return null;
        }

    }

    public DNSNameServer(DNSDomain domain, DNSHost host) {
        if (domain == null) throw new IllegalArgumentException("null domain");
        if (host == null) throw new IllegalArgumentException("null host");
        this.domain = domain;
        this.host = host;
        this.soaByUDP = new DNSSOA(false);
        this.soaByTCP = new DNSSOA(true);
    }

    public DNSNameServer(DNSDomain domain, DNSHost host, Message soaByUDP, Message soaByTCP) {
        this.domain = domain;
        this.host = host;
        this.soaByUDP = new DNSSOA(soaByUDP, false);
        this.soaByTCP = new DNSSOA(soaByTCP, true);
    }

    public boolean isReachableByUDP() {
        return soaByUDP != null;
    }

    public boolean isReachableByTCP() {
        return soaByTCP != null;
    }

    public boolean isAuthoritative() {
        return ((getSOA() != null) && (getSOA().getHeader().getFlag(Flags.AA)));
    }

    public List<Record> getAuthoritySection() {
        return (getSOA() != null) ? Arrays.asList(getSOA().getSectionArray(2)) : new ArrayList<Record>();
    }

    public List<Record> getAdditionalSection() {
        return (getSOA() != null) ? Arrays.asList(getSOA().getSectionArray(3)) : new ArrayList<Record>();
    }

    public long getSerialNumber() {
        return (getSOA() != null) ? ((SOARecord) getSOA().getSectionArray(1)[0]).getSerial() : -1;
    }

    public DNSDomain getDomain() {
        return domain;
    }

    public DNSHost getHost() {
        return host;
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
        return (getSOAByUDP() != null) ? getSOAByUDP() : getSOAByTCP();
    }

    public Message getSOAByUDP() {
        return soaByUDP.getMessage();
    }

    public Message getSOAByTCP() {
        return soaByTCP.getMessage();
    }
}
