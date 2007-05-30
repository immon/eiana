package org.iana.rzm.web.model;

import org.iana.rzm.facade.user.AdminRoleVO;

public class AdminRoleVOWrapper extends RoleVOWrapper {

    public AdminRoleVOWrapper(AdminRoleVO.Type type) {
        super(new AdminRoleVO());
        getVo().setType(type);
    }

    public AdminRoleVOWrapper(AdminRoleVO vo) {
        super(vo);
    }

    public Type getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
