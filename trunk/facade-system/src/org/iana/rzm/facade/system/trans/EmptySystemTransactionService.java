package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmptySystemTransactionService implements SystemTransactionService {

    public void setUser(AuthenticatedUser user) {

    }

    public void close() {

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

    public TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return null;
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public List<TransactionVO> findTransactions(TransactionCriteriaVO criteria)  throws AccessDeniedException, InfrastructureException {
        return null;
    }
}
