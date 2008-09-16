package org.iana.rzm.facade.admin.domain;

import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.converters.DomainToVOConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.domain.Domain;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminDomainService {

    public IDomainVO getDomain(String domainName, AuthenticatedUser authUser) throws AccessDeniedException;

    public IDomainVO getDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException;

    public void createDomain(IDomainVO domain, AuthenticatedUser authUser) throws InvalidCountryCodeException, AccessDeniedException;

    public void updateDomain(IDomainVO domain, AuthenticatedUser authUser) throws InvalidCountryCodeException, AccessDeniedException;

    public void deleteDomain(String domainName, AuthenticatedUser authUser) throws AccessDeniedException;

    public void deleteDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<IDomainVO> findDomains(AuthenticatedUser authUser) throws AccessDeniedException;

    public List<IDomainVO> findDomains(Criterion criteira, AuthenticatedUser authUser) throws AccessDeniedException;

    public void exportDomainsToXML(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public String saveDomainsToXML(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;


    int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    List<IDomainVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<IDomainVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException;
}
