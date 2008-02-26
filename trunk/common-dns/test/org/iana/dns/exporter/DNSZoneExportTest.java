package org.iana.dns.exporter;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSZone;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.obj.DNSZoneImpl;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DNSZoneExportTest {

    @Test
    public void export1Zone2NS0Domain() {
        DNSZone zone = initZone();

        StringWriter out = new StringWriter();
        BindExport export = new BindExport(out);
        export.exportZone(zone);

        String valid = ". IN\tSOA\ta.root-servers.net. nstld.verisign-grs.com. (\n" +
                "\t\t\t\t\t2008022401 ; serial\n" +
                "\t\t\t\t\t1800 ; refresh\n" +
                "\t\t\t\t\t900 ; retry\n" +
                "\t\t\t\t\t604800 ; expire\n" +
                "\t\t\t\t\t86400 ; minimum\n" +
                ")\n" +
                "$TTL 518400\n" +
                ". NS a.root-servers.net.\n" +
                ". NS b.root-servers.net.\n" +
                "a.root-servers.net. A 198.41.0.4\n" +
                "a.root-servers.net. AAAA 2001:503:ba3e:0:0:0:2:30\n" +
                "b.root-servers.net. A 128.63.2.53\n" +
                "b.root-servers.net. AAAA 2001:500:1:0:0:0:803f:235\n" +
                "$TTL 172800\n";
        valid = valid.replaceAll("\\n", System.getProperty("line.separator"));
        assert valid.equals(out.toString());
    }

    @Test
    public void export1Zone2NS2Domains() {
        DNSZone zone = initZoneWithDomains();

        StringWriter out = new StringWriter();
        BindExport export = new BindExport(out);
        export.exportZone(zone);

        String valid = ". IN\tSOA\ta.root-servers.net. nstld.verisign-grs.com. (\n" +
                "\t\t\t\t\t2008022401 ; serial\n" +
                "\t\t\t\t\t1800 ; refresh\n" +
                "\t\t\t\t\t900 ; retry\n" +
                "\t\t\t\t\t604800 ; expire\n" +
                "\t\t\t\t\t86400 ; minimum\n" +
                ")\n" +
                "$TTL 518400\n" +
                ". NS a.root-servers.net.\n" +
                ". NS b.root-servers.net.\n" +
                "a.root-servers.net. A 198.41.0.4\n" +
                "a.root-servers.net. AAAA 2001:503:ba3e:0:0:0:2:30\n" +
                "b.root-servers.net. A 128.63.2.53\n" +
                "b.root-servers.net. AAAA 2001:500:1:0:0:0:803f:235\n" +
                "$TTL 172800\n" +
                "by. NS arwena.nask.waw.pl.\n" +
                "pl. NS a-dns.pl.\n" +
                "pl. NS b-dns.pl.\n" +
                "pl. NS f-dns.pl.\n" +
                "a-dns.pl. A 195.187.245.44\n" +
                "arwena.nask.waw.pl. A 193.59.201.28\n" +
                "b-dns.pl. A 80.50.50.10\n" +
                "f-dns.pl. A 217.17.46.189\n" +
                "f-dns.pl. AAAA 2001:1a68:0:10:0:0:0:189\n";
        valid = valid.replaceAll("\\n", System.getProperty("line.separator"));
        assert valid.equals(out.toString());
    }

    private DNSZoneImpl initZone() {
        DNSZoneImpl zone = new DNSZoneImpl("");
        zone.setTTL1(518400);
        zone.setTTL2(172800);
        zone.setSerial("2008022401");
        zone.setRefresh(1800);
        zone.setRetry(900);
        zone.setExpire(604800);
        zone.setMinimum(86400);
        zone.setAdminAddress("nstld.verisign-grs.com.");
        DNSHostImpl a = new DNSHostImpl("a.root-servers.net");
        a.addIPAddress("198.41.0.4");
        a.addIPAddress("2001:503:BA3E:0:0:0:2:30");
        DNSHostImpl b = new DNSHostImpl("b.root-servers.net");
        b.addIPAddress("128.63.2.53");
        b.addIPAddress("2001:500:1:0:0:0:803F:235");
        zone.addNameServer(a);
        zone.addNameServer(b);
        return zone;
    }

    private DNSZoneImpl initZoneWithDomains() {
        DNSZoneImpl zone = initZone();
        Set<DNSDomain> domains = new TreeSet<DNSDomain>();

        DNSDomainImpl pl = new DNSDomainImpl("pl");
        DNSHostImpl apl = new DNSHostImpl("a-dns.pl");
        apl.addIPAddress("195.187.245.44");
        DNSHostImpl bpl = new DNSHostImpl("b-dns.pl");
        bpl.addIPAddress("80.50.50.10");
        DNSHostImpl fpl = new DNSHostImpl("f-dns.pl");
        fpl.addIPAddress("217.17.46.189");
        fpl.addIPAddress("2001:1A68:0:10:0:0:0:189");
        pl.addNameServer(apl);
        pl.addNameServer(bpl);
        pl.addNameServer(fpl);

        DNSDomainImpl by = new DNSDomainImpl("by");
        DNSHostImpl arwena = new DNSHostImpl("ARWENA.NASK.WAW.PL");
        arwena.addIPAddress("193.59.201.28");
        by.addNameServer(arwena);

        domains.add(pl);
        domains.add(by);
        zone.setDomains(domains);

        return zone;
    }
}
