package org.iana.rzm.domain.exporter;

import org.iana.rzm.domain.Domain;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class DomainDecorator {


    List<Domain> domains;

    public DomainDecorator(List<Domain> domains) {
        this.domains = domains;

    }

    public List<Domain> getDomain() {
        return this.domains;
    }
}
