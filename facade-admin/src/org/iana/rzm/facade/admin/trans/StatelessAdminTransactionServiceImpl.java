package org.iana.rzm.facade.admin.trans;

import org.apache.log4j.Logger;
import org.iana.criteria.Criterion;
import org.iana.criteria.Order;
import org.iana.criteria.SortCriterion;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.facade.system.trans.converters.TransactionConverter;
import org.iana.rzm.facade.system.trans.vo.TransactionStateVO;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.change.TransactionChangeType;
import org.iana.rzm.trans.confirmation.usdoc.USDoCConfirmationAlreadyReceived;
import org.iana.rzm.trans.confirmation.usdoc.USDoCConfirmationMismatch;
import org.iana.rzm.trans.epp.info.EPPStatusQuery;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessAdminTransactionServiceImpl extends StatelessTransactionServiceImpl implements StatelessAdminTransactionService {

    private static Logger logger = Logger.getLogger(GuardedAdminTransactionServiceBean.class);

    private TransactionManager transactionManager;

    private EPPStatusQuery query;

    private UserManager userManager;

    public StatelessAdminTransactionServiceImpl(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager, TransactionDetectorService transactionDetectorService, DNSTechnicalCheck dnsTechnicalCheck, EPPStatusQuery query) {
        super(userManager, transactionManager, domainManager, transactionDetectorService, dnsTechnicalCheck);
        CheckTool.checkNull(query, "null epp status query");
        this.query = query;
        this.transactionManager = transactionManager;
        this.userManager = userManager;
    }

    public String queryTransactionEPPStatus(long id, AuthenticatedUser authUser) throws NoObjectFoundException, InvalidEPPTransactionException, InfrastructureException, AccessDeniedException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            if (!trans.isNameServerChange()) throw new InvalidEPPTransactionException();
            return String.valueOf(query.queryStatusAndProcess(id));
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (Exception e) {
            throw new InfrastructureException("epp query", e);
        }
    }

    public TransactionVO getTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        try {
            Transaction retTransaction = transactionManager.getTransaction(id);
            CheckTool.checkNull(retTransaction, "no such transaction: " + id);
            return TransactionConverter.toTransactionVO(retTransaction);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
    }

    public void transitTransactionToState(long id, TransactionStateVO.Name targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        transitTransactionToState(id, targetStateName.toString(), authUser);
    }

    public void transitTransactionToState(long id, String targetStateName, AuthenticatedUser authUser) throws NoSuchStateException, StateUnreachableException, NoObjectFoundException, FacadeTransactionException, AccessDeniedException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transitTo(userManager.get(authUser.getUserName()), targetStateName);
            markModified(transaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new FacadeTransactionException(e.getMessage());
        }
    }

    public void updateTransaction(long id, Long ticketId, boolean redelegation, String comment, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        TransactionVO trans = new TransactionVO();
        trans.setTransactionID(id);
        trans.setTicketID(ticketId);
        trans.setRedelegation(redelegation);
        trans.setComment(comment);
        updateTransaction(trans, authUser);
    }

    public void updateTransaction(TransactionVO trans, AuthenticatedUser authUser) throws NoObjectFoundException, StateUnreachableException, InfrastructureException, AccessDeniedException {
        try {
            Transaction retTransaction = transactionManager.getTransaction(trans.getObjId());
            retTransaction.setTicketID(trans.getTicketID());
            retTransaction.setRedelegation(trans.isRedelegation());
            retTransaction.setComment(trans.getComment());
            retTransaction.setUsdocNotes(trans.getUsdocNotes());
            markModified(retTransaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
    }

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            if (TransactionState.Name.PENDING_USDOC_APPROVAL == transaction.getState().getName()) {
                throw new org.iana.rzm.facade.system.trans.IllegalTransactionStateException(id, ""+transaction.getState().getName());
            }
            String transition = "admin-accept";
            if (TransactionState.Name.PENDING_EVALUATION == transaction.getState().getName()) {
                transition = "accept";
            }
            transaction.transit(authUser.getUserName(), transition);
            markModified(transaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e.getMessage());
        }
    }

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException, InfrastructureException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(authUser.getUserName(), "admin-reject");
            markModified(transaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e.getMessage());
        }
    }

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws NoObjectFoundException, InfrastructureException, AccessDeniedException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(authUser.getUserName(), transitionName);
            markModified(transaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e.getMessage());
        }
    }

    public void deleteTransaction(TransactionVO transactionVO, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        CheckTool.checkNull(transactionVO, "transactionVO");
        deleteTransaction(transactionVO.getTransactionID(), authUser);
    }

    public void deleteTransaction(long transactionId, AuthenticatedUser authUser) throws NoObjectFoundException, AccessDeniedException {
        try {
            transactionManager.deleteTransaction(transactionId);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
    }


    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return super.getByTicketID(id, authUser);
    }

    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        return super.get(id, authUser);
    }

    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        super.acceptTransaction(id, token, authUser);
    }

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        super.rejectTransaction(id, token, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return super.createTransactions(domain, splitNameServerChange, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return super.createTransactions(domain, splitNameServerChange, submitterEmail, authUser);
    }

    public List<TransactionVO> find(Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return find(null, order, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit, authUser);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit, authUser);
    }

    public int count(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException {
        return transactionManager.count(criteria);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return TransactionConverter.toTransactionVOList(transactionManager.find(criteria, offset, limit));
    }

    public List<TransactionVO> find(Criterion criteria, AuthenticatedUser authUser) throws AccessDeniedException, InfrastructureException {
        return TransactionConverter.toTransactionVOList(transactionManager.find(criteria));
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return super.createTransactions(domain, authUser);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        return super.createTransactions(domain, splitNameServerChange, submitterEmail, performTechnicalCheck, comment, authUser);
    }


    public void approveByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            if (transaction.getState().getName() != TransactionState.Name.PENDING_USDOC_APPROVAL) throw new org.iana.rzm.facade.system.trans.IllegalTransactionStateException(transaction.getTransactionID(), ""+transaction.getState().getName());
            transaction.transit(authUser.getUserName(), "accept");
            markModified(transaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    public void rejectByUSDoC(long id, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            if (transaction.getState().getName() != TransactionState.Name.PENDING_USDOC_APPROVAL) throw new org.iana.rzm.facade.system.trans.IllegalTransactionStateException(transaction.getTransactionID(), ""+transaction.getState().getName());
            transaction.transit(authUser.getUserName(), "reject");
            markModified(transaction, authUser);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    public void confirmByUSDoC(long id, boolean nsChange, boolean accept, AuthenticatedUser authUser) throws NoObjectFoundException, org.iana.rzm.facade.system.trans.IllegalTransactionStateException, AccessDeniedException, InfrastructureException {
        Transaction transaction;
        try {
            transaction = transactionManager.getTransaction(id);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException("transaction", "" + e.getId());
        }
        try {
            if (transaction.getState().getName() != TransactionState.Name.PENDING_USDOC_APPROVAL) throw new org.iana.rzm.facade.system.trans.IllegalTransactionStateException(transaction.getTransactionID(), ""+transaction.getState().getName());

            transaction.confirmChangeByUSDoC(authUser.getUserName(),
                    nsChange ? TransactionChangeType.NAMESERVER_CHANGE : TransactionChangeType.DATABASE_CHANGE,
                    accept);
            markModified(transaction, authUser);
        } catch (USDoCConfirmationAlreadyReceived e) {
            logger.warn("USDoC confirmation for " + id + " nsChange: " + nsChange + " acceptance: " + accept + " has been already received.");
        } catch (USDoCConfirmationMismatch e) {
            try {
                transaction.setStateMessage("Received both: accept and reject from USDoC");
                transaction.transitTo(userManager.get(authUser.getUserName()), TransactionState.Name.EXCEPTION.toString());
            } catch (TransactionException e1) {
                throw new InfrastructureException(e);
            }
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }
}
