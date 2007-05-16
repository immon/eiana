package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class NotEnoughHostsException extends DomainException {


    public NotEnoughHostsException(String domainName) {
        super(domainName, "");
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
    
}
