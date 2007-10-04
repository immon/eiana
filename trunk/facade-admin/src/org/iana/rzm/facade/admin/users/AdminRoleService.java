package org.iana.rzm.facade.admin.users;

import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.services.FinderService;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminRoleService extends RZMStatefulService, FinderService<RoleVO> {

    public RoleVO getRole(long id) throws AccessDeniedException;

    public long createRole(RoleVO role) throws AccessDeniedException;

    public void updateRole(RoleVO role) throws AccessDeniedException;

    public void deleteRole(long id) throws AccessDeniedException;

    public List<RoleVO> findRoles() throws AccessDeniedException;

    public List<RoleVO> findRoles(Criterion criteria) throws AccessDeniedException;

    public int count(Criterion criteria) throws AccessDeniedException;

    public List<RoleVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException;
}
