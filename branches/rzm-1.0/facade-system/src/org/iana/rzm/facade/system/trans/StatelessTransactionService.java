package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessTransactionService {

    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException;

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, PerformTechnicalCheck performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException, DNSTechnicalCheckExceptionWrapper;

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException;

    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException;

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException;


    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException;

    public void withdrawTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException;

    public void withdrawTransaction(long id, String reason, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException;

}
