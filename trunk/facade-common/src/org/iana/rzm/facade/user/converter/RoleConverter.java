package org.iana.rzm.facade.user.converter;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminUser;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.AdminRoleVO;
import org.iana.rzm.common.validators.CheckTool;

/**
 * org.iana.rzm.facade.user.converter.RoleConverter
 *
 * Performs conversion between role representation at DAO and facade level (for internal use inside a package)
 *
 * @author Marcin Zajaczkowski
 */
class RoleConverter {

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

        SystemRoleVO.SystemType typeVO;

        //maybe it's possible to do that better/faster/prettier...
        //todo could be done using map
        if (type == Role.Type.AC) {
            typeVO = SystemRoleVO.SystemType.AC;
        } else if (type == Role.Type.TC) {
            typeVO = SystemRoleVO.SystemType.TC;
        } else if (type == Role.Type.SO) {
            typeVO = SystemRoleVO.SystemType.SO;
        } else {
            throw new IllegalArgumentException("Unknown role type: " + type.toString());
        }

        return typeVO;
    }

    static RoleVO convertAdminRole(AdminUser admin) {

        CheckTool.checkNull(admin.getType(), "admin type");

        RoleVO adminRoleVO = new AdminRoleVO();

        if (admin.getType() == AdminUser.Type.IANA_STAFF) {
            adminRoleVO.setType(AdminRoleVO.AdminType.IANA);

        } else if (admin.getType() == AdminUser.Type.GOV_OVERSIGHT) {
            adminRoleVO.setType(AdminRoleVO.AdminType.GOV_OVERSIGHT);

        } else if (admin.getType() == AdminUser.Type.ZONE_PUBLISHER) {
            adminRoleVO.setType(AdminRoleVO.AdminType.ZONE_PUBLISHER);

        } else {
            throw new IllegalArgumentException("Unknown admin type: " + admin.getType().toString());
        }

        return adminRoleVO;
    }
}
