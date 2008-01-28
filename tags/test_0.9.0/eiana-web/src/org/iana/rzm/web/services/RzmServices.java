package org.iana.rzm.web.services;

import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;

import java.io.*;
import java.util.*;

public interface RzmServices extends Serializable {

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword) throws PasswordChangeException;

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(String domainName) throws NoObjectFoundException;

    public String getCountryName(String domainCode);

    public int getTransactionCount(Criterion criterion);

    public List<TransactionVOWrapper>getTransactions(Criterion criterion, int offset, int length, SortOrder sort);

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain) throws NoObjectFoundException, AccessDeniedException;

    public void withdrawnTransaction(long requestId)
        throws
        FacadeTransactionException,
        NoSuchStateException,
        NoObjectFoundException,
        StateUnreachableException,
        TransactionCannotBeWithdrawnException, InfrastructureException;
}