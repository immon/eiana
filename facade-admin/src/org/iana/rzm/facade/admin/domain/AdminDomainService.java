package org.iana.rzm.facade.admin.domain;

import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.services.*;
import org.iana.rzm.facade.system.domain.vo.*;

import java.util.*;

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

    public String saveDomainsToXML() throws AccessDeniedException, InfrastructureException;

}
