package org.iana.rzm.facade.system.trans;

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
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.user.UserVOManager;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class TransactionServiceImpl extends AbstractRZMStatefulService implements TransactionService {

    StatelessTransactionService statelessTransactionService;

    public TransactionServiceImpl(UserVOManager userManager, StatelessTransactionService statelessTransactionService) {
        super(userManager);
        CheckTool.checkNull(statelessTransactionService, "stateless Transaction Service");
        this.statelessTransactionService = statelessTransactionService;
    }

    public TransactionVO get(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return statelessTransactionService.get(id, getAuthenticatedUser());
    }

    public List<TransactionVO> getByTicketID(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return statelessTransactionService.getByTicketID(id, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionService.createTransactions(domain, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionService.createTransactions(domain, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, getAuthenticatedUser());
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return statelessTransactionService.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment, getAuthenticatedUser());
    }

    public void moveTransactionToNextState(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException {
        statelessTransactionService.moveTransactionToNextState(id, getAuthenticatedUser());
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessTransactionService.acceptTransaction(id, getAuthenticatedUser());
    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessTransactionService.rejectTransaction(id, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(order, offset, limit, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, order, offset, limit, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, order, offset, limit, getAuthenticatedUser());
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return statelessTransactionService.count(criteria, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, offset, limit, getAuthenticatedUser());
    }

    public List<TransactionVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        return statelessTransactionService.find(criteria, getAuthenticatedUser());
    }


    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessTransactionService.acceptTransaction(id, token, getAuthenticatedUser());
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessTransactionService.rejectTransaction(id, token, getAuthenticatedUser());
    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        statelessTransactionService.transitTransaction(id, transitionName, getAuthenticatedUser());
    }

    public void withdrawTransaction(long id) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        statelessTransactionService.withdrawTransaction(id, getAuthenticatedUser());
    }
}
