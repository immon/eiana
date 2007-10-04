package org.iana.rzm.facade.system.trans.vo.changes;

/**
 * The constants that denote the names of object fields being changed in a domain object.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface ChangeFields {

    public static final String SO_NAME = "supportingOrg.name";

    public static final String SO_JOB_TITLE = "supportingOrg.jobTitle";

    public static final String SO_ORG = "supportingOrg.organization";

    public static final String SO_ADDRESS = "supportingOrg.address.textAddress";

    public static final String SO_CC = "supportingOrg.address.countryCode";

    public static final String SO_PHONENUMBER = "supportingOrg.phoneNumber";

    public static final String SO_ALTPHONENUMBER = "supportingOrg.altPhoneNumber";

    public static final String SO_FAXNUMBER = "supportingOrg.faxNumber";

    public static final String SO_ALTFAXNUMBER = "supportingOrg.altFaxNumber";

    public static final String SO_EMAIL = "supportingOrg.email";

    public static final String SO_PRIVATE_EMAIL = "supportingOrg.privateEmail";

    public static final String SO_ROLE = "supportingOrg.role";

    public static final String AC_NAME = "adminContact.name";

    public static final String AC_JOB_TITLE = "adminContact.jobTitle";

    public static final String AC_ORG = "adminContact.organization";

    public static final String AC_ADDRESS = "adminContact.address.textAddress";

    public static final String AC_CC = "adminContact.address.countryCode";

    public static final String AC_PHONENUMBER = "adminContact.phoneNumber";

    public static final String AC_ALTPHONENUMBER = "adminContact.altPhoneNumber";

    public static final String AC_FAXNUMBER = "adminContact.faxNumber";

    public static final String AC_ALTFAXNUMBER = "adminContact.altFaxNumber";

    public static final String AC_EMAIL = "adminContact.email";

    public static final String AC_PRIVATE_EMAIL = "adminContact.privateEmail";

    public static final String AC_ROLE = "adminContact.role";

    public static final String TC_NAME = "techContact.name";

    public static final String TC_JOB_TITLE = "techContact.jobTitle" ;

    public static final String TC_ORG = "techContact.organization";

    public static final String TC_ADDRESS = "techContact.address.textAddress";

    public static final String TC_CC = "techContact.address.countryCode";

    public static final String TC_PHONENUMBER = "techContact.phoneNumber";

    public static final String TC_ALTPHONENUMBER = "techContact.altPhoneNumber";

    public static final String TC_FAXNUMBER = "techContact.faxNumber";

    public static final String TC_ALTFAXNUMBER = "techContact.altFaxNumber";

    public static final String TC_EMAIL = "techContact.email";

    public static final String TC_PRIVATE_EMAIL = "techContact.privateEmail";

    public static final String TC_ROLE = "techContact.role";

    public static final String NS_NAME = "nameServers.name";

    public static final String NS_IP = "nameServers.address.address";
    
    public static final String NS_IPS = "nameServers.addresses.address";

    public static final String NS_IPTYPE = "nameServers.address.type";

    public static final String WHOIS = "whoisServer";

    public static final String REGISTRY_URL = "registryUrl";

}
