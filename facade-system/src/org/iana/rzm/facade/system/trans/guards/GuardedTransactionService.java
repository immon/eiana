package org.iana.rzm.facade.system.trans.guards;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
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
 * A guarded version of <code>SystemTransactionService</code> which provides a role checking before calling
 * a relevant method.
 *
 * @author Patrycja Wegrzynowicz
 */
public class GuardedTransactionService extends AbstractRZMStatefulService implements TransactionService {

    private TransactionService delegate;

    private TransactionManager manager;

    public GuardedTransactionService(UserManager userManager, TransactionService delegate, TransactionManager manager) {
        super(userManager);
        CheckTool.checkNull(delegate, "system transaction service");
        CheckTool.checkNull(manager, "transaction manager");
        this.delegate = delegate;
        this.manager = manager;
    }

    private void isUserInRole(long transactionId) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        TransactionVO trans = delegate.get(transactionId);
        try {
            isUserInIanaGovOrDomainRole(trans.getDomainName());
        } catch (AccessDeniedException e) {
            for (String domainName : trans.getImpactedDomains()) {
                try {
                    isUserInIanaGovOrDomainRole(domainName);
                    return;
                } catch (AccessDeniedException e1) {
                }
            }
            throw new AccessDeniedException("no role found for transaction " + trans.getDomainName() + " : " + trans.getImpactedDomains());
        }
    }

    private void isUserInRole(long transactionId, String token) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        try {
            Transaction trans = manager.getTransaction(transactionId);
            ContactConfirmations contactConfirmations = trans.getContactConfirmations();
            if (contactConfirmations == null) throw new AccessDeniedException("no contact confirmation pending");
            SystemRole role = contactConfirmations.getRoleForToken(token);
            if (role == null) throw new AccessDeniedException("no contact confirmation pending for the token: " + token);
            RZMUser user = getRZMUser();
            if (user.isAdmin()) return;
            if (user.isInRole(role)) return;
            throw new AccessDeniedException("User " + user.getName() + " is not an  " + role.getType().name() + " for domain " + role.getName() + "  (token: " + token + ")");
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(transactionId, "transaction");
        }
    }

    private void isUserInCreateTransactionRole(String domainName) throws AccessDeniedException {
        isUserInIanaOrDomainRole(domainName);
    }


    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
        delegate.setUser(user);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain);
    }

    public List<TransactionVO> getByTicketID(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
        return delegate.getByTicketID(id);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain, splitNameServerChange);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment);
    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, token);
        delegate.acceptTransaction(id, token);
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id, token);
        delegate.rejectTransaction(id, token);
    }

    public void moveTransactionToNextState(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException {
        isUserInRole(id);
        delegate.moveTransactionToNextState(id);
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
        delegate.rejectTransaction(id);
    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
        delegate.rejectTransaction(id);
    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
        delegate.transitTransaction(id, transitionName);
    }


    public TransactionVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        isUserInRole(id);
        return delegate.get(id);
    }


    public int count(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return delegate.count(criteria);
    }

    public List<TransactionVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return delegate.find(criteria);
    }

    public List<TransactionVO> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return delegate.find(order, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return delegate.find(criteria, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return delegate.find(criteria, order, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return delegate.find(criteria, order, offset, limit);
    }

    public void withdrawTransaction(long id) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        isUserInRole(id);
        delegate.withdrawTransaction(id);
    }
}
