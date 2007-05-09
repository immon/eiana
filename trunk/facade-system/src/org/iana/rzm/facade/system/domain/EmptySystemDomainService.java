package org.iana.rzm.facade.system.domain;

/**
 * @author Piotr Tkaczyk
 */

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.SystemDomainService;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;


public class EmptySystemDomainService implements SystemDomainService {

    public IDomainVO getDomain(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return null;
    }

    public IDomainVO getDomain(String name) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        return null;
    }

    public List<SimpleDomainVO> findUserDomains() throws AccessDeniedException, InfrastructureException {
        return new ArrayList<SimpleDomainVO>();
    }

    public List<SimpleDomainVO> findUserDomains(String userName) throws AccessDeniedException, InfrastructureException {
        return new ArrayList<SimpleDomainVO>();
    }

    public void setUser(AuthenticatedUser user) {
    }

    public void close() {
    }

    public AuthenticatedUser getUser() {
        return null;
    }
}
