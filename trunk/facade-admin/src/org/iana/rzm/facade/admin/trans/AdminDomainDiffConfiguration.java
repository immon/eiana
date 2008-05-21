package org.iana.rzm.facade.admin.trans;

import org.iana.objectdiff.ObjectConfiguration;
import org.iana.objectdiff.ObjectInstantiator;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.change.DomainDiffConfiguration;

/**
 * The configuration to detect changes in a domain object made via the admin interface.
 * It adds the following additional fields: type, description, specialInstructions, sendEmail.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class AdminDomainDiffConfiguration extends DomainDiffConfiguration {

    public AdminDomainDiffConfiguration(ObjectInstantiator hostInstantiator) {
        super(hostInstantiator);
        ObjectConfiguration config = super.getObjectConfig(Domain.class);
        config.addField("type");
        config.addField("description");
        config.addField("specialInstructions");
        config.addField("enableEmails");
    }
}
