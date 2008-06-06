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

    private static final int DEFAULT_NUMBER_OF_RETRIES = 3;

    private DNSDomain domain;
    private DNSHost host;
    private DNSSOA soaByUDP;
    private DNSSOA soaByTCP;
    private int retries;

    public Record[] getRecords() throws NameServerUnreachableException {
        List<Record> records = new ArrayList<Record>();
        records.addAll(getRecords(Type.A));
        records.addAll(getRecords(Type.AAAA));
        return records.toArray(new Record[0]);
    }

    private List<Record> getRecords(int type) throws NameServerUnreachableException {
        try {
            Lookup lookup = new Lookup(new Name(host.getFullyQualifiedName()), type);
            Record[] records = lookup.run();
            return (records == null) ? new ArrayList<Record>() : Arrays.asList(records);
        } catch (IOException e) {
            String output = "io exception: " + host.getName();
            Logger.getLogger(DNSNameServer.class).error(output, e);
            throw new NameServerUnreachableException(e, host);
        }
    }

    public Record[] getNsRecord() throws NameServerUnreachableException, EmptyIPAddressListException {

        try {
            Record question = Record.newRecord(new Name(domain.getFullyQualifiedName()), Type.NS, DClass.IN);
            Message query = Message.newQuery(question);
            Set<String> ips = host.getIPAddressesAsStrings();
            if (ips.isEmpty()) throw new EmptyIPAddressListException(domain, host);
            InetAddress address = Address.getByAddress(ips.iterator().next());
            SimpleResolver resolver = new SimpleResolver();
            resolver.setAddress(address);
            resolver.setTCP(false);
            resolver.setIgnoreTruncation(false);
            Resolver[] resolvers = {resolver};
            ExtendedResolver exResolver = new ExtendedResolver(resolvers);
            exResolver.setRetries(retries);
            Message message = exResolver.send(query);
            return message.getSectionArray(Section.ANSWER);
        } catch (SocketTimeoutException e) {
            String output = "Connection timed out;  could not reach " + host.getName();
            Logger.getLogger(DNSNameServer.class).error(output);
            throw new NameServerUnreachableException(e, host);
        } catch (IOException e) {
            String output = "io exception: " + host.getName();
            Logger.getLogger(DNSNameServer.class).error(output, e);
            throw new NameServerUnreachableException(e, host);
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

        public synchronized Message getMessage() throws NameServerUnreachableException {
            if (!initialized) {
                initialized = true;
                message = sendSOAQuery(tcp);
            }
            return message;
        }

        private Message sendSOAQuery(boolean byTCP) throws NameServerUnreachableException {
            try {
                Record question = Record.newRecord(new Name(domain.getFullyQualifiedName()), Type.SOA, DClass.IN);
                Message query = Message.newQuery(question);
                Resolver resolver = new SimpleResolver(host.getName());
                resolver.setTCP(byTCP);
                Resolver[] resolvers = {resolver};
                ExtendedResolver exResolver = new ExtendedResolver(resolvers);
                exResolver.setRetries(retries);
                return exResolver.send(query);
            } catch (IOException e) {
                String output = "io exception: " + host.getName();
                Logger.getLogger(DNSNameServer.class).error(output, e);
                throw new NameServerUnreachableException(e, host);
            }
        }
    }

    public DNSNameServer(DNSDomain domain, DNSHost host, int retries) {
        if (domain == null) {
            throw new IllegalArgumentException("null domain");
        }
        if (host == null) {
            throw new IllegalArgumentException("null host");
        }
        if (retries > 0)
            this.retries = retries;
        else
            this.retries = DEFAULT_NUMBER_OF_RETRIES;

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

    public boolean isAuthoritative() throws NameServerUnreachableException{
        return ((getSOAByUDP() != null) && (getSOAByUDP().getHeader().getFlag(Flags.AA)));
    }

    public List<Record> getAuthoritySection() throws NameServerUnreachableException {
        return (getSOA() != null) ?
                Arrays.asList(getSOA().getSectionArray(Section.AUTHORITY)) :
                new ArrayList<Record>();
    }

    public List<Record> getAdditionalSection() throws NameServerUnreachableException {
        return (getSOA() != null) ?
                Arrays.asList(getSOA().getSectionArray(Section.ADDITIONAL)) :
                new ArrayList<Record>();
    }


    public long getSerialNumber() throws NameServerUnreachableException {
        if (getSOA() != null) {
            Record[] records = getSOA().getSectionArray(1);
            if (records != null && records.length > 0) {
                return ((SOARecord) records[0]).getSerial();
            }
        }
        return -1;
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
        return host.getFullyQualifiedName();
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

    public Message getSOA() throws NameServerUnreachableException {
        Message ret;
        try {
            ret = getSOAByUDP();
            if (ret == null) ret = getSOAByTCP();
        } catch (NameServerUnreachableException e) {
            ret = getSOAByTCP();
        }
        return ret;
    }

    public Message getSOAByUDP() throws NameServerUnreachableException {
        return soaByUDP.getMessage();
    }

    public Message getSOAByTCP() throws NameServerUnreachableException {
        return soaByTCP.getMessage();
    }
}
