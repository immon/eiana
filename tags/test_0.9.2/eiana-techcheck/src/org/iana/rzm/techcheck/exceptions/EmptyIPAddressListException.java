package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczy
 */
public class EmptyIPAddressListException extends DomainException {

    public EmptyIPAddressListException(String hostName) {
        super(hostName, "");    
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
