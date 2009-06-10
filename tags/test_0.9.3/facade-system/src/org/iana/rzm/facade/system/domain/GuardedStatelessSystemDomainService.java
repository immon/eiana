package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessSystemDomainService extends AbstractRZMStatelessService implements StatelessSystemDomainService {

    StatelessSystemDomainService statelessSystemDomainService;

    public GuardedStatelessSystemDomainService(UserManager userManager, StatelessSystemDomainService statelessSystemDomainService) {
        super(userManager);
        this.statelessSystemDomainService = statelessSystemDomainService;
    }

    private void isUserInRole(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        try {
            IDomainVO domain = statelessSystemDomainService.getDomain(id, authUser);
            isUserInIanaOrDomainRole(domain.getName(), authUser);
        } catch (NoObjectFoundException e) {
            throw new AccessDeniedException("no domain found " + id);
        }
    }

    public IDomainVO getDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        if (id < 1) throw new IllegalArgumentException("domain id value out of range");
        isUserInRole(id, authUser);
        return statelessSystemDomainService.getDomain(id, authUser);
    }

    public IDomainVO getDomain(String name, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        CheckTool.checkEmpty(name, "domain name");
        isUserInIanaOrDomainRole(name, authUser);
        return statelessSystemDomainService.getDomain(name, authUser);
    }

    public List<SimpleDomainVO> findUserDomains(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return findUserDomains(authUser.getUserName(), authUser);
    }

    public List<SimpleDomainVO> findUserDomains(String userName, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        CheckTool.checkEmpty(userName, "user name");
        RZMUser user = getRZMUser(authUser);
        if (user.isAdmin() || user.getLoginName().equals(userName))
            return statelessSystemDomainService.findUserDomains(userName, authUser);
        else
            throw new AccessDeniedException("invalid user");
    }

    public void setAccessToDomain(long userId, long domainId, boolean access, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isUserInRole(domainId, authUser);
        statelessSystemDomainService.setAccessToDomain(userId, domainId, access, authUser);
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly, AuthenticatedUser authUser) throws AccessDeniedException {
        isUserInIanaOrDomainRole(domainName, authUser);
        return statelessSystemDomainService.findDomainUsers(domainName, havingAccessOnly, authUser);
    }
}
