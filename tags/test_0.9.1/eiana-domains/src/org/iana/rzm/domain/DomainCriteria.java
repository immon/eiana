package org.iana.rzm.domain;

import java.util.HashSet;
import java.util.Collection;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainCriteria {

    private Collection<String> domainNames = new HashSet<String>();

    public Collection<String> getDomainNames() {
        return domainNames;
    }

    public void addDomainName(String domainName) {
        domainNames.add(domainName);
    }

    public void addAllDomainNames(Collection<String> domainNames) {
        this.domainNames.addAll(domainNames);
    }
}
