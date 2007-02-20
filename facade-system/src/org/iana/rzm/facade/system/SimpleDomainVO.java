package org.iana.rzm.facade.system;

import org.iana.rzm.facade.common.TrackedObjectVO;
import org.iana.rzm.facade.user.RoleVO;

import javax.management.relation.Role;
import java.util.Set;

/**
 * A simplified version of DomainVO used with lists of domains. 
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleDomainVO extends TrackedObjectVO {

    private String name;
    private Set<RoleVO.Type> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleVO.Type> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleVO.Type> roles) {
        this.roles = roles;
    }
}
