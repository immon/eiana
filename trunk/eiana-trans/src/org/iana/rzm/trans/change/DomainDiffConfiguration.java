package org.iana.rzm.trans.change;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.Domain;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainDiffConfiguration extends DiffConfiguration {

    private DomainDiffConfiguration() {

        ObjectConfiguration domainConfig = new ObjectConfiguration(new String[]{
            "supportingOrg", "adminContacts", "techContacts", "whoisServer", "registryUrl"
        }, "name");
        domainConfig.addFieldClass("supportingOrg", Contact.class);
        domainConfig.addFieldClass("adminContacts", Contact.class);
        domainConfig.addFieldClass("techContacts", Contact.class);
        addObjectConfiguration(Domain.class, domainConfig);

        ObjectConfiguration contactConfig = new ObjectConfiguration(new String[]{
            "name", "addresses", "phoneNumbers", "faxNumbers", "emails", "role"
        }, "name");
        contactConfig.addFieldClass("addresses", Address.class);
        addObjectConfiguration(Contact.class, contactConfig);

        ObjectConfiguration addressConfig = new ObjectConfiguration(new String[]{
            "street", "city", "postalCode", "countryCode", "state"
        }, "id");
        addObjectConfiguration(Address.class, addressConfig);
    }

    private static DiffConfiguration instance = new DomainDiffConfiguration();

    public static DiffConfiguration getInstance() {
        return instance;
    }
}
