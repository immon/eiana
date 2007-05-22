package org.iana.rzm.domain;

import org.iana.criteria.Criterion;

import java.util.List;

/**
 * <p/>
 * This interface provides a way to access host objects directly.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface HostManager {

    public Host get(String name);

    public Host get(long id);

    public void delete(String name);

    public void delete(Host host);

    public List<Host> findAll();

    public List<Host> find(Criterion criteria);

    public int count(Criterion criteria);

    public List<Host> find(Criterion criteria, int offset, int limit);
}
