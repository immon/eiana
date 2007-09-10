package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.user.UserVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmptySystemTransactionService implements SystemTransactionService {

    public void setUser(AuthenticatedUser user) {

    }

    public void close() {

    }

    public AuthenticatedUser getAuthenticatedUser() {
        return null;
    }

    public void setIgnoreTicketingSystemErrors(boolean ignore) {

    }

    public boolean getIgnoreTicketingSystemErrors() {
        return false;
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return null;
    }

    public List<TransactionVO> findOpenTransactions() throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return new ArrayList<TransactionVO>();
    }

    public void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {

    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return null;
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return null;
    }


    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException {
        return null;
    }

    public TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return null;
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public List<TransactionVO> findTransactions(TransactionCriteriaVO criteria) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public List<TransactionVO> findTransactions(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return 0;
    }

    public List<TransactionVO> findTransactions(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public UserVO getUser() {
        return null;
    }
}
