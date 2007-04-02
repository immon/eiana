package org.iana.rzm.facade.system.trans;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.domain.DomainVO;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.trans.*;
import org.iana.rzm.user.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SystemTransactionServiceBean extends AbstractRZMStatefulService implements SystemTransactionService {

    private TransactionManager transactionManager;
    private DomainManager domainManager;


    public SystemTransactionServiceBean(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(domainManager, "domain manager");
        this.transactionManager = transactionManager;
        this.domainManager = domainManager;
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            return TransactionConverter.toTransactionVO(transactionManager.getTransaction(id));
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        }
    }

    public List<SimpleTransactionVO> findOpenTransactions() throws AccessDeniedException, InfrastructureException {
        List<SimpleTransactionVO> ret = new ArrayList<SimpleTransactionVO>();
        Set<String> domainNames = getRoleDomainNames();
        for (Transaction trans : transactionManager.findTransactions(domainNames)) {
            ret.add(TransactionConverter.toSimpleTransactionVO(trans));
        }
        return ret;
    }

    public void performTransactionTechnicalCheck(DomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {
        throw new UnsupportedOperationException();
    }

    public List<TransactionSplitVO> getPossibleTransactionSplits(DomainVO domain) throws AccessDeniedException, InfrastructureException {
        throw new UnsupportedOperationException();
    }

    public TransactionVO createTransaction(DomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        CheckTool.checkNull(domain, "domain");
        if (domainManager.get(domain.getName()) == null) throw new NoObjectFoundException(domain.getName(), "domain");
        Domain modifiedDomain = FromVOConverter.toDomain(domain);
        Transaction trans = transactionManager.createDomainModificationTransaction(modifiedDomain);
        return TransactionConverter.toTransactionVO(trans);
    }

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.accept(getUser());
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        } catch (UserAlreadyAccepted e) {
            // do nothing
        } catch (UserConfirmationNotExpected e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    public void rejectTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.reject(getUser());
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        } catch (UserAlreadyAccepted e) {
            // do nothing
        } catch (UserConfirmationNotExpected e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction transaction = transactionManager.getTransaction(id);
            transaction.transit(getUser(), transitionName);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        } catch (UserAlreadyAccepted e) {
            // do nothing
        } catch (UserConfirmationNotExpected e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }
}
