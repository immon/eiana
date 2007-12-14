package org.iana.rzm.web.model;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
* User: simon
* Date: Jun 11, 2007
* Time: 10:42:44 AM
* To change this template use File | Settings | File Templates.
*/
public class RoleUserDomain extends UserDomain{
    private long roleId;
    private boolean notify;
    private boolean acceptFrom;
    private boolean mustAccept  ;

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
        systemRoleVOWrapper.setAcceptFrom(acceptFrom);
        systemRoleVOWrapper.setNotify(notify);
        systemRoleVOWrapper.setMustAccept(mustAccept);
        return systemRoleVOWrapper;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }


    public void setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    public void setAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }


    public boolean isNotify() {
        return notify;
    }

    public boolean isAcceptFrom() {
        return acceptFrom;
    }

    public boolean isMustAccept() {
        return mustAccept;
    }
}
