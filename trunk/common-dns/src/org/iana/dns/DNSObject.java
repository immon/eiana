package org.iana.dns;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DNSObject extends Serializable {

    void accept(DNSVisitor visitor);
    
}
