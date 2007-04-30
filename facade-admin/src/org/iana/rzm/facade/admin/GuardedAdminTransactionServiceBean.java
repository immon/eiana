package org.iana.rzm.facade.admin;

import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.trans.TransactionVO;
import org.iana.rzm.facade.system.trans.TransactionCriteriaVO;
import org.iana.rzm.facade.system.trans.TransactionConverter;
import org.iana.rzm.facade.system.trans.TransactionCriteriaConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionCriteria;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */
public class GuardedAdminTransactionServiceBean extends AbstractRZMStatefulService implements AdminTransactionService {

    private static Set<Role> allowedRoles = new HashSet<Role>();

    static {
        allowedRoles.add(new AdminRole(AdminRole.AdminType.IANA));
    }

    TransactionManager transactionManager;


    private void isUserInRole() throws AccessDeniedException {
        isUserInRole(allowedRoles);
    }


    public GuardedAdminTransactionServiceBean(UserManager userManager, TransactionManager transactionManager) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        this.transactionManager = transactionManager;
    }

    public TransactionVO getTransaction(long id) throws NoSuchTransactionException {
        isUserInRole();
        Transaction retTransaction = transactionManager.getTransaction(id);
        CheckTool.checkNull(retTransaction, "no such transaction: " + id);
        return TransactionConverter.toTransactionVO(retTransaction);
    }

    public TransactionVO createDomainCreationTransaction(DomainVO domainVO) {
        isUserInRole();
        //todo
        return null;
    }

    public TransactionVO createDomainModificationTransaction(DomainVO domainVO) {
        isUserInRole();
        CheckTool.checkNull(domainVO, "domainVO");
        Domain domain = FromVOConverter.toDomain(domainVO);
        Transaction createdTransaction =
                transactionManager.createDomainModificationTransaction(domain);
        CheckTool.checkNull(createdTransaction, "created modification transaction");
        return TransactionConverter.toTransactionVO(createdTransaction);
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

    public void deleteTransaction(TransactionVO transactionVO) throws NoSuchTransactionException {
        isUserInRole();
        CheckTool.checkNull(transactionVO, "transactionVO");
        deleteTransaction(transactionVO.getTransactionID());
    }

    public void deleteTransaction(long transactionId) throws NoSuchTransactionException {
        isUserInRole();
        transactionManager.deleteTransaction(transactionId);
    }

    public List<TransactionVO> findTransactions(Criterion criteria) {
        // todo
        return null;
    }
}
