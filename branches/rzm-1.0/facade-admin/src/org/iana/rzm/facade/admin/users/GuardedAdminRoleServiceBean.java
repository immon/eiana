package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminRoleServiceBean extends AbstractFinderService<RoleVO> implements AdminRoleService {

    private StatelessAdminRoleService statelessAdminRoleService;

    public GuardedAdminRoleServiceBean(UserVOManager userManager, StatelessAdminRoleService statelessAdminRoleService) {
        super(userManager);
        CheckTool.checkNull(statelessAdminRoleService, "stateless admin role service");
        this.statelessAdminRoleService = statelessAdminRoleService;
    }

    public RoleVO getRole(long id) throws AccessDeniedException {
        return statelessAdminRoleService.getRole(id, getAuthenticatedUser());
    }

    public long createRole(RoleVO roleVO) throws AccessDeniedException {
        return statelessAdminRoleService.createRole(roleVO, getAuthenticatedUser());
    }

    public void updateRole(RoleVO roleVO) throws AccessDeniedException {
        statelessAdminRoleService.updateRole(roleVO, getAuthenticatedUser());
    }

    public void deleteRole(long id) throws AccessDeniedException {
        statelessAdminRoleService.deleteRole(id, getAuthenticatedUser());
    }

    public List<RoleVO> findRoles() throws AccessDeniedException {
        return statelessAdminRoleService.findRoles(getAuthenticatedUser());
    }

    public List<RoleVO> findRoles(Criterion criteria) throws AccessDeniedException {
        return statelessAdminRoleService.findRoles(criteria, getAuthenticatedUser());
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return statelessAdminRoleService.count(criteria, getAuthenticatedUser());
    }

    public List<RoleVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        return statelessAdminRoleService.find(criteria, offset, limit, getAuthenticatedUser());
    }


    public List<RoleVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessAdminRoleService.find(criteria, getAuthenticatedUser());
    }

    public RoleVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return statelessAdminRoleService.get(id, getAuthenticatedUser());
    }
}
