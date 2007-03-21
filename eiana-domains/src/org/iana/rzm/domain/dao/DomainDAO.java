package org.iana.rzm.domain.dao;

import org.iana.rzm.domain.Domain;

/**
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DomainDAO {

    public Domain get(long id);

    public Domain get(String name);

    public void create(Domain domain);

    public void update(Domain domain);
}
