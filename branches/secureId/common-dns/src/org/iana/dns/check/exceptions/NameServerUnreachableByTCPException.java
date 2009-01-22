package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in NameServerReachabilityCheck when SOA record in unreachable by TCP.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableByTCPException extends NameServerUnreachableException {

    /**
     * Creates exception from given data.
     *
     * @param host current host
     */
    public NameServerUnreachableByTCPException(DNSHost host) {
        super(host);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNameServerUnreachableByTCPException(this);
    }
}
