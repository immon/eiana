package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in NameServerCheckBase when SOA record in unreachable.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableException extends NameServerTechnicalCheckException {

    public NameServerUnreachableException(DNSHost host) {
        super(host);
    }

    public NameServerUnreachableException(Throwable cause, DNSHost host) {
        super(cause, host);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNameServerUnreachableException(this);
    }
}
