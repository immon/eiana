package org.iana.rzm.web.user.services;

import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.DNSTechnicalCheckExceptionWrapper;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.services.*;

import java.util.*;

public interface UserServices extends RzmServices {

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper, String submmiterEmail)
        throws
        NoObjectFoundException,
        NoDomainModificationException,
        DNSTechnicalCheckExceptionWrapper,
        TransactionExistsException,
        NameServerChangeNotAllowedException,
        SharedNameServersCollisionException,
        RadicalAlterationException;

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain, String submitterEmail)
        throws NoObjectFoundException, NoDomainModificationException, TransactionExistsException, NameServerChangeNotAllowedException,SharedNameServersCollisionException,RadicalAlterationException;

    public List<UserDomain> getUserDomains();

    public List<UserVOWrapper>getUsersForDomain(String domainName);

    public UserVOWrapper getUser(long userId);

    public void acceptTransaction(long requestId, String token) throws NoObjectFoundException;

    public void rejectTransaction(long requestId, String token) throws NoObjectFoundException;

    public void setAccessToDomain(long domainId, long userId, boolean access);


}
