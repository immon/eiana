package org.iana.rzm.facade.system.trans;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.AbstractRZMStatefulService;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.converter.FromVOConverter;
import org.iana.rzm.facade.system.domain.TechnicalCheckException;
import org.iana.rzm.facade.system.domain.IDomainVO;
import org.iana.rzm.trans.*;
import org.iana.rzm.user.UserManager;
import org.iana.objectdiff.*;

import java.util.*;
import java.sql.Timestamp;

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
        for (Transaction trans : transactionManager.findOpenTransactions(domainNames)) {
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
            trans.setCreated(now());
            trans.setCreatedBy(user.getUserName());
            return TransactionConverter.toTransactionVO(trans);
        } catch (NoModificationException e) {
            throw new NoDomainModificationException(modifiedDomain.getName());
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

    public void transitTransaction(long id, String transitionName) throws AccessDeniedException, NoObjectFoundException, InfrastructureException {
        try {
            Transaction trans = transactionManager.getTransaction(id);
            trans.transit(getRZMUser(), transitionName);
            trans.setModified(now());
            trans.setModifiedBy(user.getUserName());
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
        ret.setGroups(createTransactionGroups(domain.getName(), actions));

        return ret;
    }

    public List<TransactionVO> createTransactions(IDomainVO domain, boolean splitNameServerChange) throws AccessDeniedException, NoObjectFoundException, NoDomainModificationException, InfrastructureException {
        CheckTool.checkNull(domain, "null domain");

        try {
            Domain currentDomain = domainManager.get(domain.getName());
            if (currentDomain == null) throw new NoObjectFoundException(domain.getName(), "domain");

            Domain modifiedDomain = FromVOConverter.toDomain(domain);

            List<TransactionVO> ret = new ArrayList<TransactionVO>();
            TransactionActionsVO actions = detectTransactionActions(domain);

            for (TransactionActionGroupVO group : actions.getGroups()) {
                if (group.isSplittable() &&
                        group.containsNameServerAction() &&
                        group.containsOtherAction() &&
                        splitNameServerChange) {
                    List<TransactionActionVO> tactions = new ArrayList<TransactionActionVO>();
                    for (TransactionActionVO action : group.getActions()) {
                        tactions.add(action);
                        ret.add(createTransaction(currentDomain, modifiedDomain, tactions));
                        tactions.clear();
                    }
                } else {
                    ret.add(createTransaction(currentDomain, modifiedDomain, group.getActions()));
                }
            }
            return ret;
        } catch (NoModificationException e) {
            throw new NoDomainModificationException(domain.getName());
        } catch (CloneNotSupportedException e) {
            throw new InfrastructureException(e);
        }
    }

    private TransactionVO createTransaction(Domain currentDomain, Domain modifiedDomain, List<TransactionActionVO> actions) throws NoModificationException, CloneNotSupportedException {
        Domain md = currentDomain.clone();
        for (TransactionActionVO action : actions) {
            if (TransactionActionVO.MODIFY_TC.equals(action.getName())) {
                md.setTechContacts(modifiedDomain.getTechContacts());
            } else if (TransactionActionVO.MODIFY_SO.equals(action.getName())) {
                md.setSupportingOrg(modifiedDomain.getSupportingOrg());
            } else if (TransactionActionVO.MODIFY_AC.equals(action.getName())) {
                md.setAdminContacts(modifiedDomain.getAdminContacts());
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
        Transaction trans = transactionManager.createDomainModificationTransaction(md);
        trans.setCreated(now());
        trans.setCreatedBy(user.getUserName());
        return TransactionConverter.toTransactionVO(trans);
    }

    private List<TransactionActionGroupVO> createTransactionGroups(String domainName, List<TransactionActionVO> actions) {
        List<TransactionActionGroupVO> ret = new ArrayList<TransactionActionGroupVO>();
        TransactionActionGroupVO splittableGroup = new TransactionActionGroupVO(true);

        for (TransactionActionVO action : actions) {
            if (TransactionActionVO.MODIFY_NAME_SERVERS.equals(action.getName())) {
                ret.addAll(splitNameServerAction(domainName, action, splittableGroup));
            } else {
                splittableGroup.addAction(action);
            }
        }
        if (!splittableGroup.isEmpty()) ret.add(splittableGroup);

        return ret;
    }

    private List<TransactionActionGroupVO> splitNameServerAction(String domainName, TransactionActionVO action, TransactionActionGroupVO group) {
        List<ChangeVO> changes = action.getChange();
        Map<String, ChangeVO> hostToChanges = new HashMap<String, ChangeVO>();
        Map<String, Set<String>> hostToDomains = new HashMap<String, Set<String>>();
        for (ChangeVO change : changes) {
            if ("nameServers".equals(change.getFieldName())) {
                ObjectValueVO value = (ObjectValueVO) change.getValue();
                String hostName = value.getName();
                if (change.getType() == ChangeVO.Type.ADDITION ||
                        change.getType() == ChangeVO.Type.UPDATE) {
                    Set<String> domainNames = findDelegatedTo(hostName);
                    domainNames.remove(domainName);
                    if (!domainNames.isEmpty()) {
                        hostToDomains.put(hostName, domainNames);
                    }
                }
                hostToChanges.put(hostName, change);
            }
        }

        List<TransactionActionGroupVO> ret = new ArrayList<TransactionActionGroupVO>();
        List<Set<String>> hostGroups = findHostGroups(hostToDomains);
        for (Set<String> hostGroup : hostGroups) {
            TransactionActionVO groupAction = new TransactionActionVO(action.getName());
            for (String hostName : hostGroup) {
                groupAction.addChange(hostToChanges.get(hostName));
                hostToChanges.remove(hostName);
            }
            ret.add(new TransactionActionGroupVO(groupAction));
        }
        if (!hostToChanges.isEmpty()) {
            TransactionActionVO groupAction = new TransactionActionVO(action.getName());
            groupAction.setChange(new ArrayList<ChangeVO>(hostToChanges.values()));
            group.addAction(groupAction);
        }
        return ret;
    }

    private Set<String> findDelegatedTo(String hostName) {
        Set<String> hostNames = new HashSet<String>();
        hostNames.add(hostName);
        List<Domain> domains = domainManager.findDelegatedTo(hostNames);
        Set<String> domainNames = new HashSet<String>();
        for (Domain domain : domains) {
            domainNames.add(domain.getName());
        }
        return domainNames;
    }

    private List<Set<String>> findHostGroups(Map<String, Set<String>> hostToDomains) {
        List<Set<String>> ret = new ArrayList<Set<String>>();
        Set<String> hostNames = new HashSet<String>(hostToDomains.keySet());
        for (String hostName : hostToDomains.keySet()) {
            Set<String> hostGroup = new HashSet<String>();
            hostGroup.add(hostName);
            hostNames.remove(hostName);

            Set<String> domainNames = hostToDomains.get(hostName);
            for (String otherHostName : hostNames) {
                Set<String> otherDomainNames = hostToDomains.get(otherHostName);
                if (domainNames.equals(otherDomainNames)) {
                    hostGroup.add(otherHostName);
                }
            }

            hostNames.removeAll(hostGroup);
            ret.add(hostGroup);
        }
        return ret;
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

}
