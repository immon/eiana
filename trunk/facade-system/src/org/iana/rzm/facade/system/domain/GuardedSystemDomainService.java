package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.user.UserVO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class GuardedSystemDomainService extends AbstractRZMStatefulService implements SystemDomainService {

    private StatelessSystemDomainService statelessSystemDomainService;

    public GuardedSystemDomainService(StatelessSystemDomainService statelessSystemDomainService) {
        this.statelessSystemDomainService = statelessSystemDomainService;
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        return statelessSystemDomainService.getDomain(id, getAuthenticatedUser());
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        return statelessSystemDomainService.getDomain(name, getAuthenticatedUser());
    }

    public List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException {
        return statelessSystemDomainService.findUserDomains(getAuthenticatedUser());
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        return statelessSystemDomainService.findUserDomains(userName, getAuthenticatedUser());
    }

    public void setAccessToDomain(long userId, long domainId, boolean access) throws AccessDeniedException, InfrastructureException {
        statelessSystemDomainService.setAccessToDomain(userId, domainId, access, getAuthenticatedUser());
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly) throws AccessDeniedException {
        return statelessSystemDomainService.findDomainUsers(domainName, havingAccessOnly, getAuthenticatedUser());
    }
}