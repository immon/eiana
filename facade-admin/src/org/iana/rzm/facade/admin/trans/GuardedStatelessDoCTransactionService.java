package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.Or;
import org.iana.criteria.Order;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.user.UserManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessDoCTransactionService extends AbstractRZMStatelessService implements StatelessAdminTransactionService {

    private StatelessAdminTransactionService statelessAdminTransactionService;

    public GuardedStatelessDoCTransactionService(UserManager userManager, StatelessAdminTransactionService statelessAdminTransactionService) {
        super(userManager);
        this.statelessAdminTransactionService = statelessAdminTransactionService;
    }

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return find(null, order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isGov(authUser);
        return statelessAdminTransactionService.find(getUsDoCCriteria(), order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isGov(authUser);
        return statelessAdminTransactionService.find(getUsDoCCriteria(), order, offset, limit, authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isGov(authUser);
        return statelessAdminTransactionService.count(getUsDoCCriteria(), authUser);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isGov(authUser);
        return statelessAdminTransactionService.find(getUsDoCCriteria(), offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isGov(authUser);
        return statelessAdminTransactionService.find(getUsDoCCriteria(), authUser);
    }

    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isGov(authUser);
        TransactionVO transaction = statelessAdminTransactionService.get(id, authUser);
        TransactionStateVO.Name stateName = transaction.getState().getName();

        if (!stateName.equals(TransactionStateVO.Name.COMPLETED) || !stateName.equals(TransactionStateVO.Name.PENDING_USDOC_APPROVAL))
            throw new NoObjectFoundException("transaction", "" + id);
        
        return transaction;
    }

    private Criterion getUsDoCCriteria() {
        Criterion completed = new Equal(TransactionCriteriaFields.STATE, TransactionState.Name.COMPLETED.toString());
        Criterion usDocApproval = new Equal(TransactionCriteriaFields.STATE, TransactionState.Name.PENDING_USDOC_APPROVAL.toString());
        return new Or(Arrays.asList(completed, usDocApproval));
    }

    public String queryTransactionEPPStatus(long id, AuthenticatedUser authUser) throws NoObjectFoundException, InvalidEPPTransactionException, InfrastructureException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public TransactionVO getTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void transitTransactionToState(long id, String targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void updateTransaction(TransactionVO trans, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws NoObjectFoundException, InfrastructureException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void deleteTransaction(TransactionVO transactionVO, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void deleteTransaction(long transactionId, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void approveByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void rejectByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void confirmByUSDoC(long id, boolean nsChange, boolean accept, AuthenticatedUser authUser) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public void withdrawTransaction(long id, String reason, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authenticatedUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        throw new AccessDeniedException("authenticated user not in the role IANA");
    }
}
