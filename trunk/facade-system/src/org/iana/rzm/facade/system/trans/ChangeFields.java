package org.iana.rzm.facade.system.trans;

/**
 * The constants that denote the names of object fields being changed.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface ChangeFields {

    public static final String SO_NAME = "supportingOrg.name";

    public static final String SO_ORG = "supportingOrg.organization";

    public static final String SO_ADDRESS = "supportingOrg.address.textAddress";

    public static final String SO_CC = "supportingOrg.address.countryCode";

    public static final String SO_PHONENUMBER = "supportingOrg.phoneNumber";

    public static final String SO_FAXNUMBER = "supportingOrg.faxNumber";

    public static final String SO_EMAIL = "supportingOrg.email";

    public static final String SO_ROLE = "supportingOrg.role";

    public static final String AC_NAME = "adminContacts.name";

    public static final String AC_ORG = "adminContacts.organization";

    public static final String AC_ADDRESS = "adminContacts.address.textAddress";

    public static final String AC_CC = "adminContacts.address.countryCode";

    public static final String AC_PHONENUMBER = "adminContacts.phoneNumber";

    public static final String AC_FAXNUMBER = "adminContacts.faxNumber";

    public static final String AC_EMAIL = "adminContacts.email";

    public static final String AC_ROLE = "adminContacts.role";

    public static final String TC_NAME = "techContacts.name";

    public static final String TC_ORG = "techContacts.organization";

    public static final String TC_ADDRESS = "techContacts.address.textAddress";

    public static final String TC_CC = "techContacts.address.countryCode";

    public static final String TC_PHONENUMBER = "techContacts.phoneNumber";

    public static final String TC_FAXNUMBER = "techContacts.faxNumber";

    public static final String TC_EMAIL = "techContacts.email";

    public static final String TC_ROLE = "techContacts.role";

    public static final String NS_NAME = "nameServers.name";

    public static final String NS_IP = "nameServers.address.address";

    public static final String NS_IPTYPE = "nameServers.address.type";

    public static final String WHOIS = "whoisServer";

    public static final String REGISTRY_URL = "registryUrl";
    
}
