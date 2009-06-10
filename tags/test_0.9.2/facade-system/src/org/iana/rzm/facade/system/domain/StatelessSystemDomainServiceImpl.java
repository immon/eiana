package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.common.validators.CheckTool;
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
public class StatelessSystemDomainServiceImpl implements StatelessSystemDomainService {


    private StatelessSystemDomainService delegate;

    public StatelessSystemDomainServiceImpl(StatelessSystemDomainService delegate) {
        CheckTool.checkNull(delegate, "system domain service");
        this.delegate = delegate;
    }

    public IDomainVO getDomain(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        return delegate.getDomain(id, authUser);
    }

    public IDomainVO getDomain(String name, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException, InvalidEmailException {
        return delegate.getDomain(name, authUser);
    }

    public List<SimpleDomainVO> findUserDomains(AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return findUserDomains(authUser.getUserName(), authUser);
    }

    public List<SimpleDomainVO> findUserDomains(String userName, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return delegate.findUserDomains(userName, authUser);
    }

    public void setAccessToDomain(long userId, long domainId, boolean access, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        delegate.setAccessToDomain(userId, domainId, access, authUser);
    }

    public List<UserVO> findDomainUsers(String domainName, boolean havingAccessOnly, AuthenticatedUser authUser) throws AccessDeniedException {
        return delegate.findDomainUsers(domainName, havingAccessOnly, authUser);
    }

}
