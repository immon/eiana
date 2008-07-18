package org.iana.rzm.web.services.user;

import org.iana.codevalues.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;

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

    public UserVOWrapper getUser();

    public void acceptTransaction(long requestId, String token) throws NoObjectFoundException;

    public void rejectTransaction(long requestId, String token) throws NoObjectFoundException;

    public void setAccessToDomain(long domainId, long userId, boolean access);

    public boolean isValidCountryCode(String code);

    public List<Value> getCountrys();
}
