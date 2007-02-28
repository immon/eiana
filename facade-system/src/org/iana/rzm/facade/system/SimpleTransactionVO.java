package org.iana.rzm.facade.system;

import org.iana.rzm.facade.common.TrackedObjectVO;

/**
 * A simplified version of TransactionVO used with lists of transactions.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class SimpleTransactionVO extends TrackedObjectVO {

    private String name;
    private SimpleDomainVO domain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleDomainVO getDomain() {
        return domain;
    }

    public void setDomain(SimpleDomainVO domain) {
        this.domain = domain;
    }
}
