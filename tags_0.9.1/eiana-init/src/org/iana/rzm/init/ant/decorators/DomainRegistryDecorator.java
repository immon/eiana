package org.iana.rzm.init.ant.decorators;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */
public class DomainRegistryDecorator {

    List<DomainDecorator> domains = new ArrayList<DomainDecorator>();


    public List<DomainDecorator> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainDecorator> domains) {
        this.domains = domains;
    }
}
