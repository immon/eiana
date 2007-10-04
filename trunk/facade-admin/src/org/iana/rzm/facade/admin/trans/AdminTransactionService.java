package org.iana.rzm.facade.admin.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.admin.trans.StateUnreachableException;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface AdminTransactionService extends TransactionService {

    void updateTransaction(TransactionVO trans) throws NoObjectFoundException, StateUnreachableException, FacadeTransactionException, AccessDeniedException;

    public void deleteTransaction(TransactionVO transaction) throws NoObjectFoundException, AccessDeniedException;

    public void deleteTransaction(long transactionId) throws NoObjectFoundException, AccessDeniedException;

}
