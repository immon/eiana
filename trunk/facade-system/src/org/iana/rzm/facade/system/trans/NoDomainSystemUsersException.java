package org.iana.rzm.facade.system.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoDomainSystemUsersException extends TransactionServiceException {

    private String domainName;

    public NoDomainSystemUsersException(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
