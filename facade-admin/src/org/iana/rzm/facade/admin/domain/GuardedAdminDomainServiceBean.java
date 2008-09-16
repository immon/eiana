package org.iana.rzm.facade.admin.domain;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.exporter.DomainExporter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 *
 */

public class GuardedAdminDomainServiceBean extends AbstractFinderService<IDomainVO> implements AdminDomainService {

    StatelessAdminDomainService statelessAdminDomainService;

    public GuardedAdminDomainServiceBean(StatelessAdminDomainService statelessAdminDomainService) {
        this.statelessAdminDomainService = statelessAdminDomainService;
    }

    public IDomainVO getDomain(String domainName) throws AccessDeniedException {
        return statelessAdminDomainService.getDomain(domainName, getAuthenticatedUser());
    }

    public IDomainVO getDomain(long id) throws AccessDeniedException {
        return statelessAdminDomainService.getDomain(id, getAuthenticatedUser());
    }

    public void createDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException {
        statelessAdminDomainService.createDomain(domain, getAuthenticatedUser());
    }

    public void updateDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException {
        statelessAdminDomainService.updateDomain(domain, getAuthenticatedUser());
    }

    public void deleteDomain(String domainName) throws AccessDeniedException {
        statelessAdminDomainService.deleteDomain(domainName, getAuthenticatedUser());
    }

    public void deleteDomain(long id) throws AccessDeniedException {
        statelessAdminDomainService.deleteDomain(id, getAuthenticatedUser());
    }

    public List<IDomainVO> findDomains() throws AccessDeniedException {
        return statelessAdminDomainService.findDomains(getAuthenticatedUser());
    }

    public List<IDomainVO> findDomains(Criterion criteria) throws AccessDeniedException {
        return statelessAdminDomainService.findDomains(criteria, getAuthenticatedUser());
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return statelessAdminDomainService.count(criteria, getAuthenticatedUser());
    }

    public List<IDomainVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException {
        return statelessAdminDomainService.find(criteria, offset, limit, getAuthenticatedUser());
    }

    public IDomainVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return getDomain(id);
    }

    public List<IDomainVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessAdminDomainService.find(criteria, getAuthenticatedUser());
    }


    public void exportDomainsToXML() throws AccessDeniedException, InfrastructureException {
        statelessAdminDomainService.exportDomainsToXML(getAuthenticatedUser());
    }

    public String saveDomainsToXML() throws AccessDeniedException, InfrastructureException {
        return statelessAdminDomainService.saveDomainsToXML(getAuthenticatedUser());
    }
}
