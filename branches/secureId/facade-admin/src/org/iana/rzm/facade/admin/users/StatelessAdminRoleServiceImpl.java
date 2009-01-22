package org.iana.rzm.facade.admin.users;

import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.converter.RoleConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.RoleManager;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessAdminRoleServiceImpl implements StatelessAdminRoleService {

    RoleManager roleManager;

    public StatelessAdminRoleServiceImpl(RoleManager roleManager) {
        CheckTool.checkNull(roleManager, "role manager is null");
        this.roleManager = roleManager;
    }

    public RoleVO getRole(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        Role retRole = this.roleManager.get(id);
        CheckTool.checkNull(retRole, "no such role: " + id);
        return RoleConverter.convertRole(retRole);
    }

    public long createRole(RoleVO roleVO, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkNull(roleVO, "roleVO");
        Role newRole = RoleConverter.convertRole(roleVO);
        this.roleManager.create(newRole);
        return newRole.getObjId();
    }

    public void updateRole(RoleVO roleVO, AuthenticatedUser authUser) throws AccessDeniedException {
        CheckTool.checkNull(roleVO, "roleVO");
        Role updateRole = RoleConverter.convertRole(roleVO);
        this.roleManager.update(updateRole);
    }

    public void deleteRole(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        Role retRole = this.roleManager.get(id);
        CheckTool.checkNull(retRole, "no such role: " + id);
        this.roleManager.delete(retRole);
    }

    public List<RoleVO> findRoles(AuthenticatedUser authUser) throws AccessDeniedException {
        List<RoleVO> rolesVO = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.findAll())
            rolesVO.add(RoleConverter.convertRole(role));
        return rolesVO;
    }

    public List<RoleVO> findRoles(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        List<RoleVO> roleVOs = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.find(criteria))
            roleVOs.add(RoleConverter.convertRole(role));

        return roleVOs;
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        return roleManager.count(criteria);
    }

    public List<RoleVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException {
        List<RoleVO> roleVOs = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.find(criteria, offset, limit))
            roleVOs.add(RoleConverter.convertRole(role));

        return roleVOs;
    }


    public List<RoleVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        List<RoleVO> roleVOs = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.find(criteria))
            roleVOs.add(RoleConverter.convertRole(role));

        return roleVOs;
    }

    public RoleVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getRole(id, authUser);
    }
}
