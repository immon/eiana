package org.iana.rzm.trans;

import java.util.Collection;
import java.util.HashSet;

/**
 * It will be refactored into a set of filters.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TransactionCriteria {
    private Collection<String> domainNames = new HashSet<String>();

    public void addDomainName(String domainName) {
        domainNames.add(domainName);
    }
}
