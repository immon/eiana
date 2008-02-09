package org.iana.dns.check;

import org.apache.log4j.*;
import org.iana.dns.*;
import org.iana.dns.check.exceptions.*;
import org.xbill.DNS.*;

import java.io.*;
import java.net.*;
import java.util.*;

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

    public Record[] getRecords() throws DNSCheckIOException {
        List<Record> records = new ArrayList<Record>();
        records.addAll(getRecords(Type.A));
        records.addAll(getRecords(Type.AAAA));
        return records.toArray(new Record[0]);
    }

    private List<Record> getRecords(int type) throws DNSCheckIOException {
        try {
            Lookup lookup = new Lookup(new Name(host.getNameWithDot()), type);
            Record[] records = lookup.run();
            return (records == null) ? new ArrayList<Record>() : Arrays.asList(records);
        } catch (IOException e) {
            String output = "io exception: " + host.getName();
            Logger.getLogger(DNSNameServer.class).error(output, e);
            throw new DNSCheckIOException(output);
        }
    }

    public Record[] getNsRecord() throws DNSCheckIOException, EmptyIPAddressListException {

        try {
            Record question = Record.newRecord(new Name(domain.getNameWithDot()), Type.NS, DClass.IN);
            Message query = Message.newQuery(question);
            Set<String> ips = host.getIPAddressesAsStrings();
            if (ips.isEmpty()) throw new EmptyIPAddressListException(domain, host);
            InetAddress address = Address.getByAddress(ips.iterator().next());
            SimpleResolver resolver = new SimpleResolver();
            resolver.setAddress(address);
            resolver.setTCP(true);
            Message message = resolver.send(query);
            return message.getSectionArray(Section.ANSWER);
        } catch (IOException e) {
            String output = "io exception: " + host.getName();
            Logger.getLogger(DNSNameServer.class).error(output, e);
            throw new DNSCheckIOException(output);
        }
    }


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

        public Message getMessage() throws DNSCheckIOException {
            if (!initialized) {
                initialized = true;
                message = sendSOAQuery(tcp);
            }
            return message;
        }

        private Message sendSOAQuery(boolean byTCP) throws DNSCheckIOException {
            try {
                Record question = Record.newRecord(new Name(domain.getNameWithDot()), Type.SOA, DClass.IN);
                Message query = Message.newQuery(question);
                Resolver resolver = new SimpleResolver(host.getName());
                resolver.setTCP(byTCP);
                return resolver.send(query);
            } catch (IOException e) {
                String output = "io exception: " + host.getName();
                Logger.getLogger(DNSNameServer.class).error(output, e);
                throw new DNSCheckIOException(output);
            }
        }
    }

    public DNSNameServer(DNSDomain domain, DNSHost host) {
        if (domain == null) {
            throw new IllegalArgumentException("null domain");
        }
        if (host == null) {
            throw new IllegalArgumentException("null host");
        }
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

    public boolean isAuthoritative() throws DNSCheckIOException {
        return ((getSOA() != null) && (getSOA().getHeader().getFlag(Flags.AA)));
    }

    public List<Record> getAuthoritySection() throws DNSCheckIOException {
        return (getSOA() != null) ?
                Arrays.asList(getSOA().getSectionArray(Section.AUTHORITY)) :
                new ArrayList<Record>();
    }

    public List<Record> getAdditionalSection() throws DNSCheckIOException {
        return (getSOA() != null) ?
                Arrays.asList(getSOA().getSectionArray(Section.ADDITIONAL)) :
                new ArrayList<Record>();
    }


    public long getSerialNumber() throws DNSCheckIOException {
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

    public Message getSOA() throws DNSCheckIOException {
        return (getSOAByUDP() != null) ? getSOAByUDP() : getSOAByTCP();
    }

    public Message getSOAByUDP() throws DNSCheckIOException {
        return soaByUDP.getMessage();
    }

    public Message getSOAByTCP() throws DNSCheckIOException {
        return soaByTCP.getMessage();
    }
}
