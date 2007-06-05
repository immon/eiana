package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class NotAuthoritativeNameServerException extends NameServerTechnicalCheckException {

    public NotAuthoritativeNameServerException(DNSHost host) {
        super(host);
    }
}
