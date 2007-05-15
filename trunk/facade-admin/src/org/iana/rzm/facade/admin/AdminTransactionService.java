package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.system.trans.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminTransactionService extends RZMStatefulService {

    TransactionVO getTransaction(long id) throws NoTransactionException;

    TransactionVO createDomainCreationTransaction(DomainVO domainVO);

    TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException;

    List<TransactionVO> findAll();

    List<TransactionVO> find(TransactionCriteriaVO criteria);

    public List<TransactionVO> findTransactions(Criterion criteria);

    public void deleteTransaction(TransactionVO transaction) throws NoTransactionException;

    public void deleteTransaction(long transactionId) throws NoTransactionException;

}
