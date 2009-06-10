package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessAdminRoleServiceBean extends AbstractRZMStatelessService implements StatelessAdminRoleService{

    StatelessAdminRoleService statelessAdminRoleService;

    public GuardedStatelessAdminRoleServiceBean(UserManager userManager, StatelessAdminRoleService statelessAdminRoleService) {
        super(userManager);
        CheckTool.checkNull(statelessAdminRoleService, "null admin role service");
        this.statelessAdminRoleService = statelessAdminRoleService;
    }

    public RoleVO getRole(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminRoleService.getRole(id, authUser);
    }

    public long createRole(RoleVO roleVO, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminRoleService.createRole(roleVO, authUser);
    }

    public void updateRole(RoleVO roleVO, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminRoleService.updateRole(roleVO, authUser);
    }

    public void deleteRole(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminRoleService.deleteRole(id, authUser);
    }

    public List<RoleVO> findRoles(AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminRoleService.findRoles(authUser);
    }

    public List<RoleVO> findRoles(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminRoleService.findRoles(criteria, authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminRoleService.count(criteria, authUser);
    }

    public List<RoleVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminRoleService.find(criteria, offset, limit, authUser);
    }


    public List<RoleVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminRoleService.find(criteria, authUser);
    }

    public RoleVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        isIana(authUser);
        return statelessAdminRoleService.get(id, authUser);
    }
}
