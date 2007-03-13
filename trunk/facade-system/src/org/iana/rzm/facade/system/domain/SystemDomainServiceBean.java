package org.iana.rzm.facade.system.domain;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.IDomainVO;
import org.iana.rzm.facade.system.DomainVO;
import org.iana.rzm.facade.system.ToVOConverter;
import org.iana.rzm.facade.system.SimpleDomainVO;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidNameException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.DomainException;
import org.iana.rzm.user.*;

import java.util.*;

public class SystemDomainServiceBean implements SystemDomainService {

    private DomainManager domainManager;
    private UserManager userManager;
    private AuthenticatedUser user;

    public SystemDomainServiceBean(DomainManager domainManager, UserManager userManager) {
        this.domainManager = domainManager;
        this.userManager = userManager;
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        Domain domain = domainManager.get(id);
        if (domain == null) throw new NoObjectFoundException(id);
        DomainVO domainVO = ToVOConverter.toDomainVO(domain);
        RZMUser user = userManager.get(this.user.getUserName());
        if (user instanceof SystemUser)
            domainVO.setRoles(getRoleTypeByDomainName(((SystemUser) user).getRoles(), domainVO.getName()));
        return domainVO;
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        Domain domain = domainManager.get(name);
        if (domain == null) throw new NoObjectFoundException(name);
        DomainVO domainVO = ToVOConverter.toDomainVO(domain);
        RZMUser user = userManager.get(this.user.getUserName());
        if (user instanceof SystemUser)
            domainVO.setRoles(getRoleTypeByDomainName(((SystemUser) user).getRoles(), domainVO.getName()));
        return domainVO;
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        List<SimpleDomainVO> list = new ArrayList<SimpleDomainVO>();
        RZMUser user = userManager.get(userName);
        if(user instanceof SystemUser) {
            List<Role> roles = ((SystemUser) user).getRoles();
            Set<String> roleNames = new HashSet<String>();
            for(Iterator iterator = roles.iterator(); iterator.hasNext();)
                roleNames.add(((Role) iterator.next()).getName());

            for(Iterator iterator = roleNames.iterator(); iterator.hasNext();) {
                String roleName = (String) iterator.next();
                Domain domain = domainManager.get(roleName);
                if (domain != null) {
                    SimpleDomainVO simpleDomainVO = ToVOConverter.toSimpleDomainVO(domain);
                    simpleDomainVO.setRoles(getRoleTypeByDomainName(roles, simpleDomainVO.getName()));
                    list.add(simpleDomainVO);
                }
            }
        } else
            throw new AccessDeniedException("not system user");
        return list;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        //todo
    }

    private Set<RoleVO.Type> getRoleTypeByDomainName(List<Role> fromRoles, String domainName) {
        if ((fromRoles == null) || (domainName == null)) return null;
        Set<RoleVO.Type> roleTypeVOSet = new HashSet<RoleVO.Type>();
        for(Role role : fromRoles)
        if (role.getName().equals(domainName))
            roleTypeVOSet.add(ToVOConverter.toRoleTypeVO(role.getType()));
        return roleTypeVOSet;
    }
}
