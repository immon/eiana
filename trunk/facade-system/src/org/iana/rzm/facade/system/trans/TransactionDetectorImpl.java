package org.iana.rzm.facade.system.trans;

import org.iana.dns.check.exceptions.RadicalAlterationCheckException;
import org.iana.objectdiff.*;
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
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.trans.check.RadicalAlteration;
import org.iana.rzm.user.UserManager;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class TransactionDetectorImpl extends AbstractRZMStatefulService implements TransactionDetectorService {

    private DomainManager domainManager;
    private RadicalAlteration radicalCheck;

    private DiffConfiguration diffConfiguration;

    public TransactionDetectorImpl(UserManager userManager, DomainManager domainManager, DiffConfiguration diffConfiguration, RadicalAlteration radicalCheck) {
        super(userManager);
        this.domainManager = domainManager;
        this.radicalCheck = radicalCheck;
        this.diffConfiguration = diffConfiguration;
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        return detectTransactionActions(domain, diffConfiguration);
    }

    public TransactionActionsVO detectTransactionActions(IDomainVO domain, DiffConfiguration config) throws AccessDeniedException, NoObjectFoundException, InfrastructureException, InvalidCountryCodeException, SharedNameServersCollisionException, RadicalAlterationException {
        CheckTool.checkNull(domain, "null domain");

        Domain currentDomain = domainManager.get(domain.getName());
        if (currentDomain == null) throw new NoObjectFoundException(domain.getName(), "domain");

        TransactionActionsVO ret = new TransactionActionsVO();
        Domain modifiedDomain = DomainFromVOConverter.toDomain(domain);
        ObjectChange change = (ObjectChange) ChangeDetector.diff(currentDomain, modifiedDomain, config);

        checkForRadicalAlteration(modifiedDomain);
        detectNameServersCollision(change);

        List<TransactionActionVO> actions = TransactionConverter.toTransactionActionVO(change);
        ret.setGroups(createTransactionGroups(domain.getName(), actions));

        return ret;
    }

    private void checkForRadicalAlteration(Domain modifiedDomain) throws RadicalAlterationException {
        try {
            radicalCheck.check(modifiedDomain);
        } catch (RadicalAlterationCheckException e) {
            throw new RadicalAlterationException(e.getDomainName());
        }
    }

    private void detectNameServersCollision(ObjectChange change) throws SharedNameServersCollisionException {
        Set<String> nameServers = new HashSet<String>();
        Map<String, Change> fieldChanges = change.getFieldChanges();
        if (fieldChanges.containsKey("nameServers")) {
            CollectionChange nameServersChange = (CollectionChange) fieldChanges.get("nameServers");
            for (Change c : nameServersChange.getAdded()) {
                if (c.isModification()) {
                    nameServers.add(((ObjectChange) c).getId());
                }
            }
        }
        if(!nameServers.isEmpty())
            throw new SharedNameServersCollisionException(nameServers);
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
                if (change.getType() == ChangeVO.Type.UPDATE) {
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

}
