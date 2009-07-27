package org.iana.dns;

import org.apache.log4j.Logger;
import org.iana.dns.check.DNSNameServer;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class FromDNSUpdateAction {

    private static Logger logger = Logger.getLogger(FromDNSUpdateAction.class);

    private RootServersProducer rsProducer;

    private int retries = 1;

    public FromDNSUpdateAction(RootServersProducer rsProducer) {
        this.rsProducer = rsProducer;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void execute() throws Exception {
        try {
            List<DNSHost> rServers = rsProducer.getRootServers();

            List<DNSHost> toUpdate = new ArrayList<DNSHost>();

            for (DNSHost rs : rServers) {
                DNSDomain domain = new DNSDomainImpl(rs.getName());
                DNSNameServer ns = new DNSNameServer(domain, rs, retries);

                Set<String> ipAddresses = new HashSet<String>();
                for(Record r : ns.getRecords()) {
                    String address = parseAddress(r);
                    if (address != null && address.trim().length() > 0) {
                        ipAddresses.add(address);
                    }
                }

                if (!ipAddresses.isEmpty()) {
                    DNSHostImpl host = new DNSHostImpl(rs.getName());
                    host.setIPAddressesAsStrings(ipAddresses);
                    toUpdate.add(host);
                }
            }

            rsProducer.updateRootServers(toUpdate);

        } catch (Exception e) {
            logger.error("when updating rootservers config from dns", e);
        }
    }

    private String parseAddress(Record record) {
        if (ARecord.class.isAssignableFrom(record.getClass())) {
            ARecord aRecord = (ARecord) record;
            return aRecord.getAddress().getHostAddress();
        }

        if (AAAARecord.class.isAssignableFrom(record.getClass())) {
            AAAARecord a4Record = (AAAARecord) record;
            return a4Record.getAddress().getHostAddress();
        }

        return null;
    }
}
