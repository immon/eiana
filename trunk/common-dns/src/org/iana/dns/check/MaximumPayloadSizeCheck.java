package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import static org.iana.dns.DNSIPAddress.Type.IPv4;
import org.iana.dns.check.exceptions.MaximumPayloadSizeExceededException;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * (Test 2)
 * Checks that estimated response size from root server is not greater than 512 bytes.
 *
 * @author Piotr Tkaczyk
 */
public class MaximumPayloadSizeCheck implements DNSDomainTechnicalCheck {

    private static final int SZ_HEADER = 12;
    private static final int SZ_QUERY = 7;
    private static final int SZ_PTR = 2;
    private static final int SZ_TYPE = 2;
    private static final int SZ_CLASS = 2;
    private static final int SZ_TTL = 4;
    private static final int SZ_RDLEN = 2;

    private List<String> suffixes = new ArrayList<String>();

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {

        int domainNameSize = domain.getName().length() + 2;

        int estimatedSize = SZ_HEADER + SZ_QUERY + domainNameSize;
        int constSize = SZ_PTR + SZ_TYPE + SZ_CLASS + SZ_TTL + SZ_RDLEN;
        suffixes.add(domain.getName());

        //section 0 of SOA Record
        estimatedSize += domainNameSize;
        //section 1
        estimatedSize += domainNameSize;
        for (DNSNameServer ns : nameServers) {
            int nsNameSize = nsNameEstimatedSize(ns.getName());
            //section 2
            estimatedSize += nsNameSize + constSize;
            //section 3
            for (DNSIPAddress ipAddress : ns.getIPAddresses()) {
                int ipAddressSize = (ipAddress.getType() == IPv4) ? 8 : 16;
                estimatedSize += ipAddressSize + SZ_TYPE + SZ_TTL + SZ_PTR;
            }
        }
        if (estimatedSize > 512) throw new MaximumPayloadSizeExceededException(domain, estimatedSize);
    }

    private int nsNameEstimatedSize(String nsName) {
        int len = nsName.length() + 2;
        String[] labels = nsName.split("\\.");

        for (int i=0; i<labels.length; i++) {
            String suffix = joinLabels(i, labels);

            if (suffixes.contains(suffix))
                return nsName.length() - suffix.length() + SZ_PTR;

            suffixes.add(suffix);
        }

        return len;
    }

    private String joinLabels(int from, String[] labels) {
        StringBuffer buff = new StringBuffer("");

        for (int i=from; i<labels.length; i++) {
            if (buff.length() == 0)
                buff.append(labels[i]);
            else
                buff.append(".").append(labels[i]);
        }

        return buff.toString();
    }
}
