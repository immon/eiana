package org.iana.rzm.facade.system.trans.guards;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionsVO;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.dns.check.DNSTechnicalCheckException;

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

    private static Set<Role> allowedRoles = new HashSet<Role>();
    private static Set<Role> allowedCreateTransactionRoles = new HashSet<Role>();

    static {
        allowedCreateTransactionRoles.add(new AdminRole(AdminRole.AdminType.IANA));
        allowedCreateTransactionRoles.add(new SystemRole(SystemRole.SystemType.AC));
        allowedCreateTransactionRoles.add(new SystemRole(SystemRole.SystemType.TC));
        allowedCreateTransactionRoles.add(new SystemRole(SystemRole.SystemType.SO));

        allowedRoles.addAll(allowedCreateTransactionRoles);
        allowedRoles.add(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
    }

    private TransactionService delegate;

    public GuardedTransactionService(UserManager userManager, TransactionService delegate) {
        super(userManager);
        CheckTool.checkNull(delegate, "system transaction service");
        this.delegate = delegate;
    }

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    private void isUserInCreateTransactionRole() throws AccessDeniedException {
        isUserInRole(allowedCreateTransactionRoles);
    }


    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
        delegate.setUser(user);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInCreateTransactionRole();
        return delegate.createTransactions(domain);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInCreateTransactionRole();
        return delegate.createTransactions(domain, splitNameServerChange);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInCreateTransactionRole();
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException {
        isUserInCreateTransactionRole();
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment);
    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.acceptTransaction(id, token);
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.rejectTransaction(id, token);
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.acceptTransaction(id);
    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.rejectTransaction(id);
    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.transitTransaction(id, transitionName);
    }


    public TransactionVO get(long id) throws AccessDeniedException, InfrastructureException, NoObjectFoundException {
        isUserInRole();
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
}
