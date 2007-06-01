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

    public static final String AC_NAME = "adminContact.name";

    public static final String AC_ORG = "adminContact.organization";

    public static final String AC_ADDRESS = "adminContact.address.textAddress";

    public static final String AC_CC = "adminContact.address.countryCode";

    public static final String AC_PHONENUMBER = "adminContact.phoneNumber";

    public static final String AC_FAXNUMBER = "adminContact.faxNumber";

    public static final String AC_EMAIL = "adminContact.email";

    public static final String AC_ROLE = "adminContact.role";

    public static final String TC_NAME = "techContact.name";

    public static final String TC_ORG = "techContact.organization";

    public static final String TC_ADDRESS = "techContact.address.textAddress";

    public static final String TC_CC = "techContact.address.countryCode";

    public static final String TC_PHONENUMBER = "techContact.phoneNumber";

    public static final String TC_FAXNUMBER = "techContact.faxNumber";

    public static final String TC_EMAIL = "techContact.email";

    public static final String TC_ROLE = "techContact.role";

    public static final String NS_NAME = "nameServers.name";

    public static final String NS_IP = "nameServers.address.address";

    public static final String NS_IPTYPE = "nameServers.address.type";

    public static final String WHOIS = "whoisServer";

    public static final String REGISTRY_URL = "registryUrl";
    
}
