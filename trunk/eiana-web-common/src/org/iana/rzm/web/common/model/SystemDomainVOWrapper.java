package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.system.domain.vo.*;
import org.iana.rzm.facade.user.*;

import java.util.*;

public class SystemDomainVOWrapper extends DomainVOWrapper {

    public SystemDomainVOWrapper() {
        super(new DomainVO());
        getDomainVO().setSupportingOrg(new ContactVO());
        getDomainVO().setAdminContact(new ContactVO());
        getDomainVO().setTechContact(new ContactVO());
        getDomainVO().setStatus(IDomainVO.Status.NEW);
        getDomainVO().setNameServers(new ArrayList<HostVO>());
        getDomainVO().setState(IDomainVO.State.NO_ACTIVITY);
    }

    public SystemDomainVOWrapper(IDomainVO vo) {
        super(vo);
    }

    public List<SystemRoleVOWrapper> getRoles() {
        Set<RoleVO.Type> roles = getDomainVO().getRoles();
        List<SystemRoleVOWrapper>wrappers = new ArrayList<SystemRoleVOWrapper>();

        if(roles == null){
            return wrappers;
        }

        for (RoleVO.Type role : roles) {
            wrappers.add(new SystemRoleVOWrapper(role));
        }
        return wrappers;
    }

}
