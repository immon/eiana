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
import org.iana.rzm.facade.system.trans.IllegalTransactionStateException;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.facade.system.trans.TransactionCannotBeWithdrawnException;
import org.iana.rzm.facade.system.trans.TransactionService;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.user.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A guarded version of <code>SystemTransactionService</code> which provides a role checking before calling
 * a relevant method.
 *
 * @author Patrycja Wegrzynowicz
 */
public class GuardedTransactionService extends AbstractRZMStatefulService implements TransactionService {

    private static Set<Role.Type> allowedCreateTypes = new HashSet<Role.Type>();

    static {
        allowedCreateTypes.add(AdminRole.AdminType.IANA);
    }

    private TransactionService delegate;

    public GuardedTransactionService(UserManager userManager, TransactionService delegate) {
        super(userManager);
        CheckTool.checkNull(delegate, "system transaction service");
        this.delegate = delegate;
    }

    private void isUserInRole(long transactionId) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        TransactionVO trans = delegate.get(transactionId);
        isUserInRole(trans.getDomainName());
    }

    private void isUserInRole(String domainName) throws AccessDeniedException {
        isUserInRole(domainName, null);
    }

    private void isUserInRole(String domainName, Set<Role.Type> types) throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
        RZMUser rzmUser = getRZMUser();
        for (Role role : rzmUser.getRoles()) {
            if (role.isAdmin()) {
                if (types == null || types.contains(role.getType())) return;
            } else {
                SystemRole sys = (SystemRole) role;
                if (domainName != null && domainName.equals(sys.getName())) return;
            }
        }
        throw new AccessDeniedException("no role found for " + domainName);
    }

    private void isUserInRole() throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
    }
    
    private void isUserInCreateTransactionRole(String domainName) throws AccessDeniedException {
        isUserInRole(domainName, allowedCreateTypes);
    }


    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
        delegate.setUser(user);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain);
    }

    public List<TransactionVO> getByTicketID(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
        return delegate.getByTicketID(id);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain, splitNameServerChange);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException {
        isUserInCreateTransactionRole(domain.getName());
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment);
    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
        delegate.acceptTransaction(id, token);
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole(id);
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
        isUserInRole();
        return delegate.count(criteria);
    }

    public List<TransactionVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return delegate.find(criteria);
    }

    public List<TransactionVO> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return delegate.find(order, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return delegate.find(criteria, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
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
