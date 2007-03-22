package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.NoSuchTransactionException;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SystemTransactionServiceBean implements SystemTransactionService {

    private TransactionManager transactionManager;
    private AuthenticatedUser user;

    public SystemTransactionServiceBean(TransactionManager transactionManager) {
        CheckTool.checkNull(transactionManager, "transaction manager");
        
        this.transactionManager = transactionManager;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        user = null;
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, InfrastructureException {
        try {
            return TransactionConverter.toTransactionVO(transactionManager.getTransaction(id));
        } catch (NoSuchTransactionException e) {
            return null;
        }
    }

    public List<SimpleTransactionVO> findOpenTransactions() throws AccessDeniedException, InfrastructureException {
        Set<String> domainNames = user.getRoleDomainNames();
        // transactionManager.findAllProcessInstances(domainNames)
        return new ArrayList<SimpleTransactionVO>();
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
}
