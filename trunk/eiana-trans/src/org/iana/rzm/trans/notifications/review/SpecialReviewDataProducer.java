package org.iana.rzm.trans.notifications.review;

import org.iana.notifications.producers.DataProducer;
import org.iana.rzm.trans.TransactionData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class SpecialReviewDataProducer implements DataProducer {

    public Map<String, String> getValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        values.put("domainName", td.getCurrentDomain().getName());
        

        return values;
    }
}
