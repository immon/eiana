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
    private static Map<String, String> values = new HashMap<String, String>();

    static {
        values.put("supportingOrg.name", "Name: ");

        values.put("supportingOrg.jobTitle", "Job Title: ");

        values.put("supportingOrg.organization", "Organization: ");

        values.put("supportingOrg.address.textAddress", "Address: ");

        values.put("supportingOrg.address.countryCode", "Country code: ");

        values.put("supportingOrg.phoneNumber", "Phone Number: ");

        values.put("supportingOrg.altPhoneNumber", "Alternative Phone Number: ");

        values.put("supportingOrg.faxNumber", "Fax Number: ");

        values.put("supportingOrg.altFaxNumber", "Alternative Fax Number");

        values.put("supportingOrg.email", "Public Email: ");

        values.put("supportingOrg.privateEmail", "Private Email: ");

        values.put("supportingOrg.role", "Role: ");

        values.put("adminContact.name", "Name: ");

        values.put("adminContact.jobTitle", "Job Title: ");

        values.put("adminContact.organization", "Organization: ");

        values.put("adminContact.address.textAddress", "Address: ");

        values.put("adminContact.address.countryCode", "Country Code: ");

        values.put("adminContact.phoneNumber", "Phone Number: ");

        values.put("adminContact.altPhoneNumber", "Alternative Phone Number: ");

        values.put("adminContact.faxNumber", "Fax Number: ");

        values.put("adminContact.altFaxNumber", "Alternative Fax Number: ");

        values.put("adminContact.email", "Public Email: ");

        values.put("adminContact.privateEmail", "Private Email: ");

        values.put("adminContact.role", "Role: ");

        values.put("techContact.name", "Name: ");

        values.put("techContact.jobTitle", "Job Title: ");

        values.put("techContact.organization", "Organization: ");

        values.put("techContact.address.textAddress", "Address: ");

        values.put("techContact.address.countryCode", "Country Code: ");

        values.put("techContact.phoneNumber", "Phone Number: ");

        values.put("techContact.altPhoneNumber", "Alternative Phone Number: ");

        values.put("techContact.faxNumber", "Fax Number: ");

        values.put("techContact.altFaxNumber", "Alternative Fax Number: ");

        values.put("techContact.email", "Public Email: ");

        values.put("techContact.privateEmail", "Private Email: ");

        values.put("techContact.role", "Role: ");

        values.put("nameServers.name", "Name: ");

        values.put("nameServers.addresses.address", "IP address: ");

        values.put("nameServers.addresses.type", "Type: ");

        values.put("whoisServer", "WHOIS Server: ");

        values.put("registryUrl", "Registry URL: ");

        values.put("adminContact", "Administrative Contact");
        values.put("techContact", "Technical Contact");
        values.put("supportingOrg", "Sponsoring Organization");

        values.put("adminContact.address", "Address");
        values.put("techContact.address", "Address");
        values.put("supportingOrg.address", "Address");

        values.put("nameServers", "Name Server");
        values.put("nameServers.addresses", "IP Addresses");

    }

    public static String print(ObjectChange domainChange) {
        StringBuffer buffer = new StringBuffer();
        Map<String, Change> changes = domainChange.getFieldChanges();
        buffer.append(printChangeMap(changes, null));
        return buffer.toString();
    }

    private static String printChangeMap(Map<String, Change> changes, String prefix) {
        StringBuffer buffer = new StringBuffer();
        prefix = prefix == null ? "" : prefix + ".";
        for (String key : changes.keySet()) {
            Change change = changes.get(key);
            buffer.append(printChange(change, prefix + key));
        }
        return buffer.toString();
    }

    private static String printChange(Change change, String key) {
        StringBuffer buffer = new StringBuffer();
        if (change instanceof SimpleChange) {
            SimpleChange simple = (SimpleChange) change;
            buffer.append(printSimpleChange(simple, key));
        } else {
            if (change instanceof ObjectChange) {
                ObjectChange objectChange = (ObjectChange) change;
                buffer.append(getValue(key));
                buffer.append("\n");
                buffer.append(printChangeMap(objectChange.getFieldChanges(), key));
            } else if (change instanceof CollectionChange) {
                CollectionChange collChange = (CollectionChange) change;
                if (collChange.isAddition()) buffer.append("[ADDED] ");
                else if (collChange.isRemoval()) buffer.append("[REMOVED] ");
                else if (collChange.isModification()) buffer.append("[MODIFIED] ");
                for (Change c : collChange.getAdded())
                    buffer.append(printChange(c, key));
                for (Change c : collChange.getRemoved())
                    buffer.append(printChange(c, key));
                for (Change c : collChange.getModified())
                    buffer.append(printChange(c, key));
            }
        }
        return buffer.toString();
    }

    private static String printSimpleChange(SimpleChange change, String key) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getValue(key));
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
        return buffer.toString();
    }

    private static String getValue(String key) {
        String ret = values.get(key);
        return ret == null ? key : ret;
    }
}
