package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.UserManager;
import org.iana.objectdiff.DiffConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A guarded version of <code>SystemTransactionService</code> which provides a role checking before calling
 * a relevant method.
 *
 * @author Patrycja Wegrzynowicz
 */
public class GuardedSystemTransactionService extends AbstractRZMStatefulService implements SystemTransactionService {

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

    private SystemTransactionService delegate;

    public GuardedSystemTransactionService(UserManager userManager, SystemTransactionService delegate) {
        super(userManager);
        CheckTool.checkNull(delegate, "system transaction service");
        this.delegate = delegate;
    }

    public void setIgnoreTicketingSystemErrors(boolean ignore) {
        delegate.setIgnoreTicketingSystemErrors(ignore);
    }

    public boolean getIgnoreTicketingSystemErrors() {
        return delegate.getIgnoreTicketingSystemErrors();
    }

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    private void isUserInCreateTransactionRole() throws AccessDeniedException {
        isUserInRole(allowedCreateTransactionRoles);
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        return delegate.getTransaction(id);
    }

    public List<TransactionVO> findOpenTransactions() throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        return delegate.findOpenTransactions();
    }

    public void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {
        isUserInRole();
        delegate.performTransactionTechnicalCheck(domain);
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return delegate.detectTransactionActions(domain);
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return delegate.detectTransactionActions(domain, config);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, CreateTicketException {
        isUserInCreateTransactionRole();
        return delegate.createTransactions(domain, splitNameServerChange);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, CreateTicketException {
        isUserInCreateTransactionRole();
        return delegate.createTransactions(domain, splitNameServerChange, submitterEmail);
    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.acceptTransaction(id, token);
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.rejectTransaction(id, token);
    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.transitTransaction(id, transitionName);
    }

    public List<TransactionVO> findTransactions(TransactionCriteriaVO criteria) throws AccessDeniedException, InfrastructureException {
        //todo: only this user's domains
        isUserInRole();
        return delegate.findTransactions(criteria);
    }


    public List<TransactionVO> findTransactions(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return delegate.findTransactions(criteria);
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        return delegate.count(criteria);
    }

    public List<TransactionVO> findTransactions(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return delegate.findTransactions(criteria, offset, limit);
    }

    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
        delegate.setUser(user);
    }

    public void close() {
        super.close();
        delegate.close();
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.acceptTransaction(id);
    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        delegate.rejectTransaction(id);
    }
}
