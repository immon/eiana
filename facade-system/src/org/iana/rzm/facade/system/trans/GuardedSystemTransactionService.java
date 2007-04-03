package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.IANARole;
import org.iana.rzm.user.SystemRole;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * A guarded version of <code>SystemTransactionService</code> which provides a role checking before calling
 * a relevant method.
 *  
 * @author Patrycja Wegrzynowicz
 */
public class GuardedSystemTransactionService extends AbstractRZMStatefulService implements SystemTransactionService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new IANARole());
        allowedRoles.add(new SystemRole(SystemRole.SystemType.AC));
        allowedRoles.add(new SystemRole(SystemRole.SystemType.TC));
        allowedRoles.add(new SystemRole(SystemRole.SystemType.SO));
    }

    private SystemTransactionService delegate;

    public GuardedSystemTransactionService(UserManager userManager, SystemTransactionService delegate) {
        super(userManager);
        CheckTool.checkNull(delegate, "system transaction service");
        this.delegate = delegate;
    }

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        return delegate.getTransaction(id);
    }

    public List<TransactionVO> findOpenTransactions() throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        return delegate.findOpenTransactions();
    }

    public void performTransactionTechnicalCheck(DomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {
        isUserInRole();
        delegate.performTransactionTechnicalCheck(domain);
    }

    public List<TransactionSplitVO> getPossibleTransactionSplits(DomainVO domain) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return delegate.getPossibleTransactionSplits(domain);
    }

    public TransactionVO createTransaction(DomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        return delegate.createTransaction(domain);
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

    public void setUser(AuthenticatedUser user) {
        super.setUser(user);
        delegate.setUser(user);
    }

    public void close() {
        super.close();
        delegate.close();
    }
}