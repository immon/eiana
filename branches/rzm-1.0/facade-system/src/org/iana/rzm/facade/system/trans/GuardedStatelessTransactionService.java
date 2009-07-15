package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessTransactionService extends AbstractRZMStatelessService implements StatelessTransactionService {

    private TransactionService delegate;

    private TransactionManager manager;

    private StatelessTransactionService statelessTransactionService;

    public GuardedStatelessTransactionService(UserManager userManager, TransactionService delegate, TransactionManager manager, StatelessTransactionService statelessTransactionService) {
        super(userManager);
        CheckTool.checkNull(delegate, "system transaction service");
        CheckTool.checkNull(manager, "transaction manager");
        CheckTool.checkNull(statelessTransactionService, "stateless Transaction Service");
        this.delegate = delegate;
        this.manager = manager;
        this.statelessTransactionService = statelessTransactionService;
    }

    private void isUserInRole(long transactionId, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        TransactionVO trans = delegate.get(transactionId);
        try {
            isUserInIanaGovOrDomainRole(trans.getDomainName() , authUser);
        } catch (AccessDeniedException e) {
            for (String domainName : trans.getImpactedDomains()) {
                try {
                    isUserInIanaGovOrDomainRole(domainName, authUser);
                    return;
                } catch (AccessDeniedException e1) {
                }
            }
            throw new AccessDeniedException("no role found for transaction " + trans.getDomainName() + " : " + trans.getImpactedDomains());
        }
    }

    private void isUserInRole(long transactionId, String token, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        try {
            Transaction trans = manager.getTransaction(transactionId);
            ContactConfirmations contactConfirmations = trans.getContactConfirmations();
            if (contactConfirmations == null) throw new AccessDeniedException("no contact confirmation pending");
            SystemRole role = contactConfirmations.getRoleForToken(token);
            if (role == null) throw new AccessDeniedException("no contact confirmation pending for the token: " + token);
            RZMUser user = getRZMUser(authUser);
            if (user.isAdmin()) return;
            if (user.isInRole(role)) return;
            throw new AccessDeniedException("User " + user.getName() + " is not an  " + role.getType().name() + " for domain " + role.getName() + "  (token: " + token + ")");
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(transactionId, "transaction");
        }
    }

    private void isUserInCreateTransactionRole(String domainName, AuthenticatedUser authUser) throws AccessDeniedException {
        isUserInIanaOrDomainRole(domainName, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName(), authUser);
        return statelessTransactionService.createTransactions(domain, authUser);
    }

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, authUser);
        return statelessTransactionService.getByTicketID(id, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName(), authUser);
        return statelessTransactionService.createTransactions(domain, splitNameServerChange, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName(), authUser);
        return statelessTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, PerformTechnicalCheck performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName(), authUser);
        return statelessTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment, authUser);
    }

    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, token, authUser);
        statelessTransactionService.acceptTransaction(id, token, authUser);
    }

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, token, authUser);
        statelessTransactionService.rejectTransaction(id, token, authUser);
    }

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException {
        isUserInRole(id, authUser);
        statelessTransactionService.moveTransactionToNextState(id, authUser);
    }

    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, authUser);
        statelessTransactionService.acceptTransaction(id, authUser);
    }

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, authUser);
        statelessTransactionService.rejectTransaction(id, authUser);
    }

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, authUser);
        statelessTransactionService.transitTransaction(id, transitionName, authUser);
    }


    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        isUserInRole(id, authUser);
        return statelessTransactionService.get(id, authUser);
    }


    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        return statelessTransactionService.count(criteria, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, authUser);
    }

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, order, offset, limit, authUser);
    }

    public void withdrawTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        isUserInRole(id, authUser);
        statelessTransactionService.withdrawTransaction(id, authUser);
    }

    public void withdrawTransaction(long id, String reason, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        isUserInRole(id, authUser);
        statelessTransactionService.withdrawTransaction(id, reason, authUser);
    }
    
}
