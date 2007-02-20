package org.iana.rzm.domain;

import java.util.List;

/**
 * It provides services for direct access and modification of a given domain object.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DomainManager {

    public Domain get(String name) throws DomainException;

    public Domain get(long id) throws DomainException;

    public void create(Domain domain) throws DomainException;

    public void update(Domain domain) throws DomainException;

    public List<Domain> findAll() throws DomainException;

    public List<Domain> find(DomainCriteria criteria) throws DomainException;
}
