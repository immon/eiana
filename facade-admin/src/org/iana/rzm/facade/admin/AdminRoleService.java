package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminRoleService extends RZMStatefulService {

    public RoleVO getRole(long id);

    public long createRole(RoleVO role);

    public void updateRole(RoleVO role);

    public void deleteRole(long id);

    public List<RoleVO> findRoles();

    public List<RoleVO> findRoles(Criterion criteria);

    public int count(Criterion criteria);

    public List<RoleVO> find(Criterion criteria, int offset, int limit);
}
