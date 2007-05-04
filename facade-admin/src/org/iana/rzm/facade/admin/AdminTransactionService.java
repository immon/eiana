package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.RZMStatefulService;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.system.trans.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AdminTransactionService extends RZMStatefulService {

    TransactionVO getTransaction(long id) throws NoSuchTransactionException;

    TransactionVO createDomainCreationTransaction(DomainVO domainVO);

    TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException;

    List<TransactionVO> findAll();

    List<TransactionVO> find(TransactionCriteriaVO criteria);

    public List<TransactionVO> findTransactions(Criterion criteria);

    public void deleteTransaction(TransactionVO transaction) throws NoSuchTransactionException;

    public void deleteTransaction(long transactionId) throws NoSuchTransactionException;

}
