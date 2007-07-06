package org.iana.rzm.web.services.user;

import org.iana.codevalues.Value;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.RzmServices;

import java.util.Collection;
import java.util.List;

public interface UserServices extends RzmServices {

    public Collection<TransactionVOWrapper> getOpenTransactionsForDomin(String domainName) throws NoObjectFoundException;

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper, String submmiterEmail) throws NoObjectFoundException, NoDomainModificationException;

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain, String submitterEmail) throws NoObjectFoundException, NoDomainModificationException;

    public List<UserDomain> getUserDomains();

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper modifiedDomain) throws NoObjectFoundException;

    public List<UserVOWrapper>getUsersForDomain(String domainName);

    public UserVOWrapper getUser();

    public void acceptTransaction(long requestId, String token) throws NoObjectFoundException;

    public void rejectTransaction(long requestId, String token) throws NoObjectFoundException;

    public void setAccessToDomain(long domainId, long userId, boolean access);

    public boolean isValidCountryCode(String code);

    public List<Value> getCountrys();
}
