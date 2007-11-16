package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.Order;
import org.iana.criteria.SortCriterion;
import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.domain.converters.DomainFromVOConverter;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.converters.TransactionConverter;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.dns.DNSConverter;
import org.iana.rzm.user.UserManager;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class TransactionServiceImpl extends AbstractRZMStatefulService implements TransactionService {

    protected TransactionManager transactionManager;

    protected DomainManager domainManager;

    protected TransactionDetectorService transactionDetectorService;

    protected DNSTechnicalCheck technicalCheck;

    public TransactionServiceImpl(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager, TransactionDetectorService transactionDetectorService, DNSTechnicalCheck dnsTechnicalCheck) {
        super(userManager);
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(transactionDetectorService, "change detector");
        CheckTool.checkNull(dnsTechnicalCheck, "technical check");
        this.transactionManager = transactionManager;
        this.domainManager = domainManager;
        this.transactionDetectorService = transactionDetectorService;
        this.technicalCheck = dnsTechnicalCheck;
    }

    public TransactionVO get(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            return TransactionConverter.toTransactionVO(transactionManager.getTransaction(id));
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        }
    }

    public List<TransactionVO> getByTicketID(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        Criterion ticketID = new Equal(TransactionCriteriaFields.TICKET_ID, id);
        List<Transaction> trans = transactionManager.find(ticketID);
        return TransactionConverter.toTransactionVOList(trans);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        try {
            return createTransactions(domain, false, null, false, null);
        } catch (DNSTechnicalCheckException e) {
            // impossible
            throw new IllegalStateException("should not perform technical check");
        }
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        try {
            return createTransactions(domain, splitNameServerChange, null, false, null);
        } catch (DNSTechnicalCheckException e) {
            // impossible
            throw new IllegalStateException("should not perform technical check");
        }
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException {
        try {
            return createTransactions(domain, splitNameServerChange, submitterEmail, false, null);
        } catch (DNSTechnicalCheckException e) {
            // impossible
            throw new IllegalStateException("should not perform technical check");
        }
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, boolean performTechnicalCheck, String comment) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckException {
        CheckTool.checkNull(domain, "null domain");

        try {
            Domain currentDomain = domainManager.get(domain.getName());
            if (currentDomain == null) throw new NoObjectFoundException(domain.getName(), "domain");

            Domain modifiedDomain = DomainFromVOConverter.toDomain(domain);

            List<TransactionVO> ret = new ArrayList<TransactionVO>();
            TransactionActionsVO actions = transactionDetectorService.detectTransactionActions(domain);

            if (actions.containsNameServerAction() && performTechnicalCheck) {
                performTechnicalCheck(modifiedDomain);
            }

            for (TransactionActionGroupVO group : actions.getGroups()) {
                if (group.isSplittable() &&
                        group.containsNameServerAction() &&
                        group.containsOtherAction() &&
                        splitNameServerChange) {
                    List<TransactionActionVO> tactions = new ArrayList<TransactionActionVO>();
                    for (TransactionActionVO action : group.getActions()) {
                        tactions.add(action);
                        ret.add(createTransaction(currentDomain, modifiedDomain, tactions, submitterEmail, performTechnicalCheck, comment));
                        tactions.clear();
                    }
                } else {
                    ret.add(createTransaction(currentDomain, modifiedDomain, group.getActions(), submitterEmail, performTechnicalCheck, comment));
                }
            }

            return ret;
        } catch (NoModificationException e) {
            throw new NoDomainModificationException(domain.getName());
        } catch (CloneNotSupportedException e) {
            throw new InfrastructureException(e);
        }
    }

    private TransactionVO createTransaction(Domain currentDomain, Domain modifiedDomain, List<TransactionActionVO> actions, String submitterEmail, boolean performTechnicalCheck, String comment) throws NoModificationException, CloneNotSupportedException {
        Domain md = currentDomain.clone();
        for (TransactionActionVO action : actions) {
            if (TransactionActionVO.MODIFY_TC.equals(action.getName())) {
                md.setTechContact(modifiedDomain.getTechContact());
            } else if (TransactionActionVO.MODIFY_SO.equals(action.getName())) {
                md.setSupportingOrg(modifiedDomain.getSupportingOrg());
            } else if (TransactionActionVO.MODIFY_AC.equals(action.getName())) {
                md.setAdminContact(modifiedDomain.getAdminContact());
            } else if (TransactionActionVO.MODIFY_REGISTRATION_URL.equals(action.getName())) {
                md.setRegistryUrl(modifiedDomain.getRegistryUrl());
            } else if (TransactionActionVO.MODIFY_WHOIS_SERVER.equals(action.getName())) {
                md.setWhoisServer(modifiedDomain.getWhoisServer());
            } else if (TransactionActionVO.MODIFY_NAME_SERVERS.equals(action.getName())) {
                for (ChangeVO vo : action.getChange()) {
                    ObjectValueVO val = (ObjectValueVO) vo.getValue();
                    if (ChangeVO.Type.ADDITION.equals(vo.getType())) {
                        md.addNameServer(modifiedDomain.getNameServer(val.getName()));
                    } else if (ChangeVO.Type.REMOVAL.equals(vo.getType())) {
                        md.removeNameServer(val.getName());
                    } else if (ChangeVO.Type.UPDATE.equals(vo.getType())) {
                        {
                            md.setNameServer(modifiedDomain.getNameServer(val.getName()));
                        }
                    }
                }
            }
        }
        Transaction trans = transactionManager.createDomainModificationTransaction(md, submitterEmail);
        trans.setCreated(now());
        trans.setCreatedBy(user.getUserName());
        trans.setComment(comment);
        return TransactionConverter.toTransactionVO(trans);
    }

    final protected Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    final protected void markModified(Transaction trans) {
        trans.setModified(now());
        trans.setModifiedBy(user.getUserName());
    }

    public void moveTransactionToNextState(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.accept(getRZMUser());
            markModified(trans);
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

    public void acceptTransaction(long id) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.accept(getRZMUser());
            trans.setModified(now());
            trans.setModifiedBy(user.getUserName());
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
            Transaction trans = transactionManager.getTransaction(id);
            trans.reject(getRZMUser());
            markModified(trans);
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

    //
    // Finder interface
    //

    public List<TransactionVO> find(Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return find(null, order, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, Order order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit);
    }

    public List<TransactionVO> find(Criterion criteria, List<Order> order, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        Criterion searchCriteria = criteria;
        if (order != null) searchCriteria = new SortCriterion(criteria, order);
        return find(searchCriteria, offset, limit);
    }

    public int count(Criterion criteria) throws AccessDeniedException {
        return transactionManager.count(criteria);
    }

    public List<TransactionVO> find(Criterion criteria, int offset, int limit) throws AccessDeniedException, InfrastructureException {
        return TransactionConverter.toTransactionVOList(transactionManager.find(criteria, offset, limit));
    }

    public List<TransactionVO> find(Criterion criteria) throws AccessDeniedException, InfrastructureException {
        List<TransactionVO> ret = new ArrayList<TransactionVO>();
        Set<String> domainNames = getRoleDomainNames();
        for (Transaction trans : transactionManager.find(criteria)) {
            if (domainNames.contains(trans.getCurrentDomain().getName())) {
                TransactionVO transactionVO = TransactionConverter.toTransactionVO(trans);
                if (!ret.contains(transactionVO)) ret.add(transactionVO);
            }
        }
        return ret;
    }


    public void acceptTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.accept(new ContactIdentity(token));
            trans.setModified(now());
            // set to the role
            trans.setModifiedBy(token);
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

    public void rejectTransaction(long id, String token) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.reject(new ContactIdentity(token));
            // set to the role
            trans.setModified(now());
            trans.setModifiedBy(token);
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
            Transaction trans = transactionManager.getTransaction(id);
            trans.transit(getRZMUser(), transitionName);
            markModified(trans);
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

    private void performTechnicalCheck(Domain domain) throws DNSTechnicalCheckException {
        DNSDomain dns = DNSConverter.toDNSDomain(domain);
        technicalCheck.check(dns);
    }


    static Set allowToWithdraw = new HashSet();

    static {
        allowToWithdraw.addAll(Arrays.asList(
                TransactionState.Name.PENDING_CREATION,
                TransactionState.Name.PENDING_TECH_CHECK,
                TransactionState.Name.PENDING_TECH_CHECK_REMEDY,
                TransactionState.Name.PENDING_CONTACT_CONFIRMATION,
                TransactionState.Name.PENDING_SOENDORSEMENT,
                TransactionState.Name.PENDING_IMPACTED_PARTIES,
                TransactionState.Name.PENDING_MANUAL_REVIEW,
                TransactionState.Name.PENDING_EXT_APPROVAL,
                TransactionState.Name.PENDING_EVALUATION,
                TransactionState.Name.PENDING_IANA_CHECK,
                TransactionState.Name.PENDING_SUPP_TECH_CHECK,
                TransactionState.Name.PENDING_SUPP_TECH_CHECK_REMEDY));
    }

    public void withdrawTransaction(long id) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            if (trans == null) throw new NoObjectFoundException(id, "transaction");
            if (!isAllowedToWithdraw(trans))
                throw new TransactionCannotBeWithdrawnException(id, "" + trans.getState().getName());
            trans.transitTo(getRZMUser(), TransactionState.Name.WITHDRAWN.toString());
            markModified(trans);
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        } catch (TransactionException e) {
            throw new InfrastructureException(e);
        }
    }

    private boolean isAllowedToWithdraw(Transaction trans) {
        return trans != null && allowToWithdraw.contains(trans.getState().getName());
    }
}
