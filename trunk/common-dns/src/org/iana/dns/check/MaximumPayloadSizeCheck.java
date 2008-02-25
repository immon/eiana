package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.MaximumPayloadSizeExceededException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * (Test 2)
 * Checks that estimated response size from root server is not greater than 512 bytes.
 *
 * @author Piotr Tkaczyk
 */
public class MaximumPayloadSizeCheck implements DNSDomainTechnicalCheck {

    private static final int MAX_SIZE = 512;
    private static final int NS_SIZE = 12;
    private static final int HEADER_SIZE = 12;
    private static final int QUERY_SIZE = 255;
    private static final int A_SIZE = 16;
    private static final int AAAA_SIZE = 28;

    private static final int SZ_PTR = 2;

    private List<String> suffixes = new ArrayList<String>();

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        int ns_set_size = 0;
        boolean needs_v4_glue = false;
        boolean needs_v6_glue = false;

        compress_domain(domain.getName());

        for (DNSNameServer ns : nameServers) {
            int domainLen = compress_domain(ns.getName());
            ns_set_size += NS_SIZE + domainLen;

            for (DNSIPAddress ipAddress : ns.getIPAddresses()) {
                if (DNSIPAddress.Type.IPv4.equals(ipAddress.getType())) {
                    if (in_bailiwick(ns.getName(), domain.getName()))
                        needs_v4_glue = true;
                } else {
                    if (in_bailiwick(ns.getName(), domain.getName()))
                        needs_v6_glue = true;
                }
            }
        }

        int packet_size = HEADER_SIZE + QUERY_SIZE + ns_set_size;

        if (needs_v4_glue)
            packet_size += A_SIZE;
        if (needs_v6_glue)
            packet_size += AAAA_SIZE;
        
        if (packet_size > MAX_SIZE) throw new MaximumPayloadSizeExceededException(domain, packet_size);
    }

    private int compress_domain(String nsName) {
        String[] labels = nsName.split("\\.");

        for (int i=0; i<labels.length; i++) {
            String suffix = joinLabels(i, labels);
            if (suffixes.contains(suffix))
                return (nsName.length() - (suffix.length() -1)) + SZ_PTR;

            suffixes.add(suffix);
        }

        return nsName.length() + 2;
    }

    private String joinLabels(int from, String[] labels) {
        StringBuffer buff = new StringBuffer("");

        for (int i=from; i<labels.length; i++) {
                buff.append(".").append(labels[i]);
        }

        return buff.toString();
    }

    private boolean in_bailiwick(String child, String parent) {
        if (parent.length() == 0) return true;

        int childLen = child.length();
        if (child.substring(childLen - parent.length(), childLen).equals(parent))
            return true;
        else
            return false;
    }
}
