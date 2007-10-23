package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.converters.DomainFromVOConverter;
import org.iana.rzm.facade.system.domain.vo.DomainVO;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.NoDomainModificationException;
import org.iana.rzm.facade.system.trans.NoDomainSystemUsersException;
import org.iana.rzm.facade.system.trans.TransactionDetectorService;
import org.iana.rzm.facade.system.trans.TransactionServiceImpl;
import org.iana.rzm.facade.system.trans.converters.TransactionConverter;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.UserManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminTransactionServiceBean extends TransactionServiceImpl implements AdminTransactionService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    public GuardedAdminTransactionServiceBean(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager, TransactionDetectorService transactionDetectorService, DNSTechnicalCheck dnsTechnicalCheck) {
        super(userManager, transactionManager, domainManager, transactionDetectorService, dnsTechnicalCheck);
    }

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }


    public TransactionVO getTransaction(long id) throws NoObjectFoundException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction retTransaction = transactionManager.getTransaction(id);
            CheckTool.checkNull(retTransaction, "no such transaction: " + id);
            return TransactionConverter.toTransactionVO(retTransaction);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
    }

    public TransactionVO createCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException {
        return createCreationTransaction(domainVO, false);
    }

    public TransactionVO createCreationTransaction(DomainVO domainVO, boolean performTechnicalCheck) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        if (userManager.findUsersInSystemRole(domainVO.getName(), null, true, false).isEmpty())
            throw new NoDomainSystemUsersException(domainVO.getName());
        domainManager.create(new Domain(domainVO.getName()));
        domainVO.setStatus(DomainVO.Status.ACTIVE);
        Transaction trans;
        trans = transactionManager.createDomainCreationTransaction(DomainFromVOConverter.toDomain(domainVO), performTechnicalCheck);
        return TransactionConverter.toTransactionVO(trans);
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        transitTransactionToState(id, targetStateName.toString());
    }

    public void transitTransactionToState(long id, String targetStateName) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transitTo(getRZMUser(), targetStateName);

        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        TransactionVO trans = new TransactionVO();

        trans.setTransactionID(id);
        trans.setTicketID(ticketId);
        trans.setRedelegation(redelegation);
        trans.setComment(comment);
        updateTransaction(trans);
    }

    public void updateTransaction(TransactionVO trans) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        isUserInRole();

        try {
            Transaction retTransaction = transactionManager.getTransaction(trans.getObjId());
            retTransaction.setTicketID(trans.getTicketID());
            retTransaction.setRedelegation(trans.isRedelegation());
            retTransaction.setComment(trans.getComment());
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
    }

    public void moveTransactionToNextState(long id) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(this.getRZMUser(), "admin-accept");
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e.getMessage());
        }
    }

    public void rejectTransaction(long id) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(this.getRZMUser(), "admin-reject");
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e.getMessage());
        }
    }

    public void transitTransaction(long id, String transitionName) throws NoObjectFoundException, InfrastructureException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(getRZMUser(), transitionName);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e.getMessage());
        }
    }

    public void deleteTransaction(TransactionVO transactionVO) throws NoObjectFoundException, AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(transactionVO, "transactionVO");
        deleteTransaction(transactionVO.getTransactionID());
    }

    public void deleteTransaction(long transactionId) throws NoObjectFoundException, AccessDeniedException {
        isUserInRole();
        try {
            transactionManager.deleteTransaction(transactionId);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
    }


    public TransactionVO get(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        return super.get(id);
    }

    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        super.acceptTransaction(id, token);
    }

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        isUserInRole();
        super.rejectTransaction(id, token);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return super.createTransactions(domain, splitNameServerChange);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return super.createTransactions(domain, splitNameServerChange, submitterEmail);
    }

    public List<TransactionVO> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return super.find(order, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return super.find(criteria, order, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return super.find(criteria, order, offset, limit);
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        isUserInRole();
        return super.count(criteria);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return super.find(criteria, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        isUserInRole();
        return super.find(criteria);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return super.createTransactions(domain);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException {
        isUserInRole();
        return super.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment);
    }
}
