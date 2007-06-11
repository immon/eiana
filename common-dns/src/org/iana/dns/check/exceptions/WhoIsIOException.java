package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;

/**
 * Thrown in MinimumNetworkDiversityCheck when DNSWhoIsDataRetriever service returns IOException.
 *
 * @author Piotr Tkaczyk
 */
public class WhoIsIOException extends NameServerTechnicalCheckException {

    DNSIPAddress ipAddress;
    Throwable cause;

    /**
     * Creates exception from given data.
     *
     * @param host      current host
     * @param ipAddress current host ip for with IOException was thrown
     * @param cause     cause from IOException
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

