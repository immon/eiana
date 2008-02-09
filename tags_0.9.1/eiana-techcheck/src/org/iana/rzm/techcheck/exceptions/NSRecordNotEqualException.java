package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class NSRecordNotEqualException extends DomainException {

    public NSRecordNotEqualException(String owner) {
        super(owner, "");
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
