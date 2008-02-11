package org.iana.rzm.trans.notifications.zone_insertion;

import org.iana.notifications.refactored.producers.DataProducer;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.default_producer.DefaultTransactionDataProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ZoneInsertionAlertDataProducer extends DefaultTransactionDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> retValues = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        retValues.put("domainName", td.getCurrentDomain().getName());
        retValues.put("period", (String) dataSource.get("period"));

        return retValues;
    }
}
