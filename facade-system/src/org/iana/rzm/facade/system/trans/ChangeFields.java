package org.iana.rzm.facade.system.trans;

/**
 * The constants that denote the names of object fields being changed.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface ChangeFields {

    public static final String SO_NAME = "supportingOrg.name";

    public static final String SO_ORG = "supportingOrg.organization";

    public static final String SO_ADDRESS = "supportingOrg.addresses.textAddress";

    public static final String SO_CC = "supportingOrg.addresses.countryCode";

    public static final String SO_PHONENUMBER = "supportingOrg.phoneNumbers";

    public static final String SO_FAXNUMBER = "supportingOrg.faxNumbers";

    public static final String SO_EMAIL = "supportingOrg.emails";

    public static final String SO_ROLE = "supportingOrg.role";

    public static final String AC_NAME = "adminContacts.name";

    public static final String AC_ORG = "adminContacts.organization";

    public static final String AC_ADDRESS = "adminContacts.addresses.textAddress";

    public static final String AC_CC = "adminContacts.addresses.countryCode";

    public static final String AC_PHONENUMBER = "adminContacts.phoneNumbers";

    public static final String AC_FAXNUMBER = "adminContacts.faxNumbers";

    public static final String AC_EMAIL = "adminContacts.emails";

    public static final String AC_ROLE = "adminContacts.role";

    public static final String TC_NAME = "techContacts.name";

    public static final String TC_ORG = "techContacts.organization";

    public static final String TC_ADDRESS = "techContacts.addresses.textAddress";

    public static final String TC_CC = "techContacts.addresses.countryCode";

    public static final String TC_PHONENUMBER = "techContacts.phoneNumbers";

    public static final String TC_FAXNUMBER = "techContacts.faxNumbers";

    public static final String TC_EMAIL = "techContacts.emails";

    public static final String TC_ROLE = "techContacts.role";

    public static final String NS_NAME = "nameServers.name";

    public static final String NS_IP = "nameServers.addresses.address";

    public static final String NS_IPTYPE = "nameServers.addresses.type";

    public static final String WHOIS = "whoisServer";

    public static final String REGISTRY_URL = "registryUrl";
    
}
