package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.converter.RoleConverter;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RoleManager;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminRoleServiceBean extends AbstractRZMStatefulService implements AdminRoleService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    RoleManager roleManager;


    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public GuardedAdminRoleServiceBean(UserManager userManager, RoleManager roleManager) {
        super(userManager);
        CheckTool.checkNull(roleManager, "role maager");
        this.roleManager = roleManager;
    }

    public RoleVO getRole(long id) {
        isUserInRole();
        Role retRole = this.roleManager.getRole(id);
        CheckTool.checkNull(retRole, "no such role: " + id);
        return RoleConverter.convertRole(retRole);
    }

    public long createRole(RoleVO roleVO) {
        isUserInRole();
        CheckTool.checkNull(roleVO, "roleVO");
        Role newRole = RoleConverter.convertRole(roleVO);
        this.roleManager.createRole(newRole);
        return newRole.getObjId();
    }

    public void updateRole(RoleVO roleVO) {
        isUserInRole();
        CheckTool.checkNull(roleVO, "roleVO");
        Role updateRole = RoleConverter.convertRole(roleVO);
        this.roleManager.updateRole(updateRole);
    }

    public void deleteRole(long id) {
        isUserInRole();
        Role retRole = this.roleManager.getRole(id);
        CheckTool.checkNull(retRole, "no such role: " + id);
        this.roleManager.deleteRole(retRole);
    }

    public List<RoleVO> findRoles() {
        isUserInRole();
        List<RoleVO> rolesVO = new ArrayList<RoleVO>();
        for (Role role : this.roleManager.findRoles())
            rolesVO.add(RoleConverter.convertRole(role));
        return rolesVO;
    }

    public List<RoleVO> findRoles(RoleCriteria criteria) {
//        todo
        return null;
    }
}
