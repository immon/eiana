package org.iana.rzm.trans.notifications.default_producer;

import org.iana.notifications.producers.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.*;

import java.util.*;
import java.sql.Timestamp;

/**
 * @author Piotr Tkaczyk
 */
public class DefaultTransactionDataProducer implements DataProducer {

    public Map<String, String> getValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        Domain currentDomain = td.getCurrentDomain();
        values.put("domainName", currentDomain.getFqdnName());
        values.put("ticket", td.getTicketID().toString());
        String period = (String) dataSource.get("period");
        if (period != null && period.trim().length() > 0) {
            long time = td.getCreated().getTime();
            long intPeriod = Integer.parseInt(period);
            time += (intPeriod * 24 * 60 * 60 * 1000);
            values.put("requestDate", String.valueOf(time));
        } else {
            values.put("requestDate", String.valueOf(td.getCreated().getTime()));
        }
        values.put("confirmationDate", td.getStateEndDate(TransactionState.Name.PENDING_CONTACT_CONFIRMATION));
        values.put("docVrsnDate", td.getStateStartDate(TransactionState.Name.PENDING_USDOC_APPROVAL));
        values.put("implementationDate", td.getStateEndDate(TransactionState.Name.PENDING_USDOC_APPROVAL));
        values.put("serialNumber", ""); // todo
        values.put("reason", td.getStateMessage());
        values.putAll(getSpecificValuesMap(dataSource));
        return values;
    }

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        return new HashMap<String, String>();
    }
}
