package org.iana.rzm.trans.change;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectConfiguration;
import org.iana.rzm.domain.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainDiffConfiguration extends DiffConfiguration {

    public DomainDiffConfiguration(HostManager hostManager) {

        ObjectConfiguration domainConfig = new ObjectConfiguration(new String[]{
            "supportingOrg", "adminContact", "techContact", "whoisServer", "registryUrl", "nameServers", "status"
        }, "name");
        domainConfig.addFieldClass("supportingOrg", Contact.class);
        domainConfig.addFieldClass("adminContact", Contact.class);
        domainConfig.addFieldClass("techContact", Contact.class);
        domainConfig.addFieldInstantiator("nameServers", new HostInstantiator(hostManager));
        addObjectConfiguration(Domain.class, domainConfig);
        addSimpleClass(Domain.Status.class);

        ObjectConfiguration contactConfig = new ObjectConfiguration(new String[]{
            "name", "organization", "jobTitle", "address", "phoneNumber", "altPhoneNumber", "faxNumber", "altFaxNumber", "email", "privateEmail", "role"
        }, "name");
        contactConfig.addFieldClass("address", Address.class);
        addObjectConfiguration(Contact.class, contactConfig);

        ObjectConfiguration addressConfig = new ObjectConfiguration(new String[]{
            "textAddress", "countryCode"
        }, "textAddress");
        addObjectConfiguration(Address.class, addressConfig);

        ObjectConfiguration hostConfig = new ObjectConfiguration(new String[]{
            "name", "addresses"
        }, "name");
        hostConfig.addFieldInstantiator("addresses", new IPAddressInstantiator());
        hostConfig.setInstantiator(new HostInstantiator(hostManager));
        addObjectConfiguration(Host.class, hostConfig);

        ObjectConfiguration ipAddressConfig = new ObjectConfiguration( new String[] {
            "address", "type"
        }, "address");
        addObjectConfiguration(IPv4Address.class, ipAddressConfig);
        addObjectConfiguration(IPv6Address.class, ipAddressConfig);
    }
}
