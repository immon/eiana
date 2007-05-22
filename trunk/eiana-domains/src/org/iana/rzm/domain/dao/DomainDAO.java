package org.iana.rzm.domain.dao;

import org.iana.rzm.domain.Domain;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DomainDAO {

    public Domain get(long id);

    public Domain get(String name);

    public void create(Domain domain);

    public void update(Domain domain);

    public void delete(Domain domain);

    public List<Domain> find(Criterion criterion);

    public List<Domain> findDelegatedTo(Set<String> hostNames);

    public List<Domain> findAll();

    public int count(Criterion criteria);

    public List<Domain> find(Criterion criteria, int offset, int limit);
}
