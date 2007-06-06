package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.user.UserVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
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

    public void setAccessToDomain(long userId, long domainId, boolean access) {
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly) {
        return new ArrayList<UserVO>();
    }

    public void setUser(AuthenticatedUser user) {
    }

    public void close() {
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return null;
    }

    public UserVO getUser() {
        return null;
    }
}
