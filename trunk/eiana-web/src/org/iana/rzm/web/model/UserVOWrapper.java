package org.iana.rzm.web.model;

import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.web.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserVOWrapper extends ValueObject implements PaginatedEntity {

    private UserVO vo;

    public UserVOWrapper(UserVO vo){
        this.vo = vo;
    }

    public long getId(){
        return vo.getObjId();
    }

    public String getUserName(){
        return vo.getUserName();
    }

      public String getModified(){
        Date d = vo.getModified();
        return d == null ?
                DateUtil.formatDate(vo.getCreated()):
                DateUtil.formatDate(d);
    }

    public List<String> listSystemUserRoles(){
        List<String> rolesNames = new ArrayList<String>();
        List<SystemRoleVOWrapper> list = getSystemRoles();
        for (SystemRoleVOWrapper systemRoleVOWrapper : list) {
            rolesNames.add(systemRoleVOWrapper.getTypeAsString());
        }
        return rolesNames;
    }

    public boolean isAc(){
        return isInRole(SystemRoleVOWrapper.SystemType.AC);
    }

    public boolean isTc(){
        return isInRole(SystemRoleVOWrapper.SystemType.TC);
    }



    public List<SystemRoleVOWrapper> getSystemRoles(){
        Set<RoleVO> roleVOs = vo.getRoles();
        List<SystemRoleVOWrapper> roles = new ArrayList<SystemRoleVOWrapper>();
        for (RoleVO roleVO : roleVOs) {
            if(roleVO.isAdmin()){
                continue;
            }
            roles.add(new SystemRoleVOWrapper((SystemRoleVO) roleVO));
        }
        return roles;
    }


    public boolean isAccessEnabled(String domainName) {
        return vo.hasAccessToDomain(domainName);
    }

    private boolean isInRole(SystemRoleVOWrapper.SystemType type) {
        List<SystemRoleVOWrapper> list = getSystemRoles();
        for (SystemRoleVOWrapper role : list) {
            if(role.getType().equals(type)){
                return true;
            }
        }

        return false;
    }
}
