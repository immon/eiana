package org.iana.rzm.facade.system.domain;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.converter.ToVOConverter;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
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
        domainVO.setRoles(getRoleTypeByDomainName(user, domainVO.getName()));
        return domainVO;
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        Domain domain = domainManager.get(name);
        if (domain == null) throw new NoObjectFoundException(name);
        DomainVO domainVO = ToVOConverter.toDomainVO(domain);
        RZMUser user = userManager.get(this.user.getUserName());
        domainVO.setRoles(getRoleTypeByDomainName(user, domainVO.getName()));
        return domainVO;
    }

    public List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException {
        if (user != null) throw new AccessDeniedException("null authenticated user");
        return findUserDomains(user.getUserName());
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        List<SimpleDomainVO> list = new ArrayList<SimpleDomainVO>();
        RZMUser user = userManager.get(userName);

        Set<String> roleNames = new HashSet<String>();
        for (Role role : user.getRoles())
            if (!role.isAdmin()) {
                SystemRole sr = (SystemRole) role;
                roleNames.add(sr.getName());
            }

        if (roleNames.isEmpty())
            throw new AccessDeniedException("not system user");

        for (String roleName : roleNames) {
            Domain domain = domainManager.get(roleName);
            if (domain != null) {
                SimpleDomainVO simpleDomainVO = ToVOConverter.toSimpleDomainVO(domain);
                simpleDomainVO.setRoles(getRoleTypeByDomainName(user, simpleDomainVO.getName()));
                list.add(simpleDomainVO);
            }
        }

        return list;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        //todo
    }

    private Set<RoleVO.Type> getRoleTypeByDomainName(RZMUser user, String domainName) {
        if ((user.getRoles() == null) || (domainName == null)) return null;
        Set<RoleVO.Type> roleTypeVOSet = new HashSet<RoleVO.Type>();
        for (Role role : user.getRoles())
            if (!role.isAdmin()) {
                SystemRole sr = (SystemRole) role;
                if (sr.getName().equals(domainName))
                    roleTypeVOSet.add(ToVOConverter.toRoleTypeVO(sr.getType()));
            }
        return roleTypeVOSet;
    }
}
