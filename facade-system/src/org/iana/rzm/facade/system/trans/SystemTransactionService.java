package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SystemTransactionService extends RZMStatefulService {

    TransactionVO getTransaction(long id) throws AccessDeniedException, InfrastructureException;

    List<SimpleTransactionVO> findOpenTransactions() throws AccessDeniedException, InfrastructureException;

    void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException;

    List<TransactionSplitVO> getPossibleTransactionSplits(IDomainVO domain) throws AccessDeniedException, InfrastructureException;

    TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, InfrastructureException;

    void acceptTransaction(long id) throws AccessDeniedException, InfrastructureException;

    void rejectTransaction(long id) throws AccessDeniedException, InfrastructureException;
}
