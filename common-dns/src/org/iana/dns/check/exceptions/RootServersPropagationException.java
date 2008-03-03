package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class RootServersPropagationException extends DomainTechnicalCheckException {

    public RootServersPropagationException(DNSDomain domain, DNSHost host) {
        super(domain, host);
    }
}
