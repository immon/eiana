package org.iana.rzm.facade.system.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoDomainModificationException extends Exception {

    private String domainName;

    public NoDomainModificationException(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}
