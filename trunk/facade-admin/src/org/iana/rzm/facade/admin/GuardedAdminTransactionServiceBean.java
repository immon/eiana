package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.trans.*;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.UserManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminTransactionServiceBean extends AdminFinderServiceBean<TransactionVO> implements AdminTransactionService {

    private static Set<Role> allowedRoles = new HashSet<Role>();
    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    TransactionManager transactionManager;
    DomainManager domainManager;
    SystemTransactionService transactionService;

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public GuardedAdminTransactionServiceBean(UserManager userManager, TransactionManager transactionManager, SystemTransactionService transactionService) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        this.transactionManager = transactionManager;
        this.transactionService = transactionService;
    }

    public GuardedAdminTransactionServiceBean(UserManager userManager,
                                              TransactionManager transactionManager,
                                              DomainManager domainManager,
                                              SystemTransactionService transactionService) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(transactionService, "transaction service");
        this.transactionManager = transactionManager;
        this.domainManager = domainManager;
        this.transactionService = transactionService;
    }

    public TransactionVO getTransaction(long id) throws NoTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction retTransaction = transactionManager.getTransaction(id);
            CheckTool.checkNull(retTransaction, "no such transaction: " + id);
            return TransactionConverter.toTransactionVO(retTransaction);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        }
    }

    public TransactionVO createDomainCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException, InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        if (userManager.findUsersInSystemRole(domainVO.getName(), null, true, false).isEmpty())
            throw new NoDomainSystemUsersException(domainVO.getName());
        domainManager.create(new Domain(domainVO.getName()));
        domainVO.setStatus(DomainVO.Status.ACTIVE);
        Transaction trans = transactionManager.createDomainCreationTransaction(FromVOConverter.toDomain(domainVO));
        return TransactionConverter.toTransactionVO(trans);
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName) throws NoSuchStateException, StateUnreachableException, NoTransactionException, FacadeTransactionException, AccessDeniedException {
        transitTransactionToState(id, targetStateName.toString());
    }

    public void transitTransactionToState(long id, String targetStateName) throws NoSuchStateException, StateUnreachableException, NoTransactionException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id); 
            transaction.transitTo(getRZMUser(), targetStateName);
            
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void updateTransaction(long id, Long ticketId, TransactionStateVO.Name targetStateName, boolean redelegation) throws NoTransactionException, StateUnreachableException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        _updateTransaction(id, ticketId, targetStateName == null ? null : targetStateName.toString(), redelegation);
    }

    public void updateTransaction(long id, Long ticketId, String targetStateName, boolean redelegation) throws NoTransactionException, StateUnreachableException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        _updateTransaction(id, ticketId, targetStateName, redelegation);
    }

    private void _updateTransaction(long id, Long ticketId, String targetStateName, boolean redelegation) throws NoTransactionException, StateUnreachableException, FacadeTransactionException {
        try {
            Transaction retTransaction= transactionManager.getTransaction(id);
            retTransaction.setTicketID(ticketId);
            retTransaction.setRedelegation(redelegation);
            if (targetStateName != null && !targetStateName.equals(""+retTransaction.getState().getName())) {
                retTransaction.transitTo(getRZMUser(), targetStateName);
            }
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        } catch (TransactionException e) {
            throw new StateUnreachableException(""+targetStateName);
        }
    }
    public void setTransactionTicketId(long transactionID, long ticketId) throws NoTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction retTransaction= transactionManager.getTransaction(transactionID);
            retTransaction.setTicketID(ticketId);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        }
    }

    public void acceptTransaction(long id) throws NoTransactionException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(this.getRZMUser(), "admin-accept");
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void rejectTransaction(long id) throws NoTransactionException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(this.getRZMUser(), "admin-reject");
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void transitTransaction(long id, String transitionName) throws NoTransactionException, FacadeTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(getRZMUser(), transitionName);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException, InvalidCountryCodeException, AccessDeniedException {
        isUserInRole();
        try {
            CheckTool.checkNull(domainVO, "domainVO");
            Domain domain = FromVOConverter.toDomain(domainVO);
            Transaction createdTransaction = transactionManager.createDomainModificationTransaction(domain);
            CheckTool.checkNull(createdTransaction, "created modification transaction");
            return TransactionConverter.toTransactionVO(createdTransaction);
        } catch (NoModificationException e) {
            throw new NoDomainModificationException(domainVO.getName());
        }
    }

    public List<TransactionVO> createDomainModificationTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        isUserInRole();
        return transactionService.createTransactions(domain, splitNameServerChange, submitterEmail);
    }

    public List<TransactionVO> findAll() throws AccessDeniedException {
        isUserInRole();
        return find(new TransactionCriteriaVO());
    }

    public List<TransactionVO> find(TransactionCriteriaVO criteriaVO) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(criteriaVO, "transaction criteria");
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        TransactionCriteria criteria = TransactionCriteriaConverter.convert(criteriaVO);
        for (Transaction transaction : transactionManager.find(criteria))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(String domainName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "domain name");
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(domainName))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(Set<String> domainNames) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkCollectionNull(domainNames, "domains names");
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(domainNames))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(UserVO userVO) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        RZMUser user = UserConverter.convert(userVO);
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(user))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(UserVO userVO, String domainName) throws AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        CheckTool.checkEmpty(domainName, "domain name");
        RZMUser user = UserConverter.convert(userVO);
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(user, domainName))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public void deleteTransaction(TransactionVO transactionVO) throws NoTransactionException, AccessDeniedException {
        isUserInRole();
        CheckTool.checkNull(transactionVO, "transactionVO");
        deleteTransaction(transactionVO.getTransactionID());
    }

    public void deleteTransaction(long transactionId) throws NoTransactionException, AccessDeniedException {
        isUserInRole();
        try {
            transactionManager.deleteTransaction(transactionId);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        }
    }

    public List<TransactionVO> findTransactions(Criterion criteria) {
        isUserInRole();
        return TransactionConverter.toTransactionVOList(transactionManager.find(criteria));
    }


    public int count(Criterion criteria) {
        isUserInRole();
        return transactionManager.count(criteria);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) {
        isUserInRole();
        return TransactionConverter.toTransactionVOList(transactionManager.find(criteria, offset, limit));
    }
}
