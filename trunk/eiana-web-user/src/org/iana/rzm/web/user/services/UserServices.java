package org.iana.rzm.web.user.services;

import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.model.DomainVOWrapper;
import org.iana.rzm.web.common.model.TransactionVOWrapper;
import org.iana.rzm.web.common.model.UserDomain;
import org.iana.rzm.web.common.model.UserVOWrapper;
import org.iana.rzm.web.common.services.RzmServices;

import java.util.List;

public interface UserServices extends RzmServices {

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper, String submmiterEmail, boolean useRadicalChangesCheck)
        throws
        NoObjectFoundException,
        NoDomainModificationException,
        DNSTechnicalCheckExceptionWrapper,
        TransactionExistsException,
        NameServerChangeNotAllowedException,
        SharedNameServersCollisionException,
        RadicalAlterationException;

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain, String submitterEmail, boolean useRadicalChangesCheck)
            throws NoObjectFoundException, NoDomainModificationException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException, DNSTechnicalCheckExceptionWrapper;

    public List<UserDomain> getUserDomains();

    public List<UserVOWrapper>getUsersForDomain(String domainName);

    public UserVOWrapper getUser(long userId);

    public void acceptTransaction(long requestId, String token) throws NoObjectFoundException;

    public void rejectTransaction(long requestId, String token) throws NoObjectFoundException;

    public void setAccessToDomain(long domainId, long userId, boolean access);

}
