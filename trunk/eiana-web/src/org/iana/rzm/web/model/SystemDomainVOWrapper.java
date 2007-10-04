package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.ContactVO;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.user.*;

import java.util.*;

public class SystemDomainVOWrapper extends DomainVOWrapper {

    public SystemDomainVOWrapper() {
        super(new DomainVO());
        getDomainVO().setSupportingOrg(new ContactVO());
        getDomainVO().setAdminContact(new ContactVO());
        getDomainVO().setTechContact(new ContactVO());
        getDomainVO().setStatus(IDomainVO.Status.NEW);
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
