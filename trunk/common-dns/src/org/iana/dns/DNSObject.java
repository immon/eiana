package org.iana.dns;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DNSObject {

    void accept(DNSVisitor visitor);
    
}
