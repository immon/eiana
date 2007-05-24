package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminTransactionService extends RZMStatefulService, AdminFinderService<TransactionVO> {

    TransactionVO getTransaction(long id) throws NoTransactionException;

    TransactionVO createDomainCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException;

    TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException;

    void acceptTransaction(long id) throws NoTransactionException, FacadeTransactionException;

    void rejectTransaction(long id) throws NoTransactionException, FacadeTransactionException;

    void transitTransaction(long id, String transitionName) throws NoTransactionException, FacadeTransactionException;

    void transitTransactionToState(long id, TransactionStateVO.Name targetStateName) throws NoSuchStateException, StateUnreachableException, NoTransactionException, FacadeTransactionException;

    void transitTransactionToState(long id, String targetStateName) throws NoSuchStateException, StateUnreachableException, NoTransactionException, FacadeTransactionException;

    List<TransactionVO> findAll();

    void setTransactionTicketId(long transactionID, long ticketId) throws NoTransactionException;

    List<TransactionVO> find(TransactionCriteriaVO criteria);

    public List<TransactionVO> findTransactions(Criterion criteria);

    public void deleteTransaction(TransactionVO transaction) throws NoTransactionException;

    public void deleteTransaction(long transactionId) throws NoTransactionException;

    public int count(Criterion criteria);

    public List<TransactionVO> find(Criterion criteria, int offset, int limit);

}
