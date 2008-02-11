package org.iana.rzm.facade.admin.domain;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.services.FinderService;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminDomainService extends RZMStatefulService, FinderService<IDomainVO> {

    public IDomainVO getDomain(String domainName) throws AccessDeniedException;

    public IDomainVO getDomain(long id) throws AccessDeniedException;

    public void createDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException;

    public void updateDomain(IDomainVO domain) throws InvalidCountryCodeException, AccessDeniedException;

    public void deleteDomain(String domainName) throws AccessDeniedException;

    public void deleteDomain(long id) throws AccessDeniedException;

    public List<IDomainVO> findDomains() throws AccessDeniedException;

    public List<IDomainVO> findDomains(Criterion criteira) throws AccessDeniedException;

    public void exportDomainsToXML() throws AccessDeniedException, InfrastructureException;

}
