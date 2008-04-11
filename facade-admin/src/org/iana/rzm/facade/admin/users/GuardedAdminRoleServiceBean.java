package org.iana.rzm.facade.admin.users;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.converter.RoleConverter;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.RoleManager;
import org.iana.rzm.user.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminRoleServiceBean extends AbstractFinderService<RoleVO> implements AdminRoleService {

    RoleManager roleManager;

    private void isUserInRole() throws AccessDeniedException {
        isIana();
    }

    public GuardedAdminRoleServiceBean(UserManager userManager, RoleManager roleManager) {
        super(userManager);
        CheckTool.checkNull(roleManager, "role maager");
        this.roleManager = roleManager;
    }

    public RoleVO getRole(long id) throws AccessDeniedException {
        isUserInRole();
        Role retRole = this.roleManager.get(id);
        CheckTool.checkNull(retRole, "no such role: " + id);
        return RoleConverter.convertRole(retRole);
    }

    public long createRole(RoleVO roleVO) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(roleVO, "roleVO");
        Role newRole = RoleConverter.convertRole(roleVO);
        this.roleManager.create(newRole);
        return newRole.getObjId();
    }

    public void updateRole(RoleVO roleVO) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(roleVO, "roleVO");
        Role updateRole = RoleConverter.convertRole(roleVO);
        this.roleManager.update(updateRole);
    }

    public void deleteRole(long id) throws AccessDeniedException {
        isUserInRole();
        Role retRole = this.roleManager.get(id);
        CheckTool.checkNull(retRole, "no such role: " + id);
        this.roleManager.delete(retRole);
    }

    public List<RoleVO> findRoles() throws AccessDeniedException {
        isUserInRole();
        List<RoleVO> rolesVO = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.findAll())
            rolesVO.add(RoleConverter.convertRole(role));
        return rolesVO;
    }

    public List<RoleVO> findRoles(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        List<RoleVO> roleVOs = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.find(criteria))
            roleVOs.add(RoleConverter.convertRole(role));

        return roleVOs;
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        return roleManager.count(criteria);
    }

    public List<RoleVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        isUserInRole();
        List<RoleVO> roleVOs = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.find(criteria, offset, limit))
            roleVOs.add(RoleConverter.convertRole(role));

        return roleVOs;
    }


    public List<RoleVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        List<RoleVO> roleVOs = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.find(criteria))
            roleVOs.add(RoleConverter.convertRole(role));

        return roleVOs;
    }

    public RoleVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getRole(id);
    }
}
