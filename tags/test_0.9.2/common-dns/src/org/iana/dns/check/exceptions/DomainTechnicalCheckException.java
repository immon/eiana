package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Base exception for all checks from DNSDomainTechnicalCheck.
 *
 * @author Piotr Tkaczyk
 */
public class DomainTechnicalCheckException extends DNSTechnicalCheckException {
    DNSDomain domain;
    DNSHost host;

    public DomainTechnicalCheckException(DNSDomain domain, DNSHost host) {
        this.domain = domain;
        this.host = host;
    }

    public String getDomainName() {
        return domain != null ? domain.getName() : null;
    }

    public DNSDomain getDomain() {
        return domain;
    }

    public String getHostName() {
        return host != null ? host.getName() : null;
    }

    public DNSHost getHost() {
        return host;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainTechnicalCheckException that = (DomainTechnicalCheckException) o;

        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        return result;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptDomainTechnicalCheckException(this);
    }
}