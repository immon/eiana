package org.iana.rzm.web.model;

import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.user.RoleVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SystemDomainVOWrapper extends DomainVOWrapper {

    public SystemDomainVOWrapper(IDomainVO vo) {
        super(vo);
    }

    public List<SystemRoleVOWrapper> getRoles() {
        Set<RoleVO.Type> roles = getDomainVO().getRoles();
        List<SystemRoleVOWrapper>wrappers = new ArrayList<SystemRoleVOWrapper>();
        for (RoleVO.Type role : roles) {
            wrappers.add(new SystemRoleVOWrapper(role));
        }
        return wrappers;
    }
}
