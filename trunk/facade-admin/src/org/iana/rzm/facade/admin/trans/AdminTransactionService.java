package org.iana.rzm.facade.admin.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.admin.trans.StateUnreachableException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface AdminTransactionService extends TransactionService {

    TransactionVO createCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException;

    TransactionVO createCreationTransaction(DomainVO domainVO, boolean performTechnicalCheck) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException;

    void updateTransaction(long id, Long ticketId, String targetStateName, boolean redelegation, String comment)  throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException;

    void updateTransaction(TransactionVO trans) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException;

    void deleteTransaction(TransactionVO transaction) throws NoObjectFoundException, AccessDeniedException;

    void deleteTransaction(long transactionId) throws NoObjectFoundException, AccessDeniedException;

}
