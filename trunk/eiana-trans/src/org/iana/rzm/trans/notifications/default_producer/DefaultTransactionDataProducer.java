package org.iana.rzm.trans.notifications.default_producer;

import org.iana.notifications.producers.DataProducer;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionState;

import java.util.HashMap;
import java.util.Map;

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
        values.put("requestDate", String.valueOf(td.getCreated().getTime()));
        values.put("confirmationDate", td.getStateEndDate(TransactionState.Name.PENDING_CONTACT_CONFIRMATION));
        values.put("docVrsnDate", td.getStateStartDate(TransactionState.Name.PENDING_USDOC_APPROVAL));
        values.put("implementationDate", td.getStateEndDate(TransactionState.Name.PENDING_USDOC_APPROVAL));
        values.put("serialNumber", ""); // todo
        values.putAll(getSpecificValuesMap(dataSource));
        return values;
    }

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        return new HashMap<String, String>();
    }
}
