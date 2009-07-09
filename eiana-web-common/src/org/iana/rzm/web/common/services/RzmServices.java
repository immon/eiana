package org.iana.rzm.web.common.services;

import org.iana.codevalues.Value;
import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.passwd.PasswordChangeException;
import org.iana.rzm.facade.system.trans.RadicalAlterationException;
import org.iana.rzm.facade.system.trans.SharedNameServersCollisionException;
import org.iana.rzm.facade.system.trans.TransactionCannotBeWithdrawnException;
import org.iana.rzm.web.common.model.DomainVOWrapper;
import org.iana.rzm.web.common.model.SystemDomainVOWrapper;
import org.iana.rzm.web.common.model.TransactionActionsVOWrapper;
import org.iana.rzm.web.common.model.TransactionVOWrapper;
import org.iana.rzm.web.common.model.criteria.SortOrder;

import java.io.Serializable;
import java.util.List;


public interface RzmServices extends Serializable {

    public void changePassword(String username, String oldPassword, String newPassword, String confirmedNewPassword) throws PasswordChangeException;

    public TransactionVOWrapper getTransaction(long id) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(long domainId) throws NoObjectFoundException;

    public SystemDomainVOWrapper getDomain(String domainName) throws NoObjectFoundException;

    public String getCountryName(String domainCode);

    public int getTransactionCount(Criterion criterion);

    public List<TransactionVOWrapper> getTransactions(Criterion criterion, int offset, int length, SortOrder sort);

    public TransactionActionsVOWrapper getChanges(DomainVOWrapper domain, boolean useRadicalChangesCheck)
        throws NoObjectFoundException, AccessDeniedException,
               RadicalAlterationException, SharedNameServersCollisionException;

    public void withdrawnTransaction(long requestId)
        throws
        NoObjectFoundException,
        TransactionCannotBeWithdrawnException, InfrastructureException;


    public List<Value> getCountrys();

    public boolean isValidCountryCode(String code);

    public List<String> parseErrors(String technicalErrors);
}
