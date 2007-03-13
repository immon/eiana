package org.iana.rzm.facade.system.domain;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.RoleVO;
import org.iana.rzm.facade.user.SystemRoleVO;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.IDomainVO;
import org.iana.rzm.facade.system.DomainVO;
import org.iana.rzm.facade.system.SimpleDomainVO;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;

import java.util.List;
import java.util.Set;
import java.util.Iterator;

public class GuardedSystemDomainService implements SystemDomainService {

    private SystemDomainService delegate;
    private AuthenticatedUser user;

    public GuardedSystemDomainService(SystemDomainService delegate) {
        this.delegate = delegate;
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        if (this.user == null) throw new AccessDeniedException("AuthenticatedUser is null");
        if (id < 1) throw new IllegalArgumentException ("Domain Id value out of range");
        DomainVO domainVO = (DomainVO) delegate.getDomain(id);
        if (!isInRole(domainVO.getName()))
            throw new AccessDeniedException("user is not in any role for this domain");
        return domainVO;
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        if (this.user == null) throw new AccessDeniedException("AuthenticatedUser is null");
        CheckTool.checkEmpty(name, "Domain name");
        DomainVO domainVO = (DomainVO) delegate.getDomain(name);
        if (!isInRole(domainVO.getName()))
            throw new AccessDeniedException("user is not in any role for this domain");
        return domainVO;
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        if (this.user == null) throw new AccessDeniedException("AuthenticatedUser is null");
        CheckTool.checkEmpty(userName, "user name");
        if(user.isAdmin() || user.getUserName().equals(userName))
            return delegate.findUserDomains(userName);
        else
            throw new AccessDeniedException("invalid user");
    }

    public void setUser(AuthenticatedUser user) {
        CheckTool.checkNull(user, "Authenticated User");
        delegate.setUser(user);
        this.user = user;
    }

    public void close() {
        delegate.close();
    }

    private boolean isInRole(String domainName) {
        if(user.isAdmin()) {
            return true;
        } else {
            Set<RoleVO> roles = user.getRoles();
            for(Iterator iterator = roles.iterator(); iterator.hasNext();) {
                SystemRoleVO systemRole = (SystemRoleVO) iterator.next();
                if(systemRole.getName().equals(domainName))
                    return true;
            }
        }
        return false;
    }
}