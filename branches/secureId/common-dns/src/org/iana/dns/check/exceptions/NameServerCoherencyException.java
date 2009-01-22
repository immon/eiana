package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

import java.util.Set;
import java.util.HashSet;

/**
 * Thrown in NameServerCoherencyCheck when supplied name servers names don't match names returned in SOA.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerCoherencyException extends DomainTechnicalCheckException {

    private Set<String> expectedNameServers;

    private Set<String> receivedNameServers;

    public NameServerCoherencyException(DNSDomain domain) {
        this(domain, null, new HashSet<String>(), new HashSet<String>());
    }

    public NameServerCoherencyException(DNSDomain domain, DNSHost host, Set<String> expectedNameServers, Set<String> receivedNameServers) {
        super(domain, host);
        this.expectedNameServers = expectedNameServers;
        this.receivedNameServers = receivedNameServers;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNameServerCoherencyException(this);
    }

    public Set<String> getExpectedNameServers() {
        return expectedNameServers;
    }

    public Set<String> getReceivedNameServers() {
        return receivedNameServers;
    }
}
