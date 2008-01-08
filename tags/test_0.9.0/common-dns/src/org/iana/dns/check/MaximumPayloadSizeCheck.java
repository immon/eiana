package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import static org.iana.dns.DNSIPAddress.Type.IPv4;
import org.iana.dns.check.exceptions.MaximumPayloadSizeExceededException;

import java.util.Set;

/**
 * (Test 2)
 * Checks that estimated response size from root server is not greater than 512 bytes.
 *
 * @author Piotr Tkaczyk
 */
public class MaximumPayloadSizeCheck implements DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        int estimatedSize = 78;
        int domainNameSize = domain.getNameWithDot().length() + 1;
        //section 0 of SOA Record
        estimatedSize += domainNameSize;
        //section 1
        estimatedSize += domainNameSize;
        for (DNSNameServer ns : nameServers) {
            int nsNameSize = ns.getNameWithDot().length() + 1;
            //section 2
            estimatedSize += domainNameSize + nsNameSize;
            //section 3
            for (DNSIPAddress ipAddress : ns.getIPAddresses()) {
                int ipAddressSize = (ipAddress.getType() == IPv4) ? 8 : 16;
                estimatedSize += nsNameSize + ipAddressSize;
            }
        }
        if (estimatedSize > 512) throw new MaximumPayloadSizeExceededException(domain, estimatedSize);
    }
}
