package org.iana.rzm.domain;

import org.iana.criteria.Criterion;

import java.util.List;

/**
 * <p/>
 * This interface provides a way to access and modify domain objects directly
 * i.e. without a mean of creating domain transactions.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DomainManager {

    public Domain get(String name);

    public Domain get(long id);

    public void create(Domain domain);

    public void update(Domain domain);

    public void delete(Domain domain);

    public void delete(String name);

    public List<Domain> findAll();

    public List<Domain> find(Criterion criteria);
}
