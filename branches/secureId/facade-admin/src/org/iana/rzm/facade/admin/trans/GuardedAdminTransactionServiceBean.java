package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminTransactionServiceBean extends AbstractRZMStatefulService implements AdminTransactionService {

    private StatelessAdminTransactionService statelessAdminTransactionService;

    public GuardedAdminTransactionServiceBean(UserVOManager userManager, StatelessAdminTransactionService statelessAdminTransactionService) {
        super(userManager);
        CheckTool.checkNull(statelessAdminTransactionService, "statelessAdminTransactionService");
        this.statelessAdminTransactionService = statelessAdminTransactionService;
    }

    public String queryTransactionEPPStatus(long id) throws NoObjectFoundException, InvalidEPPTransactionException, InfrastructureException, AccessDeniedException {
        return statelessAdminTransactionService.queryTransactionEPPStatus(id, getAuthenticatedUser());
    }

    public TransactionVO getTransaction(long id) throws NoObjectFoundException, AccessDeniedException {
        return statelessAdminTransactionService.getTransaction(id, getAuthenticatedUser());
    }

   public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        statelessAdminTransactionService.transitTransactionToState(id, targetStateName, getAuthenticatedUser());
    }

    public void transitTransactionToState(long id, String targetStateName) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        statelessAdminTransactionService.transitTransactionToState(id, targetStateName, getAuthenticatedUser());
    }

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        statelessAdminTransactionService.updateTransaction(id, ticketId, redelegation, comment, getAuthenticatedUser());
    }

    public void updateTransaction(TransactionVO trans) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        statelessAdminTransactionService.updateTransaction(trans, getAuthenticatedUser());
    }

    public void moveTransactionToNextState(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException {
        statelessAdminTransactionService.moveTransactionToNextState(id, getAuthenticatedUser());
    }

    public void rejectTransaction(long id) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        statelessAdminTransactionService.rejectTransaction(id, getAuthenticatedUser());
    }

    public void transitTransaction(long id, String transitionName) throws NoObjectFoundException, InfrastructureException, AccessDeniedException {
        statelessAdminTransactionService.transitTransaction(id, transitionName, getAuthenticatedUser());
    }

    public void deleteTransaction(TransactionVO transactionVO) throws NoObjectFoundException, AccessDeniedException {
        statelessAdminTransactionService.deleteTransaction(transactionVO, getAuthenticatedUser());
    }

    public void deleteTransaction(long transactionId) throws NoObjectFoundException, AccessDeniedException {
        statelessAdminTransactionService.deleteTransaction(transactionId, getAuthenticatedUser());
    }


    public TransactionVO get(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return statelessAdminTransactionService.get(id, getAuthenticatedUser());
    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessAdminTransactionService.acceptTransaction(id, token, getAuthenticatedUser());
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessAdminTransactionService.rejectTransaction(id, token, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessAdminTransactionService.createTransactions(domain, splitNameServerChange, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessAdminTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessAdminTransactionService.find(order, offset, limit, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessAdminTransactionService.find(criteria, order, offset, limit, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessAdminTransactionService.find(criteria, order, offset, limit, getAuthenticatedUser());
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return statelessAdminTransactionService.count(criteria, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessAdminTransactionService.find(criteria, offset, limit, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessAdminTransactionService.find(criteria, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessAdminTransactionService.createTransactions(domain, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessAdminTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment, getAuthenticatedUser());
    }


    public void approveByUSDoC(long id) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        statelessAdminTransactionService.approveByUSDoC(id, getAuthenticatedUser());
    }

    public void rejectByUSDoC(long id) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        statelessAdminTransactionService.rejectByUSDoC(id, getAuthenticatedUser());
    }

    public void confirmByUSDoC(long id, boolean nsChange, boolean accept) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        statelessAdminTransactionService.confirmByUSDoC(id, nsChange, accept, getAuthenticatedUser());
    }

    public void withdrawTransaction(long id) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        statelessAdminTransactionService.withdrawTransaction(id, getAuthenticatedUser());
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessAdminTransactionService.acceptTransaction(id, getAuthenticatedUser());
    }

    public List<TransactionVO> getByTicketID(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return statelessAdminTransactionService.getByTicketID(id, getAuthenticatedUser());
    }
}
