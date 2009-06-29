package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * @author Piotr Tkaczyk
 */
public class RootServersPropagationException extends DomainTechnicalCheckException {

    public RootServersPropagationException(DNSDomain domain, DNSHost host) {
        super(domain, host);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptRootServersPropagationException(this);
    }
}
