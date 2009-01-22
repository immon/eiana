package org.iana.rzm.facade.system.trans;

/**
 * @author Piotr Tkaczyk
 */
public class RadicalAlterationException extends TransactionServiceException {

    String domainName;

    public RadicalAlterationException(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
