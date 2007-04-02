package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.domain.DomainVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SystemTransactionService extends RZMStatefulService {

    TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    List<SimpleTransactionVO> findOpenTransactions() throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void performTransactionTechnicalCheck(DomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException;

    List<TransactionSplitVO> getPossibleTransactionSplits(DomainVO domain) throws AccessDeniedException, InfrastructureException;

    TransactionVO createTransaction(DomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException; 
}
