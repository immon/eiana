package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class GuardedSystemDomainService extends AbstractRZMStatefulService implements SystemDomainService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
        allowedRoles.add(new SystemRole(SystemRole.SystemType.AC));
        allowedRoles.add(new SystemRole(SystemRole.SystemType.TC));
        allowedRoles.add(new SystemRole(SystemRole.SystemType.SO));
    }

    private SystemDomainService delegate;

    public GuardedSystemDomainService(UserManager userManager, SystemDomainService delegate) {
        super(userManager);
        CheckTool.checkNull(delegate, "system domain service");
        this.delegate = delegate;
    }

    private void isUserInRole(long id) throws AccessDeniedException, InfrastructureException {
        try {
            IDomainVO domain = delegate.getDomain(id);
            isUserInIanaOrDomainRole(domain.getName());
        } catch (NoObjectFoundException e) {
            throw new AccessDeniedException("no domain found " + id);
        }
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        if (id < 1) throw new IllegalArgumentException("domain id value out of range");
        isUserInRole(id);
        return delegate.getDomain(id);
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        CheckTool.checkEmpty(name, "domain name");
        isUserInIanaOrDomainRole(name);
        return delegate.getDomain(name);
    }

    public List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException {
        return findUserDomains(user.getUserName());
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        CheckTool.checkEmpty(userName, "user name");
        RZMUser user = getRZMUser();
        if (user.isAdmin() || user.getLoginName().equals(userName))
            return delegate.findUserDomains(userName);
        else
            throw new AccessDeniedException("invalid user");
    }

    public void setAccessToDomain(long userId, long domainId, boolean access) throws AccessDeniedException, InfrastructureException {
        isUserInRole(domainId);
        delegate.setAccessToDomain(userId, domainId, access);
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly) throws AccessDeniedException {
        isUserInIanaOrDomainRole(domainName);
        return delegate.findDomainUsers(domainName, havingAccessOnly);
    }

    public void setUser(AuthenticatedUser user) {
        CheckTool.checkNull(user, "authenticated user");
        delegate.setUser(user);
        super.setUser(user);
    }

}