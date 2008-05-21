package org.iana.rzm.facade.admin.trans;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.IllegalTransactionStateException;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface AdminTransactionService extends TransactionService {

    void transitTransactionToState(long id, TransactionStateVO.Name targetStateName) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException;

    void transitTransactionToState(long id, String targetStateName) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException;

    void updateTransaction(long id, Long ticketId, boolean redelegation, String comment) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException;

    void updateTransaction(TransactionVO trans) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException;

    void deleteTransaction(TransactionVO transaction) throws NoObjectFoundException, AccessDeniedException;

    void deleteTransaction(long transactionId) throws NoObjectFoundException, AccessDeniedException;

    void approveByUSDoC(long transactionId) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException;

    void rejectByUSDoC(long transactionId) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException;

    void confirmByUSDoC(long transactionId, boolean nsChange, boolean accept) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException;

    
}
