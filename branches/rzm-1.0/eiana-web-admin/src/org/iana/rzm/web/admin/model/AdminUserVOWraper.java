package org.iana.rzm.web.admin.model;

import org.iana.rzm.facade.user.*;
import org.iana.rzm.web.common.model.*;

import java.util.*;

public class AdminUserVOWraper extends UserVOWrapper {

    public AdminUserVOWraper(UserVO vo){
        super(vo);
    }

    public AdminUserVOWraper() {
        this(new UserVO());        
    }

    public List<AdminRoleVOWrapper> getAdminRoles() {
        Set<RoleVO> roleVOs = super.getVo().getRoles();
        List<AdminRoleVOWrapper> roles = new ArrayList<AdminRoleVOWrapper>();
        for (RoleVO roleVO : roleVOs) {
            if (roleVO.isAdmin()) {
                roles.add(new AdminRoleVOWrapper((AdminRoleVO) roleVO));
            }
        }
        return roles;
    }

}
