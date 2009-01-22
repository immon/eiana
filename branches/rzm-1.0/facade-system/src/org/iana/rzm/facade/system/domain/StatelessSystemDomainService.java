package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.vo.SimpleDomainVO;
import org.iana.rzm.facade.user.UserVO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessSystemDomainService {

    public IDomainVO getDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException;

    public IDomainVO getDomain(String name, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException;

    public List<SimpleDomainVO> findUserDomains(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<SimpleDomainVO> findUserDomains(String userName, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public void setAccessToDomain(long userId, long domainId, boolean access, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly, AuthenticatedUser authUser) throws AccessDeniedException;
    
}
