package org.iana.rzm.user.dao;

import org.iana.rzm.user.Role;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public interface RoleDAO {

    public Role get(long id);

    public void create(Role role);

    public void update(Role role);

    public void delete(Role role);

    public List<Role> findAll();

    public List<Role> find(Criterion criteria);

    public int count(Criterion criteria);
    public List<Role> find(Criterion criteria, int offset, int limit);
}
