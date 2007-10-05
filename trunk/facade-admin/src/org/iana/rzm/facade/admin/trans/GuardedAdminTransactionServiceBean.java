package org.iana.rzm.facade.admin.trans;

import org.iana.criteria.*;
import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
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
import org.iana.rzm.facade.system.notification.NotificationAddresseeVO;
import org.iana.rzm.facade.system.notification.NotificationConverter;
import org.iana.rzm.facade.system.notification.NotificationCriteriaConverter;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.converters.TransactionCriteriaConverter;
import org.iana.rzm.facade.system.trans.converters.TransactionConverter;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.admin.trans.AdminTransactionService;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.admin.trans.NoSuchStateException;
import org.iana.rzm.facade.admin.trans.StateUnreachableException;
import org.iana.rzm.facade.services.AbstractFinderService;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.UserManager;
import org.iana.objectdiff.DiffConfiguration;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminTransactionServiceBean extends TransactionServiceImpl implements AdminTransactionService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    public GuardedAdminTransactionServiceBean(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager, TransactionDetectorService transactionDetectorService) {
        super(userManager, transactionManager, domainManager, transactionDetectorService);
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
            throw new NoObjectFoundException("transaction", ""+e.getId());
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
            throw new NoObjectFoundException("transaction", ""+e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void updateTransaction(long id, Long ticketId, String targetStateName, boolean redelegation, String comment) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        TransactionVO trans = new TransactionVO();
        try {
            trans.setTransactionID(id);
            trans.setTicketID(ticketId);
            trans.setState(new TransactionStateVO(targetStateName));
            trans.setRedelegation(redelegation);
            trans.setComment(comment);
            updateTransaction(trans);
        } catch (IllegalArgumentException e) {
            throw new StateUnreachableException(targetStateName);
        }
    }

    public void updateTransaction(TransactionVO trans) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        isUserInRole();
        String targetStateName = ""+trans.getState().getName();
        try {
            Transaction retTransaction = transactionManager.getTransaction(trans.getObjId());
            retTransaction.setTicketID(trans.getTicketID());
            retTransaction.setRedelegation(trans.isRedelegation());
            if (targetStateName != null && !targetStateName.equals("" + retTransaction.getState().getName())) {
                retTransaction.transitTo(getRZMUser(), targetStateName);
            }
            retTransaction.setComment(trans.getComment());
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", ""+e.getId());
        } catch (TransactionException e) {
            throw new StateUnreachableException(targetStateName);
        }
    }

    public void acceptTransaction(long id) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(this.getRZMUser(), "admin-accept");
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", ""+e.getId());
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
            throw new NoObjectFoundException("transaction", ""+e.getId());
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
            throw new NoObjectFoundException("transaction", ""+e.getId());
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
            throw new NoObjectFoundException("transaction", ""+e.getId());
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

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return super.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment);
    }
}
