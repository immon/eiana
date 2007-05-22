package org.iana.rzm.domain.dao;

import org.iana.rzm.domain.Host;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 *
 * @author Patrycja Wegrzynowicz
 */
public interface HostDAO {

    public Host get(long id);

    public Host get(String name);

    public void delete(Host host);

    public List<Host> find(Criterion criterion);

    public List<Host> findAll();

    public int count(Criterion criteria);

    public List<Host> find(Criterion criteria, int offset, int limit);
}
