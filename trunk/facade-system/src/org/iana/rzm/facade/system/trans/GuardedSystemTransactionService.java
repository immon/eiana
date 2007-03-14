package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class GuardedSystemTransactionService implements SystemTransactionService {

    private AuthenticatedUser user;
    private SystemTransactionService delegate;

    public GuardedSystemTransactionService(SystemTransactionService delegate) {
        CheckTool.checkNull(delegate, "system transaction service delegate");
        this.delegate = delegate;
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, InfrastructureException {
        return delegate.getTransaction(id);
    }

    public List<SimpleTransactionVO> findOpenTransactions() throws AccessDeniedException, InfrastructureException {        
        return delegate.findOpenTransactions();
    }

    public void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {
        delegate.performTransactionTechnicalCheck(domain);
    }

    public List<TransactionSplitVO> getPossibleTransactionSplits(IDomainVO domain) throws AccessDeniedException, InfrastructureException {
        return delegate.getPossibleTransactionSplits(domain);
    }

    public TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, InfrastructureException {
        return delegate.createTransaction(domain);
    }

    public void acceptTransaction(long id) throws AccessDeniedException, InfrastructureException {
        delegate.acceptTransaction(id);
    }

    public void rejectTransaction(long id) throws AccessDeniedException, InfrastructureException {
        delegate.rejectTransaction(id);
    }

    public void setUser(AuthenticatedUser user) {
        CheckTool.checkNull(user, "authenticated user");
        this.user = user;
        delegate.setUser(user);
    }

    public void close() {
        delegate.close();
    }
}
