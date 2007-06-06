package org.iana.rzm.trans.collectors;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.trans.TransactionData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public abstract class AbstractNotificationDataCollector implements NotificationDataCollector {
    private static Map<String, String> values = new HashMap<String, String>();

    static {
        values.put("adminContact", "Administrative Contact");
        values.put("techContact", "Technical Contact");
        values.put("supportingOrg", "Supporting Organization");

        values.put("nameServers", "Name Server");
        values.put("addresses", "IP Addresses");
        values.put("address", "IP Address:   ");

        values.put("registryUrl", "Registry URL: ");
        values.put("whoisServer", "Whois Server: ");

        values.put("jobTitle", "Job Title:    ");
        values.put("email", "Public Email: ");
        values.put("name", "Name:         ");
        values.put("role", "Role:         ");
        values.put("faxNumber", "Fax Number:   ");
        values.put("organization", "Organization: ");
        values.put("phoneNumber", "Phone Number: ");
    }

    protected String getChanges(TransactionData td) {
        StringBuffer buffer = new StringBuffer();
        Map<String, Change> changes = td.getDomainChange().getFieldChanges();
        buffer.append(printChangeMap(changes));
        return buffer.toString();
    }

    private String printChangeMap(Map<String, Change> changes) {
        StringBuffer buffer = new StringBuffer();
        for (String key : changes.keySet()) {
            Change change = changes.get(key);
            buffer.append(pringChange(change, key));
        }
        return buffer.toString();
    }

    private String pringChange(Change change, String key) {
        StringBuffer buffer = new StringBuffer();
        if (change instanceof SimpleChange) {
            SimpleChange simple = (SimpleChange) change;
            buffer.append(printSimpleChange(simple, key));
        } else {
            if (change instanceof ObjectChange) {
                ObjectChange objectChange = (ObjectChange) change;
                buffer.append(values.get(key));
                buffer.append("\n");
                buffer.append(printChangeMap(objectChange.getFieldChanges()));
            } else if (change instanceof CollectionChange) {
                CollectionChange collChange = (CollectionChange) change;
                if (collChange.isAddition()) {
                    buffer.append("\n[ADDED]");
                    for (Change c : collChange.getAdded())
                        buffer.append(pringChange(c, key));
                } else if (collChange.isRemoval()) {
                    buffer.append("\n[REMOVED]");
                    for (Change c : collChange.getRemoved())
                        buffer.append(pringChange(c, key));
                } else {
                    buffer.append("\n[MODIFIED]");
                    for (Change c : collChange.getModified())
                        buffer.append(pringChange(c, key));
                }
            }
        }
        return buffer.toString();
    }

    private String printSimpleChange(SimpleChange change, String key) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(values.get(key));
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
}
