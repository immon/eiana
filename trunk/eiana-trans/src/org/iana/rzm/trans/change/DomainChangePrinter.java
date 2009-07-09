package org.iana.rzm.trans.change;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class DomainChangePrinter {
    private static Map<String, String> dbValues = new HashMap<String, String>();
    private static Map<String, String> nsValues = new HashMap<String, String>();
    private static Map<String, String> allValues = new HashMap<String, String>();

    static {
        dbValues.put("supportingOrg.name", "Name: ");

        dbValues.put("supportingOrg.jobTitle", "Job Title: ");

        dbValues.put("supportingOrg.organization", "Organization: ");

        dbValues.put("supportingOrg.address.textAddress", "Address: ");

        dbValues.put("supportingOrg.address.countryCode", "Country code: ");

        dbValues.put("supportingOrg.phoneNumber", "Phone Number: ");

        dbValues.put("supportingOrg.altPhoneNumber", "Alternative Phone Number: ");

        dbValues.put("supportingOrg.faxNumber", "Fax Number: ");

        dbValues.put("supportingOrg.altFaxNumber", "Alternative Fax Number");

        dbValues.put("supportingOrg.email", "Public Email: ");

        dbValues.put("supportingOrg.privateEmail", "Private Email: ");

        dbValues.put("supportingOrg.role", "Role: ");

        dbValues.put("adminContact.name", "Name: ");

        dbValues.put("adminContact.jobTitle", "Job Title: ");

        dbValues.put("adminContact.organization", "Organization: ");

        dbValues.put("adminContact.address.textAddress", "Address: ");

        dbValues.put("adminContact.address.countryCode", "Country Code: ");

        dbValues.put("adminContact.phoneNumber", "Phone Number: ");

        dbValues.put("adminContact.altPhoneNumber", "Alternative Phone Number: ");

        dbValues.put("adminContact.faxNumber", "Fax Number: ");

        dbValues.put("adminContact.altFaxNumber", "Alternative Fax Number: ");

        dbValues.put("adminContact.email", "Public Email: ");

        dbValues.put("adminContact.privateEmail", "Private Email: ");

        dbValues.put("adminContact.role", "Role: ");

        dbValues.put("techContact.name", "Name: ");

        dbValues.put("techContact.jobTitle", "Job Title: ");

        dbValues.put("techContact.organization", "Organization: ");

        dbValues.put("techContact.address.textAddress", "Address: ");

        dbValues.put("techContact.address.countryCode", "Country Code: ");

        dbValues.put("techContact.phoneNumber", "Phone Number: ");

        dbValues.put("techContact.altPhoneNumber", "Alternative Phone Number: ");

        dbValues.put("techContact.faxNumber", "Fax Number: ");

        dbValues.put("techContact.altFaxNumber", "Alternative Fax Number: ");

        dbValues.put("techContact.email", "Public Email: ");

        dbValues.put("techContact.privateEmail", "Private Email: ");

        dbValues.put("techContact.role", "Role: ");

        dbValues.put("whoisServer", "WHOIS Server: ");

        dbValues.put("registryUrl", "Registry URL: ");

        dbValues.put("adminContact", "Administrative Contact");
        dbValues.put("techContact", "Technical Contact");
        dbValues.put("supportingOrg", "Sponsoring Organization");

        dbValues.put("adminContact.address", "Address");
        dbValues.put("techContact.address", "Address");
        dbValues.put("supportingOrg.address", "Address");



        nsValues.put("nameServers.name", "Name: ");
        nsValues.put("nameServers.addresses.address", "IP address: ");

        nsValues.put("nameServers.addresses.type", "Type: ");

        nsValues.put("nameServers", "Name Server");
        nsValues.put("nameServers.addresses", "IP Addresses");

        allValues.putAll(dbValues);
        allValues.putAll(nsValues);
    }

    public static String print(ObjectChange domainChange) {
        return print(domainChange, allValues);
    }

    public static String printNsChage(ObjectChange domainChange) {
        return print(domainChange, nsValues);
    }

    public static String printDbChange(ObjectChange domainChange) {
        return print(domainChange, dbValues);
    }

    private static String print(ObjectChange domainChange, Map<String, String> values ) {
        StringBuffer buffer = new StringBuffer();
        Map<String, Change> changes = domainChange.getFieldChanges();
        buffer.append(printChangeMap(changes, null, values));
        return buffer.toString();
    }


    private static String printChangeMap(Map<String, Change> changes, String prefix, Map<String, String> values) {
        StringBuffer buffer = new StringBuffer();
        prefix = prefix == null ? "" : prefix + ".";
        for (String key : changes.keySet()) {
            Change change = changes.get(key);
            buffer.append(printChange(change, prefix + key, values));
        }
        return buffer.toString();
    }

    private static String printChange(Change change, String key, Map<String, String> values) {
        StringBuffer buffer = new StringBuffer();
        if (change instanceof SimpleChange) {
            SimpleChange simple = (SimpleChange) change;
            buffer.append(printSimpleChange(simple, key, values));
        } else {
            if (change instanceof ObjectChange) {
                ObjectChange objectChange = (ObjectChange) change;
                String value = getValue(key, values);
                if (value != null) {
                    buffer.append(value);
                    buffer.append("\n");
                    buffer.append(printChangeMap(objectChange.getFieldChanges(), key, values));
                }
            } else if (change instanceof CollectionChange) {
                CollectionChange collChange = (CollectionChange) change;
                if (collChange.isAddition()) buffer.append("[ADDED] ");
                else if (collChange.isRemoval()) buffer.append("[REMOVED] ");
                else if (collChange.isModification()) buffer.append("[MODIFIED] ");
                for (Change c : collChange.getAdded())
                    buffer.append(printChange(c, key, values));
                for (Change c : collChange.getRemoved())
                    buffer.append(printChange(c, key, values));
                for (Change c : collChange.getModified())
                    buffer.append(printChange(c, key, values));
            }
        }
        return buffer.toString();
    }

    private static String printSimpleChange(SimpleChange change, String key, Map<String, String> values) {
        StringBuffer buffer = new StringBuffer();
        String value = getValue(key, values);
        if (value != null) {
            buffer.append(value);
            if (change.isAddition()) {
                buffer.append("[added new value] ");
                buffer.append(change.getNewValue());
                buffer.append("\n");
            } else if (change.isRemoval()) {
                buffer.append("[removed value] ");
                buffer.append(change.getOldValue());
                buffer.append("\n");
            } else {
                buffer.append("[old value] ");
                buffer.append(change.getOldValue());
                buffer.append(" [updated to] ");
                buffer.append(change.getNewValue());
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    private static String getValue(String key, Map<String, String> values) {
        String ret = values.get(key);
        return ret == null ? null : ret;
    }
}
