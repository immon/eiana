package org.iana.rzm.trans.change;

import org.aspectj.lang.ProceedingJoinPoint;
import org.iana.objectdiff.*;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Host;

import java.sql.Timestamp;

/**
 * This aspect takes care of the proper setting of the update fields (contacts, name servers & domain).
 *
 * @author Patrycja Wegrzynowicz
 */
public class DomainUpdateAdvisor {

    private DiffConfiguration configuration;

    public DomainUpdateAdvisor(DiffConfiguration configuration) {
        CheckTool.checkNull(configuration, "diff configuration");
        this.configuration = configuration;
    }

    public Object setUpdateDate(ProceedingJoinPoint pjp) throws Throwable {
        Domain modifiedDomain = (Domain) pjp.getArgs()[0];
        String modifiedBy = (String) pjp.getArgs()[1];
        DomainManager domainManager = (DomainManager) pjp.getTarget();
        CheckTool.checkNull(modifiedDomain, "modified domain");

        Domain currentDomain = domainManager.get(modifiedDomain.getObjId());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Object retVal = pjp.proceed();
        ObjectChange change = (ObjectChange) ChangeDetector.diff(currentDomain, modifiedDomain, configuration);
        if (change != null) {
            if (change.containsFieldChange(DomainDiffConfiguration.ADMIN_CONTACT)) {
                Contact contact = modifiedDomain.getAdminContact();
                contact.setModified(now);
                contact.setModifiedBy(modifiedBy);
            }
            if (change.containsFieldChange(DomainDiffConfiguration.TECH_CONTACT)) {
                Contact contact = modifiedDomain.getTechContact();
                contact.setModified(now);
                contact.setModifiedBy(modifiedBy);
            }
            if (change.containsFieldChange(DomainDiffConfiguration.SPONSORING_ORG)) {
                Contact contact = modifiedDomain.getSupportingOrg();
                contact.setModified(now);
                contact.setModifiedBy(modifiedBy);
            }
            if (change.containsFieldChange(DomainDiffConfiguration.NAME_SERVERS)) {
                CollectionChange nsChange = (CollectionChange) change.getFieldChange(DomainDiffConfiguration.NAME_SERVERS);
                for (Change singleChange : nsChange.getAdded()) {
                    ObjectChange objectChange = (ObjectChange) singleChange;
                    if (objectChange.isModification()) {
                        Host host = modifiedDomain.getNameServer(objectChange.getId());
                        if (host != null) {
                            host.setModified(now);
                            host.setModifiedBy(modifiedBy);
                        }
                    }
                }
                for (Change singleChange : nsChange.getModified()) {
                    ObjectChange objectChange = (ObjectChange) singleChange;
                    Host host = modifiedDomain.getNameServer(objectChange.getId());
                    if (host != null) {
                        host.setModified(now);
                        host.setModifiedBy(modifiedBy);
                    }
                }
            }
        }
        modifiedDomain.setModified(now);
        modifiedDomain.setModifiedBy(modifiedBy);
        return retVal;
    }
}
