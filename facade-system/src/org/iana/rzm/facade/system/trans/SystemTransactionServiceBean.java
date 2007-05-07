package org.iana.rzm.facade.system.trans;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.trans.*;
import org.iana.rzm.user.UserManager;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.ChangeDetector;
import org.iana.objectdiff.DiffConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SystemTransactionServiceBean extends AbstractRZMStatefulService implements SystemTransactionService {

    private TransactionManager transactionManager;
    private DomainManager domainManager;
    private DiffConfiguration diffConfiguration;


    public SystemTransactionServiceBean(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager, DiffConfiguration diffConfiguration) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(diffConfiguration, "diff configuration");
        this.transactionManager = transactionManager;
        this.domainManager = domainManager;
        this.diffConfiguration = diffConfiguration;
    }

    public TransactionVO getTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            return TransactionConverter.toTransactionVO(transactionManager.getTransaction(id));
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        }
    }

    public List<TransactionVO> findOpenTransactions() throws AccessDeniedException, InfrastructureException {
        List<TransactionVO> ret = new ArrayList<TransactionVO>();
        Set<String> domainNames = getRoleDomainNames();
        for (Transaction trans : transactionManager.findTransactions(domainNames)) {
            ret.add(TransactionConverter.toTransactionVO(trans));
        }
        return ret;
    }

    public void performTransactionTechnicalCheck(IDomainVO domain) throws AccessDeniedException, TechnicalCheckException, InfrastructureException {
        throw new UnsupportedOperationException();
    }

    public TransactionVO createTransaction(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException {
        CheckTool.checkNull(domain, "domain");
        if (domainManager.get(domain.getName()) == null) throw new NoObjectFoundException(domain.getName(), "domain");
        Domain modifiedDomain = FromVOConverter.toDomain(domain);
        return createTransaction(modifiedDomain);
    }

    private TransactionVO createTransaction(Domain modifiedDomain) throws NoDomainModificationException {
        try {
            Transaction trans = transactionManager.createDomainModificationTransaction(modifiedDomain);
            return TransactionConverter.toTransactionVO(trans);
        } catch (NoModificationException e) {
            throw new NoDomainModificationException(modifiedDomain.getName());
        }
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
        } catch (UserNotAuthorizedToTransit e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    public List<TransactionVO> findTransactions(TransactionCriteriaVO criteria) throws AccessDeniedException, InfrastructureException {
        List<TransactionVO> ret = new ArrayList<TransactionVO>();
        Set<String> domainNames = getRoleDomainNames();
        TransactionCriteria transactionCriteria = TransactionCriteriaConverter.convert(criteria);
        for (Transaction trans : transactionManager.find(transactionCriteria)) {
            if (domainNames.contains(trans.getCurrentDomain().getName()))
                ret.add(TransactionConverter.toTransactionVO(trans));
        }
        return ret;
    }

   public TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        CheckTool.checkNull(domain, "null domain");

        Domain currentDomain = domainManager.get(domain.getName());
        if (currentDomain == null) throw new NoObjectFoundException(domain.getName(), "domain");

        TransactionActionsVO ret = new TransactionActionsVO();
        Domain modifiedDomain = FromVOConverter.toDomain(domain);
        ObjectChange change = (ObjectChange) ChangeDetector.diff(currentDomain, modifiedDomain, diffConfiguration);
        List<TransactionActionVO> actions = TransactionConverter.toTransactionActionVO(change);
        ret.setActions(actions);
        return ret;
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException {
        CheckTool.checkNull(domain, "null domain");

        Domain currentDomain = domainManager.get(domain.getName());
        if (currentDomain == null) throw new NoObjectFoundException(domain.getName(), "domain");

        List<TransactionVO> ret = new ArrayList<TransactionVO>();
        TransactionActionsVO actions = detectTransactionActions(domain);

        if (actions.containsNameServerAction() && actions.containsOtherAction() && splitNameServerChange) {
            // no name server change
            Domain modifiedDomain = FromVOConverter.toDomain(domain);
            List<Host> nameServers = modifiedDomain.getNameServers();
            modifiedDomain.setNameServers(currentDomain.getNameServers());
            ret.add(createTransaction(modifiedDomain));

            // only name server change
            Domain modifiedNsDomain = currentDomain.clone();
            modifiedNsDomain.setNameServers(nameServers);
            ret.add(createTransaction(modifiedDomain));
        } else {
            ret.add(createTransaction(domain));
        }
        return ret;
    }}
