package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.user.*;


public abstract class RoleVOWrapper extends ValueObject {

    public interface Type{
        public String serverName();
    }

    private RoleVO vo;

    protected RoleVOWrapper(RoleVO vo) {
        this.vo = vo;
    }

    protected RoleVO getVo(){
        return vo;
    }

    public abstract Type getType();



}
