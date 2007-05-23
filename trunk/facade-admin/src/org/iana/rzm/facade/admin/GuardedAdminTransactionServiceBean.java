package org.iana.rzm.facade.admin;

import org.iana.criteria.Criterion;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
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
public class GuardedAdminTransactionServiceBean extends AbstractRZMStatefulService implements AdminTransactionService {

    private static Set<Role> allowedRoles = new HashSet<Role>();
    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    private static List<String> states = new ArrayList<String>();
    static {
        states.add("PENDING_CONTACT_CONFIRMATION");
        states.add("PENDING_IMPACTED_PARTIES");
        states.add("PENDING_IANA_CONFIRMATION");
        states.add("PENDING_EXT_APPROVAL");
        states.add("PENDING_USDOC_APPROVAL");
        states.add("PENDING_ZONE_INSERTION");
        states.add("PENDING_ZONE_PUBLICATION");
    }

    TransactionManager transactionManager;
    DomainManager domainManager;

    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }

    public GuardedAdminTransactionServiceBean(UserManager userManager, TransactionManager transactionManager) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        this.transactionManager = transactionManager;
    }

    public GuardedAdminTransactionServiceBean(UserManager userManager, TransactionManager transactionManager,
                                              DomainManager domainManager) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(domainManager, "domain manager");
        this.transactionManager = transactionManager;
        this.domainManager = domainManager;
    }

    public TransactionVO getTransaction(long id) throws NoTransactionException {
        isUserInRole();
        try {
            Transaction retTransaction = transactionManager.getTransaction(id);
            CheckTool.checkNull(retTransaction, "no such transaction: " + id);
            return TransactionConverter.toTransactionVO(retTransaction);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        }
    }

    public TransactionVO createDomainCreationTransaction(DomainVO domainVO) throws NoDomainSystemUsersException {
        isUserInRole();
        if (userManager.findUsersInSystemRole(domainVO.getName(), null, true, false).isEmpty())
            throw new NoDomainSystemUsersException(domainVO.getName());
        domainManager.create(new Domain(domainVO.getName()));
        Transaction trans = transactionManager.createDomainCreationTransaction(FromVOConverter.toDomain(domainVO));
        return TransactionConverter.toTransactionVO(trans);
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName) throws NoSuchStateException, StateUnreachableException, NoTransactionException, FacadeTransactionException {
        transitTransactionToState(id, targetStateName.toString());
    }

    public void transitTransactionToState(long id, String targetStateName) throws NoSuchStateException, StateUnreachableException, NoTransactionException, FacadeTransactionException {
        isUserInRole();
        try {
            if (!states.contains(targetStateName)) throw new NoSuchStateException(targetStateName);

            Transaction transaction = transactionManager.getTransaction(id);

            if (((targetStateName.equals("PENDING_ZONE_INSERTION")) ||
                    (targetStateName.equals("PENDING_ZONE_PUBLICATION"))) &&
                    !(transaction.getDomainChange().getFieldChanges().containsKey("nameServers"))) throw new StateUnreachableException(targetStateName);

            String currentState = transaction.getState().getName().toString();

            int currentStateIndex = states.indexOf(currentState);
            int difference = states.indexOf(targetStateName) - currentStateIndex;

            if (difference < 0) {
                for(int i = currentStateIndex; i > (difference + currentStateIndex);) {
                    transaction = transactionManager.getTransaction(id);
                    transaction.transit(this.getRZMUser(), "admin-" + states.get(--i));
                }
            } else {
                for(int i = currentStateIndex; i < (difference + currentStateIndex);) {
                    transaction = transactionManager.getTransaction(id);
                    transaction.transit(this.getRZMUser(), "admin-" + states.get(++i));
                }
            }
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void setTransactionTicketId(long transactionID, long ticketId) throws NoTransactionException {
        isUserInRole();
        try {
            Transaction retTransaction= transactionManager.getTransaction(transactionID);
            retTransaction.setTicketID(ticketId);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        }
    }

    public void acceptTransaction(long id) throws NoTransactionException, FacadeTransactionException {
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

    public void rejectTransaction(long id) throws NoTransactionException, FacadeTransactionException {
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

    public void transitTransaction(long id, String transitionName) throws NoTransactionException, FacadeTransactionException {
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

    public TransactionVO createDomainModificationTransaction(DomainVO domainVO) throws NoDomainModificationException {
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

    public List<TransactionVO> findAll() {
        isUserInRole();
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findAll())
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> find(TransactionCriteriaVO criteriaVO) {
        isUserInRole();
        CheckTool.checkNull(criteriaVO, "transaction criteria");
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        TransactionCriteria criteria = TransactionCriteriaConverter.convert(criteriaVO);
        for (Transaction transaction : transactionManager.find(criteria))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(String domainName) {
        isUserInRole();
        CheckTool.checkEmpty(domainName, "domain name");
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(domainName))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(Set<String> domainNames){
        isUserInRole();
        CheckTool.checkCollectionNull(domainNames, "domains names");
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(domainNames))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(UserVO userVO) {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        RZMUser user = UserConverter.convert(userVO);
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(user))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public List<TransactionVO> findTransactions(UserVO userVO, String domainName) {
        isUserInRole();
        CheckTool.checkNull(userVO, "userVO");
        CheckTool.checkEmpty(domainName, "domain name");
        RZMUser user = UserConverter.convert(userVO);
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionManager.findTransactions(user, domainName))
            transactionVOs.add(TransactionConverter.toTransactionVO(transaction));
        return transactionVOs;
    }

    public void deleteTransaction(TransactionVO transactionVO) throws NoTransactionException {
        isUserInRole();
        CheckTool.checkNull(transactionVO, "transactionVO");
        deleteTransaction(transactionVO.getTransactionID());
    }

    public void deleteTransaction(long transactionId) throws NoTransactionException {
        isUserInRole();
        try {
            transactionManager.deleteTransaction(transactionId);
        } catch (NoSuchTransactionException e) {
            throw new NoTransactionException(e.getId());
        }
    }

    public List<TransactionVO> findTransactions(Criterion criteria) {
        // todo
        return null;
    }


    public int count(Criterion criteria) {
        return 0; //todo
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) {
        return null;  //todo
    }
}
