package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.dns.check.DNSTechnicalCheckException;
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

    private StatelessAdminTransactionService statelessAdminTransactionService;

    public GuardedStatelessAdminTransactionService(UserManager userManager, StatelessAdminTransactionService statelessAdminTransactionService) {
        super(userManager);
        this.statelessAdminTransactionService = statelessAdminTransactionService;
    }



    public String queryTransactionEPPStatus(long id, AuthenticatedUser authUser) throws NoObjectFoundException, InvalidEPPTransactionException, InfrastructureException, AccessDeniedException {
        isIana(authUser);
        return statelessAdminTransactionService.queryTransactionEPPStatus(id, authUser);
    }

    public TransactionVO getTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        isIana(authUser);
        return statelessAdminTransactionService.getTransaction(id, authUser);
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        transitTransactionToState(id, targetStateName.toString(), authUser);
    }

    public void transitTransactionToState(long id, String targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        isIana(authUser);
        statelessAdminTransactionService.transitTransactionToState(id, targetStateName, authUser);
    }

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        isIana(authUser);
        statelessAdminTransactionService.updateTransaction(id, ticketId, redelegation, comment, authUser);
    }

    public void updateTransaction(TransactionVO trans, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        isIana(authUser);
        statelessAdminTransactionService.updateTransaction(trans, authUser);
    }

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException {
        isIana(authUser);
        statelessAdminTransactionService.moveTransactionToNextState(id, authUser);
    }

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        isIana(authUser);
        statelessAdminTransactionService.rejectTransaction(id, authUser);
    }

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws NoObjectFoundException, InfrastructureException, AccessDeniedException {
        isIana(authUser);
        statelessAdminTransactionService.transitTransaction(id, transitionName, authUser);
    }

    public void deleteTransaction(TransactionVO transactionVO, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        isIana(authUser);
        statelessAdminTransactionService.deleteTransaction(transactionVO, authUser);
    }

    public void deleteTransaction(long transactionId, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        isIana(authUser);
        statelessAdminTransactionService.deleteTransaction(transactionId, authUser);
    }


    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.get(id, authUser);
    }

    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isIana(authUser);
        statelessAdminTransactionService.acceptTransaction(id, token, authUser);
    }

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isIana(authUser);
        statelessAdminTransactionService.rejectTransaction(id, token, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isIana(authUser);
        return statelessAdminTransactionService.createTransactions(domain, splitNameServerChange, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isIana(authUser);
        return statelessAdminTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, authUser);
    }

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.find(order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.find(criteria, order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.find(criteria, order, offset, limit, authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        isIana(authUser);
        return statelessAdminTransactionService.count(criteria, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.find(criteria, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.find(criteria, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isIana(authUser);
        return statelessAdminTransactionService.createTransactions(domain, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isIana(authUser);
        return statelessAdminTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment, authUser);
    }


    public void approveByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        isIanaOrGOV(authUser);
        statelessAdminTransactionService.approveByUSDoC(id, authUser);
    }

    public void rejectByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        isIanaOrGOV(authUser);
        statelessAdminTransactionService.rejectByUSDoC(id, authUser);
    }

    public void withdrawTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        isIana(authUser);
        statelessAdminTransactionService.withdrawTransaction(id, authUser);
    }

    public void confirmByUSDoC(long id, boolean nsChange, boolean accept, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        isIanaOrGOV(authUser);
        statelessAdminTransactionService.confirmByUSDoC(id, nsChange, accept, authUser);
    }


    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isIana(authUser);
        statelessAdminTransactionService.acceptTransaction(id, authUser);
    }

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isIana(authUser);
        return statelessAdminTransactionService.getByTicketID(id, authUser);
    }
}
