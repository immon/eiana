package org.iana.rzm.domain;

import java.util.List;

/**
 * <p>
 * This interface provides a way to access and modify domain objects directly
 * i.e. without a mean of creating domain transactions.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DomainManager {

    public Domain get(String name) throws DomainException;

    public Domain get(long id) throws DomainException;

    public void create(Domain domain) throws DomainException;

    public List<Domain> findAll() throws DomainException;

    public List<Domain> find(DomainCriteria criteria) throws DomainException;
}
