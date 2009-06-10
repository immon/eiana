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
import java.util.Set;
import java.util.HashSet;

/**
 * org.iana.rzm.facade.user.converter.RoleConverter
 *
 * Performs conversion between role representation at DAO and facade level (for internal use inside a package)
 *
 * @author Marcin Zajaczkowski
 */
public class RoleConverter {

    public static Map<SystemRole.SystemType, SystemRoleVO.SystemType> systemRolesMap = new HashMap<SystemRole.SystemType, SystemRoleVO.SystemType>();
    private static Map<AdminRole.AdminType, AdminRoleVO.AdminType> adminRolesMap = new HashMap<AdminRole.AdminType, AdminRoleVO.AdminType>();

    static Map<SystemRoleVO.SystemType, SystemRole.SystemType> systemRolesVOMap = new HashMap<SystemRoleVO.SystemType, SystemRole.SystemType>();
    static Map<AdminRoleVO.AdminType, AdminRole.AdminType> adminRolesVOMap = new HashMap<AdminRoleVO.AdminType, AdminRole.AdminType>();

    static {
        systemRolesMap.put(SystemRole.SystemType.AC, SystemRoleVO.SystemType.AC);
        systemRolesMap.put(SystemRole.SystemType.TC, SystemRoleVO.SystemType.TC);
        systemRolesMap.put(SystemRole.SystemType.SO, SystemRoleVO.SystemType.SO);

        adminRolesMap.put(AdminRole.AdminType.IANA, AdminRoleVO.AdminType.IANA);
        adminRolesMap.put(AdminRole.AdminType.GOV_OVERSIGHT, AdminRoleVO.AdminType.GOV_OVERSIGHT);
        adminRolesMap.put(AdminRole.AdminType.ZONE_PUBLISHER, AdminRoleVO.AdminType.ZONE_PUBLISHER);

        systemRolesVOMap.put(SystemRoleVO.SystemType.AC, SystemRole.SystemType.AC);
        systemRolesVOMap.put(SystemRoleVO.SystemType.TC, SystemRole.SystemType.TC);
        systemRolesVOMap.put(SystemRoleVO.SystemType.SO, SystemRole.SystemType.SO);

        adminRolesVOMap.put(AdminRoleVO.AdminType.IANA, AdminRole.AdminType.IANA);
        adminRolesVOMap.put(AdminRoleVO.AdminType.GOV_OVERSIGHT, AdminRole.AdminType.GOV_OVERSIGHT);
        adminRolesVOMap.put(AdminRoleVO.AdminType.ZONE_PUBLISHER, AdminRole.AdminType.ZONE_PUBLISHER);
    }

    public static RoleVO convertRole(Role role) {

        return role.isAdmin() ? convertAdminRole((AdminRole) role)
                : convertSystemRole((SystemRole) role);
    }

    public static Role convertRole(RoleVO roleVO) {
        return roleVO.isAdmin() ? convertAdminRole((AdminRoleVO) roleVO)
                : convertSystemRole((SystemRoleVO) roleVO);
    }

    private static RoleVO convertSystemRole(SystemRole systemRole) {

        CheckTool.checkNull(systemRole.getType(), "system role");

        SystemRoleVO systemRoleVO = new SystemRoleVO();

        SystemRoleVO.Type systemRoleVOtype = systemRolesMap.get(systemRole.getType());
        CheckTool.checkNull(systemRoleVOtype, "Unknown system role type: " + systemRole.getType().toString());
        systemRoleVO.setType(systemRoleVOtype);
        systemRoleVO.setName(systemRole.getName());
        systemRoleVO.setNotify(systemRole.isNotify());
        systemRoleVO.setAcceptFrom(systemRole.isAcceptFrom());
        systemRoleVO.setMustAccept(systemRole.isMustAccept());
        systemRoleVO.setAccessToDomain(systemRole.isAccessToDomain());

        systemRoleVO.setObjId(systemRole.getObjId());

        return systemRoleVO;
    }

    private static Role convertSystemRole(SystemRoleVO systemRoleVO) {
        CheckTool.checkNull(systemRoleVO.getType(), "system roleVO");

        SystemRole systemRole = new SystemRole();

        SystemRole.Type systemRoleType = systemRolesVOMap.get(systemRoleVO.getType());
        CheckTool.checkNull(systemRoleType, "Unknown system roleVO type: " + systemRoleVO.getType().toString());
        systemRole.setType(systemRoleType);
        systemRole.setName(systemRoleVO.getName());
        systemRole.setNotify(systemRoleVO.isNotify());
        systemRole.setAcceptFrom(systemRoleVO.isAcceptFrom());
        systemRole.setMustAccept(systemRoleVO.isMustAccept());
        systemRole.setAccessToDomain(systemRoleVO.isAccessToDomain());

        systemRole.setObjId(systemRoleVO.getObjId());

        return systemRole;
    }

    private static RoleVO convertAdminRole(AdminRole adminRole) {

        CheckTool.checkNull(adminRole.getType(), "admin role");

        RoleVO adminRoleVO = new AdminRoleVO();

        AdminRoleVO.Type adminRoleVOType = adminRolesMap.get(adminRole.getType());
        CheckTool.checkNull(adminRoleVOType, "Unknown admin role type: " + adminRole.getType().toString());
        adminRoleVO.setType(adminRoleVOType);

        adminRoleVO.setObjId(adminRole.getObjId());

        return adminRoleVO;
    }

    private static Role convertAdminRole(AdminRoleVO adminRoleVO) {
        CheckTool.checkNull(adminRoleVO.getType(), "admin roleVO");

        AdminRole adminRole = new AdminRole();

        AdminRole.Type adminRoleType = adminRolesVOMap.get(adminRoleVO.getType());
        CheckTool.checkNull(adminRoleType, "Unknown admin roleVO type: " + adminRoleVO.getType().toString());
        adminRole.setType(adminRoleType);

        adminRole.setObjId(adminRoleVO.getObjId());

        return adminRole;
    }

    public static Set<SystemRoleVO.SystemType> convertTypes(Set<SystemRole.SystemType> types) {
        Set<SystemRoleVO.SystemType> ret = new HashSet<SystemRoleVO.SystemType>();
        if (types != null) {
            for (SystemRole.SystemType type : types) ret.add(systemRolesMap.get(type));
        }
        return ret;
    }
}
