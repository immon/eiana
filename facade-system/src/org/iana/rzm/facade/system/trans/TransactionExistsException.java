package org.iana.rzm.facade.system.trans;

/**
 * Thrown when there is an attempt to create a transaction for a domain when some transaction for this domain
 * already exists.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TransactionExistsException extends TransactionServiceException {

    private String domainName;

    public TransactionExistsException(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
