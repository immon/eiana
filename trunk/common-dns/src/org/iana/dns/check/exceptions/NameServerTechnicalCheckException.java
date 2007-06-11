package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckException;

/**
 * @author Piotr Tkaczyk
 *         <p/>
 *         Base exception for all checks from DNSNameServerTechnicalCheck;
 */
public class NameServerTechnicalCheckException extends DNSTechnicalCheckException {
    DNSHost host;

    public NameServerTechnicalCheckException(DNSHost host) {
        this.host = host;
    }

    public DNSHost getHost() {
        return host;
    }

    public String getHostName() {
        return host.getName();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameServerTechnicalCheckException that = (NameServerTechnicalCheckException) o;

        if (host != null ? !host.equals(that.host) : that.host != null) return false;

        return true;
    }

    public int hashCode() {
        return (host != null ? host.hashCode() : 0);
    }
}
