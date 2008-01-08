package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.system.trans.TransactionServiceException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoDomainModificationException extends TransactionServiceException {

    private String domainName;

    public NoDomainModificationException(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
