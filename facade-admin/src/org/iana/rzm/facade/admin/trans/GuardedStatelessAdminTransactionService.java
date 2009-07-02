package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.Criterion;
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
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessAdminTransactionService extends AbstractRZMStatelessService implements StatelessAdminTransactionService {

    private StatelessAdminTransactionService statelessIanaTransactionService;
    private StatelessAdminTransactionService statelessDoCTransactionService;

    public GuardedStatelessAdminTransactionService(UserManager userManager,
                                                   StatelessAdminTransactionService statelessIanaTransactionService,
                                                   StatelessAdminTransactionService statelessDoCTransactionService) {
        super(userManager);
        this.statelessIanaTransactionService = statelessIanaTransactionService;
        this.statelessDoCTransactionService = statelessDoCTransactionService;
    }

    private StatelessAdminTransactionService getService(AuthenticatedUser user) {
        if (checkIsIana(user))
            return statelessIanaTransactionService;
        else
            return statelessDoCTransactionService;
    }

    public String queryTransactionEPPStatus(long id, AuthenticatedUser authUser) throws NoObjectFoundException, InvalidEPPTransactionException, InfrastructureException, AccessDeniedException {
        return getService(authUser).queryTransactionEPPStatus(id, authUser);
    }

    public TransactionVO getTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        return getService(authUser).getTransaction(id, authUser);
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        transitTransactionToState(id, targetStateName.toString(), authUser);
    }

    public void transitTransactionToState(long id, String targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        getService(authUser).transitTransactionToState(id, targetStateName, authUser);
    }

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        getService(authUser).updateTransaction(id, ticketId, redelegation, comment, authUser);
    }

    public void updateTransaction(TransactionVO trans, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        getService(authUser).updateTransaction(trans, authUser);
    }

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException {
        getService(authUser).moveTransactionToNextState(id, authUser);
    }

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        getService(authUser).rejectTransaction(id, authUser);
    }

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws NoObjectFoundException, InfrastructureException, AccessDeniedException {
        getService(authUser).transitTransaction(id, transitionName, authUser);
    }

    public void deleteTransaction(TransactionVO transactionVO, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        getService(authUser).deleteTransaction(transactionVO, authUser);
    }

    public void deleteTransaction(long transactionId, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        getService(authUser).deleteTransaction(transactionId, authUser);
    }

    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return getService(authUser).get(id, authUser);
    }

    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        getService(authUser).acceptTransaction(id, token, authUser);
    }

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        getService(authUser).rejectTransaction(id, token, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return getService(authUser).createTransactions(domain, splitNameServerChange, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return getService(authUser).createTransactions(domain, splitNameServerChange, submitterEmail, authUser);
    }

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return getService(authUser).find(order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return getService(authUser).find(criteria, order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return getService(authUser).find(criteria, order, offset, limit, authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        return getService(authUser).count(criteria, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return getService(authUser).find(criteria, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return getService(authUser).find(criteria, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return getService(authUser).createTransactions(domain, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, PerformTechnicalCheck performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return getService(authUser).createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment, authUser);
    }


    public void approveByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        getService(authUser).approveByUSDoC(id, authUser);
    }

    public void rejectByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        getService(authUser).rejectByUSDoC(id, authUser);
    }

    public void withdrawTransaction(long id, String reason, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        getService(authUser).withdrawTransaction(id, reason, authUser);
    }

    public void confirmByUSDoC(long id, boolean nsChange, boolean accept, AuthenticatedUser authUser) throws NoObjectFoundException, IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        getService(authUser).confirmByUSDoC(id, nsChange, accept, authUser);
    }


    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        getService(authUser).acceptTransaction(id, authUser);
    }

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return getService(authUser).getByTicketID(id, authUser);
    }
}
