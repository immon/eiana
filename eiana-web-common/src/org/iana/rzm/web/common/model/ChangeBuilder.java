package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.system.trans.vo.changes.*;

import java.util.*;

public class ChangeBuilder {

    public Change buildChange(StringValueVO vo, String name, ChangeVO.Type type) {
        ChangeDictionary changeDictionary = new ChangeDictionary();
        String field = changeDictionary.getName(name);
        Change change = new Change(field, vo.getOldValue(), vo.getNewValue(), type);
        change.setNameServer(changeDictionary.isNameServer(name));
        return change;
    }


    private static class ChangeDictionary {

        private static Map<String, String> dictionary = new HashMap<String, String>();

        private static final String CONTACT_ADDRESS = "Contact Address";
        private static final String CONTACT_COUNTRY = "Contact Country";
        private static final String CONTACT_EMAIL = "Contact Email";
        private static final String CONTACT_PRIVATE_EMAIL = "Contact Private Email";
        private static final String CONTACT_PHONE = "Contact Phone";
        private static final String CONTACT_ALT_PHONE = "Contact Alternate Phone";
        private static final String CONTACT_FAX = "Contact Fax";
        private static final String CONTACT_ALT_FAX = "Contact Alternate Fax";
        private static final String CONTACT_NAME = "Contact Name";
        private static final String CONTACT_JOB_TITLE = "Contact Job Title";
        private static final String CONTACT_ORGANISATION = "Contact Organization";
        private static final String CONTACT_ROLE = "Contact Role";
        private static final String IP_ADDRESS = "IP Address";
        private static final String HOST_NAME = "Host name";
        private static final String NAME_SERVER = "Name Server";
        private static final String WHOIS = "WHOIS";
        private static final String REGISTRY_URL = "Registry URL";

        static {
            initMap();
        }

        private static void initMap() {

            add(ChangeFields.AC_NAME, CONTACT_NAME);
            add(ChangeFields.AC_JOB_TITLE, CONTACT_JOB_TITLE);
            add(ChangeFields.AC_ORG, CONTACT_ORGANISATION);
            add(ChangeFields.AC_ADDRESS, CONTACT_ADDRESS);
            add(ChangeFields.AC_CC, CONTACT_COUNTRY);
            add(ChangeFields.AC_EMAIL, CONTACT_EMAIL);
            add(ChangeFields.AC_PRIVATE_EMAIL, CONTACT_PRIVATE_EMAIL);
            add(ChangeFields.AC_PHONENUMBER, CONTACT_PHONE);
            add(ChangeFields.AC_ALTPHONENUMBER, CONTACT_ALT_PHONE);
            add(ChangeFields.AC_FAXNUMBER, CONTACT_FAX);
            add(ChangeFields.AC_ALTFAXNUMBER, CONTACT_ALT_FAX);
            add(ChangeFields.AC_ROLE, CONTACT_ROLE);

            add(ChangeFields.TC_NAME, CONTACT_NAME);
            add(ChangeFields.TC_JOB_TITLE, CONTACT_JOB_TITLE);
            add(ChangeFields.TC_ORG, CONTACT_ORGANISATION);
            add(ChangeFields.TC_ADDRESS, CONTACT_ADDRESS);
            add(ChangeFields.TC_CC, CONTACT_COUNTRY);
            add(ChangeFields.TC_EMAIL, CONTACT_EMAIL);
            add(ChangeFields.TC_PRIVATE_EMAIL, CONTACT_PRIVATE_EMAIL);
            add(ChangeFields.TC_PHONENUMBER, CONTACT_PHONE);
            add(ChangeFields.TC_ALTPHONENUMBER, CONTACT_ALT_PHONE);
            add(ChangeFields.TC_FAXNUMBER, CONTACT_FAX);
            add(ChangeFields.TC_ALTFAXNUMBER, CONTACT_ALT_FAX);
            add(ChangeFields.TC_ROLE, CONTACT_ROLE);


            add(ChangeFields.SO_NAME, CONTACT_NAME);
            add(ChangeFields.SO_JOB_TITLE, CONTACT_JOB_TITLE);
            add(ChangeFields.SO_ORG, CONTACT_ORGANISATION);
            add(ChangeFields.SO_ADDRESS, CONTACT_ADDRESS);
            add(ChangeFields.SO_CC, CONTACT_COUNTRY);
            add(ChangeFields.SO_EMAIL, CONTACT_EMAIL);
            add(ChangeFields.SO_PRIVATE_EMAIL, CONTACT_PRIVATE_EMAIL);
            add(ChangeFields.SO_PHONENUMBER, CONTACT_PHONE);
            add(ChangeFields.SO_ALTPHONENUMBER, CONTACT_ALT_PHONE);
            add(ChangeFields.SO_FAXNUMBER, CONTACT_FAX);
            add(ChangeFields.SO_ALTFAXNUMBER, CONTACT_ALT_FAX);
            add(ChangeFields.SO_ROLE, CONTACT_ROLE);


            add(ChangeFields.NS_IP, IP_ADDRESS);
            add(ChangeFields.NS_IPS, IP_ADDRESS);
            add(ChangeFields.NS_NAME, HOST_NAME);
            add(ChangeFields.REGISTRY_URL, REGISTRY_URL);
            add(ChangeFields.WHOIS, WHOIS);
            add(ChangeFields.NAME_SERVERS, NAME_SERVER);

        }

        public boolean isNameServer(String name) {
            return name != null
                   && (name.equals(ChangeFields.NS_IP) ||
                       name.equals(ChangeFields.NS_IPS) ||
                       name.equals(ChangeFields.NS_IPTYPE) ||
                       name.equals(ChangeFields.NS_NAME) ||
                       name.equals(ChangeFields.NAME_SERVERS));

        }

        public String getName(String value) {
            if (value == null) {
                return value;
            }

            String s = dictionary.get(value);
            if (s == null) {
                return value;
            }

            return s;
        }

        private static void add(String key, String value) {
            dictionary.put(key, value);
        }
    }
}
