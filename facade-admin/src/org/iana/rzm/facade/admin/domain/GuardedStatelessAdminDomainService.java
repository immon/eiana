package org.iana.rzm.facade.admin.domain;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessAdminDomainService extends AbstractRZMStatelessService implements StatelessAdminDomainService {

    StatelessAdminDomainService statelessAdminDomainService;

    public GuardedStatelessAdminDomainService(UserManager userManager, StatelessAdminDomainService statelessAdminDomainService) {
        super(userManager);
        this.statelessAdminDomainService = statelessAdminDomainService;
    }

    public IDomainVO getDomain(String domainName, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminDomainService.getDomain(domainName, authUser);
    }

    public IDomainVO getDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminDomainService.getDomain(id, authUser);
    }

    public void createDomain(IDomainVO domain, AuthenticatedUser authUser) throws InvalidCountryCodeException, AccessDeniedException {
        isIana(authUser);
        statelessAdminDomainService.createDomain(domain, authUser);
    }

    public void updateDomain(IDomainVO domain, AuthenticatedUser authUser) throws InvalidCountryCodeException, AccessDeniedException {
        isIana(authUser);
        statelessAdminDomainService.updateDomain(domain, authUser);
    }

    public void deleteDomain(String domainName, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminDomainService.deleteDomain(domainName, authUser);
    }

    public void deleteDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        statelessAdminDomainService.deleteDomain(id, authUser);
    }

    public List<IDomainVO> findDomains(AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminDomainService.findDomains(authUser);
    }

    public List<IDomainVO> findDomains(Criterion criteira, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminDomainService.findDomains(criteira, authUser);
    }

    public void exportDomainsToXML(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        statelessAdminDomainService.exportDomainsToXML(authUser);
    }

    public String saveDomainsToXML(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminDomainService.saveDomainsToXML(authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminDomainService.count(criteria, authUser);
    }

    public List<IDomainVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminDomainService.find(criteria, offset, limit, authUser);
    }

    public List<IDomainVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminDomainService.find(criteria, authUser);
    }
}
