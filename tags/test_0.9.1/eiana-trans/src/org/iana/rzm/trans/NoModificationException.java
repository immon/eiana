package org.iana.rzm.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoModificationException extends TransactionException {

    private String domainName;

    public NoModificationException(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
