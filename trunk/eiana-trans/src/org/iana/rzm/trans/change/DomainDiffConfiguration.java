package org.iana.rzm.trans.change;

import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectConfiguration;
import org.iana.objectdiff.ObjectInstantiator;
import org.iana.rzm.domain.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DomainDiffConfiguration extends DiffConfiguration {

    public static final String TECH_CONTACT = "techContact";
    public static final String ADMIN_CONTACT = "adminContact";
    public static final String SPONSORING_ORG = "supportingOrg";
    public static final String NAME_SERVERS = "nameServers";

    public DomainDiffConfiguration(ObjectInstantiator hostInstantiator) {
        ObjectConfiguration domainConfig = new ObjectConfiguration(new String[]{
                SPONSORING_ORG, ADMIN_CONTACT, TECH_CONTACT, "whoisServer", "registryUrl", NAME_SERVERS, "status", "specialReview"
        }, "name");
        domainConfig.addFieldClass(SPONSORING_ORG, Contact.class);
        domainConfig.addFieldClass(ADMIN_CONTACT, Contact.class);
        domainConfig.addFieldClass(TECH_CONTACT, Contact.class);
        domainConfig.addFieldInstantiator(NAME_SERVERS, hostInstantiator);
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
        hostConfig.setInstantiator(hostInstantiator);
        addObjectConfiguration(Host.class, hostConfig);

        ObjectConfiguration ipAddressConfig = new ObjectConfiguration( new String[] {
            "address", "type"
        }, "address");
        addObjectConfiguration(IPv4Address.class, ipAddressConfig);
        addObjectConfiguration(IPv6Address.class, ipAddressConfig);
    }
}
