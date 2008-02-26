package org.iana.dns.exporter;

import org.iana.dns.*;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Patrycja Wegrzynowicz
 */
class BindExport {

    private static final String IN = "IN";

    private static final String SOA = "SOA";

    private static final String NS = "NS";

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

    private Set<DNSHost> glueHosts = new TreeSet<DNSHost>();

    public BindExport(Writer out) {
        this.out = new PrintWriter(out);
    }

    public void exportZone(DNSZone zone) {
        exportHeader(zone);
        exportHeaderNS(zone);
        exportDomains(zone);
    }

    private void exportHeader(DNSZone zone) {
        _prints(zone.getFullyQualifiedName());
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
        _prints(TTL);
        _println(zone.getTTL1());
        exportDomain(zone);
        flushGlue();
    }

    private void exportDomains(DNSZone zone) {
        _prints(TTL);
        _println(zone.getTTL2());
        Set<DNSDomain> domains = zone.getDomains();
        if (domains != null) {
            for (DNSDomain domain : zone.getDomains()) {
                exportDomain(domain);
            }
            flushGlue();
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

    public void exportDomain(DNSDomain domain) {
        String domainName = domain.getFullyQualifiedName();
        for (DNSHost host : domain.getNameServers()) {
            _prints(domainName);
            _prints(NS);
            _println(host.getFullyQualifiedName());
            // if (host.isInDomain(domain))
            glueHosts.add(host);
        }
    }

    private void flushGlue() {
        for (DNSHost host : glueHosts) {
            exportGlue(host);
        }
        glueHosts.clear();
    }

    public void exportGlue(DNSHost host) {
        String hostName = host.getFullyQualifiedName();
        for (DNSIPAddress addr : host.getIPAddresses()) {
            _prints(hostName);
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
