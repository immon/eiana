package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.common.RZMStatefulService;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminRoleService extends RZMStatefulService {

    public RoleVO getRole(long id);

    public void createRole(RoleVO role);

    public void updateRole(RoleVO role);

    public void deleteRole(long id);

    public List<RoleVO> findRoles();

    public List<RoleVO> findRoles(RoleCriteria criteria);
}
