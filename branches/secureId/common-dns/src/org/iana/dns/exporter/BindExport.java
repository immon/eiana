package org.iana.dns.exporter;

import org.iana.dns.*;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
class BindExport {

    private static final String IN = "IN";

    private static final String SOA = "SOA";

    private static final String NS = "NS";

    private static final String DS = "DS";

    private static final String A = "A";

    private static final String AAAA = "AAAA";

    private static final String TTL = "$TTL";

    private static final String TAB = "\t";

    private static final String SPACE = " ";

    private static final String SERIAL = "serial";

    private static final String REFRESH = "refresh";

    private static final String RETRY = "retry";

    private static final String EXPIRE = "expire";

    private static final String MINIMUM = "minimum";

    private PrintWriter out;

    private Set<DNSHost> glueHostsExported = new HashSet<DNSHost>();
    private Set<DNSHost> glueHosts = new TreeSet<DNSHost>();

    public BindExport(Writer out) {
        this.out = new PrintWriter(out);
    }

    public void exportZone(DNSZone zone) {
        exportHeader(zone);
        exportHeaderNS(zone);
        exportDomains(zone, zone.getDefaultTTL());
        glueHostsExported.clear();
    }

    private void exportHeader(DNSZone zone) {
        _prints(zone.getFullyQualifiedName());
        _prints(zone.getZoneTTL());
        _printt(IN);
        _printt(SOA);
        _prints(getPrimaryServer(zone).getFullyQualifiedName());
        _prints(zone.getAdminAddress());
        _println("(");
        _param(zone.getSerial(), SERIAL);
        _param(zone.getRefresh(), REFRESH);
        _param(zone.getRetry(), RETRY);
        _param(zone.getExpire(), EXPIRE);
        _param(zone.getMinimum(), MINIMUM);
        _println(")");
    }

    private void exportHeaderNS(DNSZone zone) {
        exportDomain(zone, zone.getZoneNameServersTTL());
        flushGlue(zone.getZoneNameServersTTL());
    }

    private void exportDomains(DNSZone zone, long ttl) {
        Set<DNSDomain> domains = zone.getDomains();
        if (domains != null) {
            for (DNSDomain domain : zone.getDomains()) {
                exportDomain(domain, ttl);
            }
            flushGlue(ttl);
        }
    }


    private DNSHost getPrimaryServer(DNSZone zone) {
        DNSHost ret = zone.getPrimaryServer();
        if (ret == null) {
            Set<DNSHost> hosts = zone.getNameServers();
            if (hosts.isEmpty()) throw new IllegalArgumentException("empty name servers for a zone " + zone.getName());
            ret = hosts.iterator().next();
        }
        return ret;
    }

    public void exportDomain(DNSDomain domain, long ttl) {
        String domainName = domain.getFullyQualifiedName();
        for (DNSHost host : domain.getNameServers()) {
            _prints(domainName);
            _prints(ttl);
            _prints(IN);
            _prints(NS);
            _println(host.getFullyQualifiedName());
            // if (host.isInDomain(domain))
            glueHosts.add(host);
        }
        exportDSs(domainName, domain.getDSRecords());
    }

    private void exportDSs(String domainName, List<DNSDelegationSigner> list) {
        for (DNSDelegationSigner ds : list) {
            _prints(domainName);
            _prints(DS);
            _prints(""+ds.getKeyTag());
            _prints(""+ds.getAlg());
            _prints(""+ds.getDigestType());
            _println(""+ds.getDigest());
        }
    }

    private void flushGlue(long ttl) {
        for (DNSHost host : glueHosts) {
            if (!glueHostsExported.contains(host)) {
                exportGlue(host, ttl);
            }
        }
        glueHostsExported.addAll(glueHosts);
        glueHosts.clear();
    }

    public void exportGlue(DNSHost host, long ttl) {
        String hostName = host.getFullyQualifiedName();
        for (DNSIPAddress addr : host.getIPAddresses()) {
            _prints(hostName);
            _prints(ttl);
            _prints(IN);
            switch (addr.getType()) {
                case IPv4:
                    _prints(A); break;
                case IPv6:
                    _prints(AAAA); break;
                default:
                    throw new IllegalArgumentException("unknown ip type: " + addr.getType());
            }
            _println(addr.getAddress());
        }
    }

    private void _param(String val, String name) {
        _tab(5);
        _prints(val);
        _prints(";");
        _println(name);
    }

    private void _param(long val, String name) {
        _param(String.valueOf(val), name);
    }

    private void _print(String s) {
        out.print(s);
    }

    private void _println(String s) {
        out.println(s);
    }

    private void _println(long l) {
        out.println(l);
    }

    private void _printt(String s) {
        out.print(s);
        _tab();
    }

    private void _prints(long l) {
        out.print(l);
        _sp();
    }

    private void _prints(String s) {
        out.print(s);
        _sp();
    }

    private void _tab() {
        _print(TAB);
    }

    private void _sp() {
        _print(SPACE);
    }

    private void _tab(int i) {
        while (i-- > 0) _tab();
    }

}
