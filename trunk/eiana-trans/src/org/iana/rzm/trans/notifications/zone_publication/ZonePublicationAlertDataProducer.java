package org.iana.rzm.trans.notifications.zone_publication;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.default_producer.DefaultDataProducer;
import org.iana.rzm.trans.notifications.producer.DataProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ZonePublicationAlertDataProducer extends DefaultDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> retValues = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        retValues.put("domainName", td.getCurrentDomain().getName());
        retValues.put("period", (String) dataSource.get("period"));

        return retValues;
    }
}
