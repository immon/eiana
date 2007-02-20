package org.iana.rzm.facade.system;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.common.RZMStatefulService;

import java.util.List;

/**
 * This is a stateful domain facade service providing the basic operations on domains for the RZM end-users.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface SystemDomainService extends RZMStatefulService {

    /**
     * Returns a domain object with a given id.
     *
     * @param id
     * @return
     * @throws org.iana.rzm.facade.auth.AccessDeniedException - user is not authenticated or has no permission to access a given domain object.
     * @throws InfrastructureException
     */
    IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException;

    /**
     * Returns a list of the domain objects for which a specified user is in one of the roles: AC, TC, SO.
     *
     * @return a list of the domains managed/administered by a given user, an empty list if no domains found.
     * @throws org.iana.rzm.facade.auth.AccessDeniedException - user is not authenticated.
     * @throws InfrastructureException
     */
    List<SimpleDomainVO> findDomains() throws AccessDeniedException, InfrastructureException;
}
