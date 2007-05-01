package org.iana.rzm.user;

import org.iana.criteria.Criterion;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public interface RoleManager {

    public Role get(long id);

    public void create(Role role);

    public void update(Role role);

    public void delete(Role role);

    public List<Role> findAll();

    public List<Role> find(Criterion criteria);

}
