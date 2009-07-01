package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminTransactionService {

    public String queryTransactionEPPStatus(long id, AuthenticatedUser authUser) throws NoObjectFoundException, InvalidEPPTransactionException, InfrastructureException, AccessDeniedException;

    public TransactionVO getTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException;

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException;

    public void transitTransactionToState(long id, String targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException;

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException;

    public void updateTransaction(TransactionVO trans, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException;

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException;

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException, InfrastructureException;

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws NoObjectFoundException, InfrastructureException, AccessDeniedException;

    public void deleteTransaction(TransactionVO transactionVO, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException;

    public void deleteTransaction(long transactionId, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException;

    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, PerformTechnicalCheck performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public void approveByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException;

    public void rejectByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException;

    public void confirmByUSDoC(long id, boolean nsChange, boolean accept, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException;

    void withdrawTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException;

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authenticatedUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;
}
