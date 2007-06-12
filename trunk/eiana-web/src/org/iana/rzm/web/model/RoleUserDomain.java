package org.iana.rzm.web.model;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: Jun 11, 2007
* Time: 10:42:44 AM
* To change this template use File | Settings | File Templates.
*/
public class RoleUserDomain extends UserDomain{
    private long roleId;

    public RoleUserDomain(long domainId, String domainName, String roleType, Date modified, long roleId) {
        super(domainId, domainName, roleType, modified);
        this.roleId = roleId;
    }

    public SystemRoleVOWrapper getRole(){

        SystemRoleVOWrapper.SystemType systemType = SystemRoleVOWrapper.SystemType.fromType(getRoleType());
        SystemRoleVOWrapper systemRoleVOWrapper = new SystemRoleVOWrapper(systemType.getServerType());
        systemRoleVOWrapper.setId(roleId);
        systemRoleVOWrapper.setName(getDomainName());
        systemRoleVOWrapper.setDomainAccess(true);
        return systemRoleVOWrapper;
    }
}
