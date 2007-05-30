package org.iana.rzm.web.services.user;

import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.RzmServices;

import java.util.Collection;
import java.util.List;

public interface UserServices extends RzmServices {

    public Collection<TransactionVOWrapper> getOpenTransactionsForDomin(String domainName) throws NoObjectFoundException;

    public TransactionVOWrapper createTransaction(DomainVOWrapper domainVOWrapper) throws NoObjectFoundException;

    public List<TransactionVOWrapper> createTransactions(DomainVOWrapper domain) throws NoObjectFoundException;

    public List<UserDomain> getUserDomains();

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper modifiedDomain) throws NoObjectFoundException;

    public List<UserVOWrapper>getUsersForDomain(long domainId);

}
