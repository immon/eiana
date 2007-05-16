package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */

public class HostIPSetNotEqualException extends DomainException {

    public HostIPSetNotEqualException(String hostName) {
        super(hostName, "");
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
