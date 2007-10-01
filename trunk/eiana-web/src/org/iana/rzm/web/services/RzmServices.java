package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public interface RzmServices {

    public void changePassword(long userId, String newPassword);

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(String domainName) throws NoObjectFoundException;

    public String getCountryName(String domainCode);

    public int getTransactionCount(Criterion criterion);

    public List<TransactionVOWrapper>getTransactions(Criterion criterion, int offset, int length);

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain) throws NoObjectFoundException, AccessDeniedException;

}