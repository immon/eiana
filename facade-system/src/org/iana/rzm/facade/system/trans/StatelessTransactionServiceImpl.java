package org.iana.rzm.facade.system.trans;

import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.Order;
import org.iana.criteria.SortCriterion;
import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSTechnicalCheck;
import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.objectdiff.*;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.domain.converters.DomainFromVOConverter;
import org.iana.rzm.facade.system.domain.vo.IDomainVO;
import org.iana.rzm.facade.system.trans.converters.TransactionConverter;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.dns.DNSConverter;
import org.iana.rzm.user.UserManager;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessTransactionServiceImpl implements StatelessTransactionService {

    protected TransactionManager transactionManager;

    protected DomainManager domainManager;

    protected TransactionDetectorService transactionDetectorService;

    protected DNSTechnicalCheck technicalCheck;

    protected DNSTechnicalCheck technicalCheckNoRadicalAlteration;

    private UserManager userManager;

    private DiffConfiguration diffConfiguration;

    public StatelessTransactionServiceImpl(UserManager userManager, TransactionManager transactionManager, DomainManager domainManager, TransactionDetectorService transactionDetectorService, DNSTechnicalCheck dnsTechnicalCheck, DNSTechnicalCheck dnsTechnicalCheckNoRA, DiffConfiguration diffConfiguration) {
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(domainManager, "domain manager");
        CheckTool.checkNull(transactionDetectorService, "change detector");
        CheckTool.checkNull(dnsTechnicalCheck, "technical check");
        CheckTool.checkNull(dnsTechnicalCheckNoRA, "technical check no radical alteration");
        CheckTool.checkNull(diffConfiguration, "diff configuration");
        this.userManager = userManager;
        this.transactionManager = transactionManager;
        this.domainManager = domainManager;
        this.transactionDetectorService = transactionDetectorService;
        this.technicalCheck = dnsTechnicalCheck;
        this.technicalCheckNoRadicalAlteration = dnsTechnicalCheckNoRA;
        this.diffConfiguration = diffConfiguration;
    }

    public TransactionVO get(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            return TransactionConverter.toTransactionVO(transactionManager.getTransaction(id));
        } catch (NoSuchTransactionException e) {
            throw new NoObjectFoundException(id, "transaction");
        }
    }

    public List<TransactionVO> getByTicketID(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        Criterion ticketID = new Equal(TransactionCriteriaFields.TICKET_ID, id);
        List<Transaction> trans = transactionManager.find(ticketID);
        return TransactionConverter.toTransactionVOList(trans);
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        try {
            return createTransactions(domain, false, null, PerformTechnicalCheck.OFF, null, authUser);
        } catch (DNSTechnicalCheckExceptionWrapper e) {
            // impossible
            throw new IllegalStateException("should not perform technical check");
        }
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        try {
            return createTransactions(domain, splitNameServerChange, null, PerformTechnicalCheck.OFF, null, authUser);
        } catch (DNSTechnicalCheckExceptionWrapper e) {
            // impossible
            throw new IllegalStateException("should not perform technical check");
        }
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {
        try {
            return createTransactions(domain, splitNameServerChange, submitterEmail, PerformTechnicalCheck.OFF, null, authUser);
        } catch (DNSTechnicalCheckExceptionWrapper e) {
            // impossible
            throw new IllegalStateException("should not perform technical check");
        }
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange, String submitterEmail, PerformTechnicalCheck performTechnicalCheck, String comment, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException, InvalidCountryCodeException, DNSTechnicalCheckExceptionWrapper, TransactionExistsException, NameServerChangeNotAllowedException, SharedNameServersCollisionException, RadicalAlterationException {

        CheckTool.checkNull(domain, "null domain");

        existsTransaction(domain.getName());
        try {
            Domain currentDomain = domainManager.get(domain.getName());
            if (currentDomain == null) throw new NoObjectFoundException(domain.getName(), "domain");

            Domain modifiedDomain = DomainFromVOConverter.toDomain(domain);

            List<TransactionVO> ret = new ArrayList<TransactionVO>();
            TransactionActionsVO actions = transactionDetectorService.detectTransactionActions(domain, performTechnicalCheck);

            if (actions.containsNameServerAction()) {
                performTechnicalCheck(modifiedDomain, performTechnicalCheck);
            }

            for (TransactionActionGroupVO group : actions.getGroups()) {
                if (group.isSplittable() &&
                        group.containsNameServerAction() &&
                        group.containsOtherAction() &&
                        splitNameServerChange) {
                    List<TransactionActionVO> tactions = new ArrayList<TransactionActionVO>();
                    for (TransactionActionVO action : group.getActions()) {
                        tactions.add(action);
                        ret.add(createTransaction(currentDomain, modifiedDomain, tactions, submitterEmail, performTechnicalCheck, comment, authUser));
                        tactions.clear();
                    }
                } else {
                    ret.add(createTransaction(currentDomain, modifiedDomain, group.getActions(), submitterEmail, performTechnicalCheck, comment, authUser));
                }
            }

            return ret;
        } catch(DNSTechnicalCheckException e){
            throw new DNSTechnicalCheckExceptionWrapper(e);
        } catch (NoModificationException e) {
            throw new NoDomainModificationException(domain.getName());
        } catch (CloneNotSupportedException e) {
            throw new InfrastructureException(e);
        }
    }

    private void existsTransaction(String domainName) throws TransactionExistsException {
        List<Transaction> list = transactionManager.findOpenTransactions(domainName);
        if (list.size() > 0) 
            throw new TransactionExistsException(domainName);
    }

    private TransactionVO createTransaction(Domain currentDomain, Domain modifiedDomain, List<TransactionActionVO> actions, String submitterEmail, PerformTechnicalCheck performTechnicalCheck, String comment, AuthenticatedUser authUser) throws NoModificationException, CloneNotSupportedException, NameServerChangeNotAllowedException {

        Domain md = currentDomain.clone();

        Set<String> alreadyUsedNameServers = new HashSet<String>();

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
                    if (ChangeVO.Type.ADDITION.equals(vo.getType()) || vo.isAddition()) {
                        md.addNameServer(modifiedDomain.getNameServer(val.getName()));
                        List<Transaction> transactions = transactionManager.findOpenForNameServer(val.getName());
                        for (Transaction trans : transactions) {
                            ObjectChange domainChange = (ObjectChange) ChangeDetector.diff(currentDomain, modifiedDomain, diffConfiguration);
                            ObjectChange transDomainChange = trans.getDomainChange();
                            if (!isSameNameServerAddition(domainChange, transDomainChange, val.getName())) {
                                alreadyUsedNameServers.add(val.getName());
                            }
                        }
                    } else if (ChangeVO.Type.REMOVAL.equals(vo.getType())) {
                        md.removeNameServer(val.getName());
                        if (isNameServerAlreadyInUse(val.getName())) {
                            alreadyUsedNameServers.add(val.getName());
                        }
                    } else if (ChangeVO.Type.UPDATE.equals(vo.getType())) {
                        md.setNameServer(modifiedDomain.getNameServer(val.getName()));
                        if (isNameServerAlreadyInUse(val.getName())) {
                            alreadyUsedNameServers.add(val.getName());
                        }
                    }
                }
            }
        }

        if (!alreadyUsedNameServers.isEmpty())
            throw new NameServerChangeNotAllowedException("for: " + alreadyUsedNameServers);

        Transaction trans = transactionManager.createDomainModificationTransaction(md, submitterEmail, authUser.getUserName());
        trans.setComment(comment);
        return TransactionConverter.toTransactionVO(trans);
    }

    private boolean isSameNameServerAddition(ObjectChange current, ObjectChange existing, String nameServer) {
        ObjectChange currentObjChange = getNameServerChangeFromObjectChange(current, nameServer);
        ObjectChange existingObjChange = getNameServerChangeFromObjectChange(existing, nameServer);

        return (currentObjChange.equals(existingObjChange));
    }

    private ObjectChange getNameServerChangeFromObjectChange(ObjectChange objectChange, String nameServer) {
        Map<String, Change> fieldChanges = objectChange.getFieldChanges();
            if (fieldChanges.containsKey("nameServers")) {
                CollectionChange nameServersChange = (CollectionChange) fieldChanges.get("nameServers");
                for (Change c : nameServersChange.getAdded()) {
                    ObjectChange oc = ((ObjectChange) c);
                    if (nameServer.equals(oc.getId())) {
                        return oc;
                    }
                }
            }

        return null;
    }

    private boolean isNameServerAlreadyInUse(String nameServer) {
        List<Transaction> transactions = transactionManager.findOpenForNameServer(nameServer);
        return (transactions != null && !transactions.isEmpty());
    }

    final protected Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    final protected void markModified(Transaction trans, AuthenticatedUser authUser) {
        trans.setModified(now());
        trans.setModifiedBy(authUser.getUserName());
    }

    public void moveTransactionToNextState(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, IllegalTransactionStateException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.accept(userManager.get(authUser.getUserName()));
            markModified(trans, authUser);
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

    public void acceptTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.accept(userManager.get(authUser.getUserName()));
            trans.setModified(now());
            trans.setModifiedBy(authUser.getUserName());
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

    public void rejectTransaction(long id, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.reject(userManager.get(authUser.getUserName()));
            markModified(trans, authUser);
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
 /*
       List<TransactionVO> ret = new ArrayList<TransactionVO>();
        Set<String> domainNames = getRoleDomainNames();
        for (Transaction trans : transactionManager.find(criteria)) {
            if (domainNames.contains(trans.getCurrentDomain().getName())) {
                TransactionVO transactionVO = TransactionConverter.toTransactionVO(trans);
                if (!ret.contains(transactionVO)) ret.add(transactionVO);
            }
        }
        return ret;
 */
    }


    public void acceptTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.accept(token);
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

    public void rejectTransaction(long id, String token, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.reject(token);
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

    public void transitTransaction(long id, String transitionName, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.transit(authUser.getUserName(), transitionName);
            markModified(trans, authUser);
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

    private void performTechnicalCheck(Domain domain, PerformTechnicalCheck performTechnicalCheck) throws DNSTechnicalCheckException {
        DNSDomain dns = DNSConverter.toDNSDomain(domain);

        if (PerformTechnicalCheck.OFF.equals(performTechnicalCheck)) return;

        if (PerformTechnicalCheck.ON_NO_RADICAL_ALTERATION.equals(performTechnicalCheck)) {
            technicalCheckNoRadicalAlteration.check(dns);
        } else {
            technicalCheck.check(dns);
        }
    }


    static Set<TransactionState.Name> allowToWithdraw = new HashSet<TransactionState.Name>();

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

    public void withdrawTransaction(long id, String reason, AuthenticatedUser authUser) throws AccessDeniedException, NoObjectFoundException, TransactionCannotBeWithdrawnException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            if (trans == null) throw new NoObjectFoundException(id, "transaction");
            if (!isAllowedToWithdraw(trans))
                throw new TransactionCannotBeWithdrawnException(id, "" + trans.getState().getName());
            trans.getData().setWidthdrawnReason(reason);
            trans.transitTo(userManager.get(authUser.getUserName()), TransactionState.Name.WITHDRAWN.toString());
            markModified(trans, authUser);
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
