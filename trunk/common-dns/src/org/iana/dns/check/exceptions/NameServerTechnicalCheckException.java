package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Base exception for all checks from DNSNameServerTechnicalCheck.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerTechnicalCheckException extends DNSTechnicalCheckException {

    DNSHost host;

    public NameServerTechnicalCheckException(DNSHost host) {
        this.host = host;
    }

    public NameServerTechnicalCheckException(Throwable cause, DNSHost host) {
        super(cause);
        this.host = host;
    }

    public DNSHost getHost() {
        return host;
    }

    public String getHostName() {
        return (host == null)? null : host.getName();
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

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNameServerTechnicalCheckException(this);
    }
}
