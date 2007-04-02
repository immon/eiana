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

    public void performTransactionTechnicalCheck(DomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {

    }

    public List<TransactionSplitVO> getPossibleTransactionSplits(DomainVO domain) throws AccessDeniedException, InfrastructureException {
        return null;
    }

    public TransactionVO createTransaction(DomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return null;
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {

    }
}
