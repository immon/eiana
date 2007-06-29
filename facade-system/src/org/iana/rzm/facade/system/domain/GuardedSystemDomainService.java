package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.*;

import java.util.HashSet;
import java.util.Iterator;
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

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        isUserInRole();
        if (id < 1) throw new IllegalArgumentException("Domain Id value out of range");
        DomainVO domainVO = (DomainVO) delegate.getDomain(id);
        if (!isInRole(domainVO.getName()))
            throw new AccessDeniedException("user is not in any role for this domain");
        return domainVO;
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        isUserInRole();
        CheckTool.checkEmpty(name, "Domain name");
        DomainVO domainVO = (DomainVO) delegate.getDomain(name);
        if (!isInRole(domainVO.getName()))
            throw new AccessDeniedException("user is not in any role for this domain");
        return domainVO;
    }

    public List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return findUserDomains(user.getUserName());
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        CheckTool.checkEmpty(userName, "user name");
        RZMUser user = getRZMUser();
        if (user.isAdmin() || user.getLoginName().equals(userName))
            return delegate.findUserDomains(userName);
        else
            throw new AccessDeniedException("invalid user");
    }

    public void setAccessToDomain(long userId, long domainId, boolean access) throws AccessDeniedException {
        isUserInRole();
        delegate.setAccessToDomain(userId, domainId, access);
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly) throws AccessDeniedException {
        isUserInRole();
        return delegate.findDomainUsers(domainName, havingAccessOnly);
    }

    public void setUser(AuthenticatedUser user) {
        CheckTool.checkNull(user, "authenticated user");
        delegate.setUser(user);
        super.setUser(user);
    }

    private boolean isInRole(String domainName) {
        RZMUser rzmUser = getRZMUser();
        if (rzmUser.isAdmin()) {
            return true;
        } else {
            List<Role> roles = rzmUser.getRoles();
            for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
                SystemRole systemRole = (SystemRole) iterator.next();
                if (systemRole.getName().equals(domainName))
                    return true;
            }
        }
        return false;
    }

}