package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmptySystemTransactionService implements SystemTransactionService {

    public TransactionVO getTransaction(long id) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public List<SimpleTransactionVO> findOpenTransactions() throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {

    }

    public List<TransactionSplitVO> getPossibleTransactionSplits(IDomainVO domain) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public void acceptTransaction(long id) throws AccessDeniedException, InfrastructureException {

    }

    public void rejectTransaction(long id) throws AccessDeniedException, InfrastructureException {

    }

    public void setUser(AuthenticatedUser user) {

    }

    public void close() {

    }
}
