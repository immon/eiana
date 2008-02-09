package org.iana.rzm.facade.admin.trans;

import org.iana.rzm.trans.change.DomainDiffConfiguration;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.domain.Domain;
import org.iana.objectdiff.ObjectConfiguration;

/**
 * The configuration to detect changes in a domain object made via the admin interface.
 * It adds the following additional fields: type, description, specialInstructions, sendEmail.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class AdminDomainDiffConfiguration extends DomainDiffConfiguration {

    public AdminDomainDiffConfiguration(HostManager hostManager) {
        super(hostManager);
        ObjectConfiguration config = super.getObjectConfig(Domain.class);
        config.addField("type");
        config.addField("description");
        config.addField("specialInstructions");
        config.addField("enableEmails");
    }
}
