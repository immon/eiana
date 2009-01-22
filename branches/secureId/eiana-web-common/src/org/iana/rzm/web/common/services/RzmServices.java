package org.iana.rzm.web.common.services;

import org.iana.codevalues.*;
import org.iana.criteria.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.model.criteria.*;

import java.io.*;
import java.util.*;


public interface RzmServices extends Serializable {

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword) throws PasswordChangeException;

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(String domainName) throws NoObjectFoundException;

    public String getCountryName(String domainCode);

    public int getTransactionCount(Criterion criterion);

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length, SortOrder sort);

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain)
        throws NoObjectFoundException, AccessDeniedException,
               RadicalAlterationException, SharedNameServersCollisionException;

    public void withdrawnTransaction(long requestId)
        throws
        NoObjectFoundException,
        TransactionCannotBeWithdrawnException, InfrastructureException;


    public List<Value> getCountrys();

    public boolean isValidCountryCode(String code);
}
