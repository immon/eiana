package org.iana.rzm.facade.user.converter;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;

import java.util.HashMap;
import java.util.Map;

/**
 * org.iana.rzm.facade.user.converter.RoleConverter
 *
 * Performs conversion between role representation at DAO and facade level (for internal use inside a package)
 *
 * @author Marcin Zajaczkowski
 */
class RoleConverter {

    static Map<SystemRole.SystemType, SystemRoleVO.SystemType> systemRolesMap = new HashMap<SystemRole.SystemType, SystemRoleVO.SystemType>();
    static Map<AdminRole.AdminType, AdminRoleVO.AdminType> adminRolesMap = new HashMap<AdminRole.AdminType, AdminRoleVO.AdminType>();

    static {
        systemRolesMap.put(SystemRole.SystemType.AC, SystemRoleVO.SystemType.AC);
        systemRolesMap.put(SystemRole.SystemType.TC, SystemRoleVO.SystemType.TC);
        systemRolesMap.put(SystemRole.SystemType.SO, SystemRoleVO.SystemType.SO);

        adminRolesMap.put(AdminRole.AdminType.IANA, AdminRoleVO.AdminType.IANA);
        adminRolesMap.put(AdminRole.AdminType.GOV_OVERSIGHT, AdminRoleVO.AdminType.GOV_OVERSIGHT);
        adminRolesMap.put(AdminRole.AdminType.ZONE_PUBLISHER, AdminRoleVO.AdminType.ZONE_PUBLISHER);
    }

    static RoleVO convertRole(Role role) {

        return role.isAdmin() ? convertAdminRole((AdminRole) role)
                : convertSystemRole((SystemRole) role);
    }

    static RoleVO convertSystemRole(SystemRole systemRole) {

        CheckTool.checkNull(systemRole.getType(), "system role");

        SystemRoleVO systemRoleVO = new SystemRoleVO();

        SystemRoleVO.Type systemRoleVOtype = systemRolesMap.get(systemRole.getType());
        CheckTool.checkNull(systemRoleVOtype, "Unknown system role type: " + systemRole.getType().toString());
        systemRoleVO.setType(systemRoleVOtype);
        systemRoleVO.setName(systemRole.getName());
        systemRoleVO.setNotify(systemRole.isNotify());
        systemRoleVO.setAcceptFrom(systemRole.isAcceptFrom());
        systemRoleVO.setMustAccept(systemRole.isMustAccept());

        return systemRoleVO;
    }

    static RoleVO convertAdminRole(AdminRole adminRole) {

        CheckTool.checkNull(adminRole.getType(), "admin role");

        RoleVO adminRoleVO = new AdminRoleVO();

        AdminRoleVO.Type adminRoleVOType = adminRolesMap.get(adminRole.getType());
        CheckTool.checkNull(adminRoleVOType, "Unknown admin role type: " + adminRole.getType().toString());
        adminRoleVO.setType(adminRoleVOType);

        return adminRoleVO;
    }
}
