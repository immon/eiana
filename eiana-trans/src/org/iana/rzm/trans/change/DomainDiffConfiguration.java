package org.iana.rzm.trans.change;

import org.iana.rzm.domain.*;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectConfiguration;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainDiffConfiguration extends DiffConfiguration {

    public DomainDiffConfiguration(HostManager hostManager) {

        ObjectConfiguration domainConfig = new ObjectConfiguration(new String[]{
            "supportingOrg", "adminContacts", "techContacts", "whoisServer", "registryUrl", "nameServers"
        }, "name");
        domainConfig.addFieldClass("supportingOrg", Contact.class);
        domainConfig.addFieldClass("adminContacts", Contact.class);
        domainConfig.addFieldClass("techContacts", Contact.class);
        domainConfig.addFieldInstantiator("nameServers", new HostInstantiator(hostManager));
        addObjectConfiguration(Domain.class, domainConfig);

        ObjectConfiguration contactConfig = new ObjectConfiguration(new String[]{
            "name", "addresses", "phoneNumbers", "faxNumbers", "emails", "role"
        }, "name");
        contactConfig.addFieldClass("addresses", Address.class);
        addObjectConfiguration(Contact.class, contactConfig);

        ObjectConfiguration addressConfig = new ObjectConfiguration(new String[]{
            "textAddress", "countryCode"
        }, "id");
        addObjectConfiguration(Address.class, addressConfig);

        ObjectConfiguration hostConfig = new ObjectConfiguration(new String[]{
            "name", "addresses", "numDelegations"
        }, "name");
        hostConfig.addFieldInstantiator("addresses", new IPAddressInstantiator());
        addObjectConfiguration(Host.class, hostConfig);

        ObjectConfiguration ipAddressConfig = new ObjectConfiguration( new String[] {
            "address", "type"
        }, "address");
        addObjectConfiguration(IPv4Address.class, ipAddressConfig);
        addObjectConfiguration(IPv6Address.class, ipAddressConfig);
    }
}
