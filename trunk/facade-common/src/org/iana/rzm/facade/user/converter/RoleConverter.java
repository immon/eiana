package org.iana.rzm.facade.user.converter;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminUser;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Map;
import java.util.HashMap;

/**
 * org.iana.rzm.facade.user.converter.RoleConverter
 *
 * Performs conversion between role representation at DAO and facade level (for internal use inside a package)
 *
 * @author Marcin Zajaczkowski
 */
class RoleConverter {

    static Map<Role.Type, SystemRoleVO.SystemType> systemRolesMap = new HashMap<Role.Type, SystemRoleVO.SystemType>();
    static Map<AdminUser.Type, AdminRoleVO.AdminType> adminRolesMap = new HashMap<AdminUser.Type, AdminRoleVO.AdminType>();

    static {
        systemRolesMap.put(Role.Type.AC, SystemRoleVO.SystemType.AC);
        systemRolesMap.put(Role.Type.TC, SystemRoleVO.SystemType.TC);
        systemRolesMap.put(Role.Type.SO, SystemRoleVO.SystemType.SO);

        adminRolesMap.put(AdminUser.Type.IANA_STAFF, AdminRoleVO.AdminType.IANA);
        adminRolesMap.put(AdminUser.Type.GOV_OVERSIGHT, AdminRoleVO.AdminType.GOV_OVERSIGHT);
        adminRolesMap.put(AdminUser.Type.ZONE_PUBLISHER, AdminRoleVO.AdminType.ZONE_PUBLISHER);
    }

    static RoleVO convertRole(Role role) {
        
        SystemRoleVO systemRoleVO = new SystemRoleVO();

        systemRoleVO.setName(role.getName());
        systemRoleVO.setType(convertType(role.getType()));
        systemRoleVO.setNotify(role.isNotify());
        systemRoleVO.setAcceptFrom(role.isAcceptFrom());
        systemRoleVO.setMustAccept(role.isMustAccept());

        return systemRoleVO;
    }

    private static SystemRoleVO.SystemType convertType(Role.Type type) {

        SystemRoleVO.SystemType typeVO = systemRolesMap.get(type);
        CheckTool.checkNull(typeVO, "Unknown role type: " + type.toString());

        return typeVO;
    }

    static RoleVO convertAdminRole(AdminUser admin) {

        CheckTool.checkNull(admin.getType(), "admin type");

        RoleVO adminRoleVO = new AdminRoleVO();

        AdminRoleVO.Type adminRole = adminRolesMap.get(admin.getType());
        CheckTool.checkNull(adminRole, "Unknown admin type: " + admin.getType().toString());
        adminRoleVO.setType(adminRole);

        return adminRoleVO;
    }
}
