package org.iana.rzm.domain;

import org.iana.criteria.Criterion;

import java.util.List;
import java.util.Set;

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

    public Domain getCloned(String name);

    public Domain getCloned(long id);

    public void updateOpenProcesses(String name, boolean inc);

    public void updateThirdPartyPendingProcesses(String name, boolean inc);

    public void create(Domain domain);

    public void update(Domain domain, String updater);

    public void delete(Domain domain);

    public void delete(String name);

    public List<Domain> findAll();

    public List<Domain> find(Criterion criteria);

    public List<Domain> findDelegatedTo(Set<String> hostName);

    public int count(Criterion criteria);

    public List<Domain> find(Criterion criteria, int offset, int limit);
}
