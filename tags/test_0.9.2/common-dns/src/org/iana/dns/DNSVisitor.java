package org.iana.dns;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DNSVisitor {

    void visitZone(DNSZone zone);

    void visitDomain(DNSDomain domain);

    void visitHost(DNSHost host);

    void visitIPv4Address(DNSIPv4Address address);

    void visitIPv6Address(DNSIPv6Address address);
    
}
