package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;

/**
 * @author Piotr Tkaczyk
 */
public class WhoIsIOException extends NameServerTechnicalCheckException {

    DNSIPAddress ipAddress;
    Throwable cause;

    /**
     * Thrown in MinimumNetworkDiversityCheck when DNSWhoIsData service returns IOException;
     *
     * @param host      - current host
     * @param ipAddress - current host ip for with IOException was thrown
     * @param cause     - cause from IOException
     */
    public WhoIsIOException(DNSHost host, DNSIPAddress ipAddress, Throwable cause) {
        super(host);
        this.ipAddress = ipAddress;
        this.cause = cause;
    }

    public DNSIPAddress getIpAddress() {
        return ipAddress;
    }

    public Throwable getCause() {
        return cause;
    }
}

